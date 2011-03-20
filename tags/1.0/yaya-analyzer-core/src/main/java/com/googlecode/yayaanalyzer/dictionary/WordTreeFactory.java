package com.googlecode.yayaanalyzer.dictionary;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * @author jolestar@gmail.com
 * @version 1.0
 * 
 */
public class WordTreeFactory {
	
	private static class InstanceHolder{
		static WordTree instance = createDefaultInstance();
	}
	
	public static WordTree getDefaultInstance(){
		return InstanceHolder.instance;
	}

	public static WordTree createDefaultInstance(){
		try {
			return createInstance(new InputStreamReader(
					WordTreeFactory.class.getResourceAsStream("word.txt"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new DictionaryInitException(e);
		}
	}

	public static WordTree createInstance(Reader in) {
		System.out.println("词库尚未被初始化，开始初始化词库.");
		long begin = System.currentTimeMillis();
		WordTree tree = WordTree.createDefault();
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(in);
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("#") < 0) {
					tree.addChineseWord(line);
				}
			}
		} catch (Exception e) {
			throw new DictionaryInitException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("初始化词库结束。用时:" + (end - begin) + "毫秒;");
		System.out.println("共添加" + tree.getWordCount() + "个词语。");
		return tree;
	}

	public static WordTree createInstance(File file) {
		if (!file.exists() || !file.isFile() || !file.canRead()) {
			throw new IllegalArgumentException("file " + file
					+ " is not exists or not a file.");
		}
		try {
			return createInstance(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
		} catch (IOException e) {
			throw new DictionaryInitException(e);
		}
	}

	public static WordTree createInstance(String file) {
		return createInstance(new File(file));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
