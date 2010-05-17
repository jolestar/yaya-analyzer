package com.googlecode.yayaanalyzer.lucene;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Reader;
import org.apache.lucene.analysis.*;

import com.googlecode.yayaanalyzer.dictionary.AssociateStream;
import com.googlecode.yayaanalyzer.dictionary.WordTreeFactory;

/**
 * Title: ThesaurusAnalyzer
 * Description: Extract tokens from the Stream through a dictionary
 * Copyright:   Copyright (c) 2007
 * @author Jolestar
 * @version 1.0
 *
 */

public final class YayaTokenizer extends Tokenizer {

	public YayaTokenizer(Reader in) {
		input = in;
	}

	private int offset = 0, bufferIndex = 0, dataLen = 0;

	private final static int MAX_WORD_LEN = 24;

	private final static int IO_BUFFER_SIZE = 10240;

	/*
	 *将前一次io读取结果的最后 MAX_WORD_LEN 个字备份，以备回溯。
	 */
	private final char[] backBuffer = new char[MAX_WORD_LEN];

	private final char[] buffer = new char[MAX_WORD_LEN];

	private final char[] ioBuffer = new char[IO_BUFFER_SIZE];

	private int length;

	private int start;

	private final void push(char c) {

		if (length == 0)
			start = offset - 1; 
		buffer[length++] = Character.toLowerCase(c); 

	}

	private final Token flush() {

		if (length > 0) {
			Token token = new Token(new String(buffer, 0, length), start, start
					+ length);
			return token;
		} else
			return null;
	}

	public final Token next() throws java.io.IOException {
		length = 0;
		start = offset;
		
		//创建联想流.模仿输入法的联想词语的功能
		AssociateStream assoStream = new AssociateStream(WordTreeFactory.getInstance());
		while (true) {

			final char c;
			offset++;

			if (bufferIndex >= dataLen) {
				System.arraycopy(ioBuffer, IO_BUFFER_SIZE - MAX_WORD_LEN,
						backBuffer, 0, MAX_WORD_LEN);
				dataLen = input.read(ioBuffer);
				bufferIndex = 0;
			}
			;

			if (dataLen == -1)
				return flush();
			else {
				if (bufferIndex < 0) {
					c = backBuffer[backBuffer.length + bufferIndex];
				} else
					c = ioBuffer[bufferIndex];
				bufferIndex++;
			}

			if (Character.isWhitespace(c)) {
				if (!assoStream.isBegin()) {
					assoStream.reset();
					return flush();
				}
				if (length > 0)
					return flush();
			} else if (Character.isDigit(c)) {
				if (!assoStream.isBegin()) {
					bufferIndex--;
					offset--;
					assoStream.reset();
					return flush();
				}
				push(c);
				if (length == MAX_WORD_LEN)
					return flush();

			} else if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
				if (!assoStream.isBegin()) {
					bufferIndex--;
					offset--;
					assoStream.reset();
					return flush();
				}
				push(c);
				if (length == MAX_WORD_LEN)
					return flush();

			} else if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {

				if (assoStream.isBegin() && length > 0) {
					bufferIndex--;
					offset--;
					return flush();
				}

				if (assoStream.associate(c)) {
					push(c);
					if (!assoStream.canContinue()) {
						assoStream.reset();
						return flush();
					}
				} else {
					if (assoStream.isWordEnd()) {
						assoStream.reset();
						bufferIndex--;
						offset--;
						return flush();
					} else if (assoStream.isOccurWord()) {
						assoStream.backToLastWordEnd();
						bufferIndex = bufferIndex
								- (length - assoStream.getSetp()) - 1;
						offset = offset - (length - assoStream.getSetp()) - 1;

						length = assoStream.getSetp();

						assoStream.reset();
						return flush();
					} else {
						if (length > 0) {

							bufferIndex = bufferIndex - (length - 1) - 1;
							offset = offset - (length - 1) - 1;

							length = 1;

							assoStream.reset();
							return flush();
						}

						assoStream.reset();
						push(c);
						return flush();
					}
				}
			}

			else {
				if (length > 0)
					return flush();
			}

		}

	}

}
