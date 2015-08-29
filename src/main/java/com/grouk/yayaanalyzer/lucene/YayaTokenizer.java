package com.grouk.yayaanalyzer.lucene;

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

import com.grouk.yayaanalyzer.dictionary.AssociateStream;
import com.grouk.yayaanalyzer.dictionary.WordTree;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

/**
 * Description: Extract tokens from the text stream
 * 
 * @author jolestar@gmail.com
 * @version 1.0
 * 
 */

public final class YayaTokenizer extends Tokenizer {

	private int offset = 0, bufferIndex = 0, dataLen = 0;

	private final static int MAX_WORD_LEN = 24;

	private final static int IO_BUFFER_SIZE = 10240;

	/*
	 * 将前一次io读取结果的最后 MAX_WORD_LEN 个字备份，以备回溯。
	 */
	private final char[] backBuffer = new char[MAX_WORD_LEN];

	private final char[] buffer = new char[MAX_WORD_LEN];

	private final char[] ioBuffer = new char[IO_BUFFER_SIZE];

	private int length;

	private int start;
	

	
	/**
	 * 词库中没有的词汇 
	 */
	static final int TOKEN_TYPE_UNKNOW = 0;
	
	/**
	 * 词库中的词汇
	 */
	static final int TOKEN_TYPE_WORD = 1;
	
	/**
	 * 单字词
	 */
	static final int TOKEN_TYPE_SINGLE = 2;
	
	/**
	 * 数字
	 */
	static final int TOKEN_TYPE_DIGIT = 3;
	
	/**
	 * 字母
	 */
	static final int TOKEN_TYPE_LETTER = 4;
	
	static final String[] TOKEN_TYPE_NAMES = { "unknow", "word", "single", "digit","letter"};

	private CharTermAttribute termAtt;
	private OffsetAttribute offsetAtt;
	private TypeAttribute typeAtt;
	private WordTree tree;
	private int tokenType = TOKEN_TYPE_UNKNOW;

	public YayaTokenizer(WordTree tree) {
		this.tree = tree;
		init();
	}

	private void init() {
		termAtt = addAttribute(CharTermAttribute.class);
		offsetAtt = addAttribute(OffsetAttribute.class);
		typeAtt = addAttribute(TypeAttribute.class);
	}

	private final void push(char c) {
		if (length == 0)
			start = offset - 1;
		buffer[length++] = Character.toLowerCase(c);

	}

	private final boolean flush() {
		if (length > 0) {
			termAtt.copyBuffer(buffer, 0, length);
			offsetAtt.setOffset(correctOffset(start), correctOffset(start
					+ length));
			if(tokenType == TOKEN_TYPE_UNKNOW && length == 1){
				tokenType = TOKEN_TYPE_SINGLE;
			}
			typeAtt.setType(TOKEN_TYPE_NAMES[tokenType]);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		length = 0;
		start = offset;

		// 创建联想流.模仿输入法的联想词语的功能
		AssociateStream assoStream = new AssociateStream(this.tree);
		while (true) {
			final char c;
			offset++;
			if (bufferIndex >= dataLen) {
				System.arraycopy(ioBuffer, IO_BUFFER_SIZE - MAX_WORD_LEN,
						backBuffer, 0, MAX_WORD_LEN);
				dataLen = input.read(ioBuffer);
				bufferIndex = 0;
			}

			if (dataLen == -1){
				if(!assoStream.isBegin()&&assoStream.isWordEnd()){
					tokenType = TOKEN_TYPE_WORD;
				}
				return flush();
			}else {
				if (bufferIndex < 0) {
					c = backBuffer[backBuffer.length + bufferIndex];
				} else
					c = ioBuffer[bufferIndex];
				bufferIndex++;
			}

			if (Character.isWhitespace(c)) {
				if (!assoStream.isBegin()) {
					if(assoStream.isWordEnd()){
						tokenType = TOKEN_TYPE_WORD;
					}
					assoStream.reset();
				}
				if (length > 0)
					return flush();
			} else if (Character.isDigit(c)) {
				if (!assoStream.isBegin()) {
					if(assoStream.isWordEnd()){
						tokenType = TOKEN_TYPE_WORD;
					}
					bufferIndex--;
					offset--;
					assoStream.reset();
					return flush();
				}
				tokenType = TOKEN_TYPE_DIGIT;
				push(c);
				if (length == MAX_WORD_LEN)
					return flush();

			} else if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
				if (!assoStream.isBegin()) {
					if(assoStream.isWordEnd()){
						tokenType = TOKEN_TYPE_WORD;
					}
					bufferIndex--;
					offset--;
					assoStream.reset();
					return flush();
				}
				tokenType = TOKEN_TYPE_LETTER;
				push(c);
				if (length == MAX_WORD_LEN)
					return flush();

			} else if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
				if (assoStream.isBegin() && length > 0) {
					bufferIndex--;
					offset--;
					return flush();
				}

				tokenType = TOKEN_TYPE_UNKNOW;
				if (assoStream.associate(c)) {
					push(c);
					if (!assoStream.canContinue()) {
						assoStream.reset();
						tokenType = TOKEN_TYPE_WORD;
						return flush();
					}
				} else {
					if (assoStream.isWordEnd()) {
						tokenType = TOKEN_TYPE_WORD;
						assoStream.reset();
						bufferIndex--;
						offset--;
						return flush();
					} else if (assoStream.isOccurWord()) {
						tokenType = TOKEN_TYPE_WORD;
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
			}else {
				if (length > 0){
					if(!assoStream.isBegin()){
						if (assoStream.isWordEnd()) {
							tokenType = TOKEN_TYPE_WORD;
							assoStream.reset();
						}
					}
					return flush();
				}
			}

		}
	}

	@Override
	public void reset() throws IOException {
		super.reset();
	}
}
