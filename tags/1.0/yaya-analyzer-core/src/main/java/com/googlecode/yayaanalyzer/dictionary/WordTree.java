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
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 单词树
 * @author jolestar@gmail.com
 * @version 1.0
 * 
 */
public class WordTree implements Serializable {

	private int wordCount = 0;

	private static final long serialVersionUID = 1L;

	private TreeNode root = null;

	private HashMap<Integer, Character> chineseCharMap = new HashMap<Integer, Character>();

	private WordTree() {
		root = new TreeNode();
		root.setChineseChar(' ');
	}

	static WordTree createDefault() {
		return new WordTree();
	}

	public TreeNode getRoot() {
		return this.root;
	}

	public void addChineseChar(Character chineseChar) {
		this.chineseCharMap.put(chineseChar.hashCode(), chineseChar);
	}

	public boolean containsChineseChar(int key) {
		return this.chineseCharMap.containsKey(key);
	}

	public char removeChineseChar(int key) {
		return this.chineseCharMap.remove(key);
	}

	public void addChineseWord(String word) {
		wordCount++;
		TreeNode tempNode = this.root;
		char[] charArray = word.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			TreeNode node = new TreeNode();
			node.setChineseChar(charArray[i]);
			if (!tempNode.containsChild(node)) {
				tempNode.addChild(node);
				tempNode = node;
			} else {
				tempNode = tempNode.getByChar(node.getChineseChar());
			}
		}
		tempNode.setWordEnd(true);
	}

	public int getWordCount() {
		return this.wordCount;
	}

	public boolean containsWord(String word) {
		TreeNode tempNode = this.root;
		char[] charArray = word.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			TreeNode node = new TreeNode();
			node.setChineseChar(charArray[i]);
			if (!tempNode.containsChild(node))
				return false;
			else
				tempNode = tempNode.getByChar(node.getChineseChar());
		}
		if (tempNode.isWordEnd())
			return true;
		else
			return false;

	}

	public void printTree(Writer out) throws IOException {
		out.write(this.root.toString());
		out.write(System.getProperty("line.separator"));
		printChildren(out, this.root, "");
	}

	private void printChildren(Writer out, TreeNode parent, String indent)
			throws IOException {
		Map<Integer, TreeNode> children = parent.getChildren();
		Set<Integer> keySet = children.keySet();
		Iterator<Integer> iterator = keySet.iterator();
		indent += "      ";
		while (iterator.hasNext()) {
			TreeNode child = children.get(iterator.next());
			out.write(indent);
			out.write(child.toString());
			if (child.isWordEnd())
				out.write('^');
			out.write(System.getProperty("line.separator"));
			if (child.hasChildren())
				printChildren(out, child, indent);
		}
	}

	public int countChar() {
		int count = this.root.count();
		count += countChildren(root);
		return count;
	}

	private int countChildren(TreeNode parent) {
		int count = 0;
		Map<Integer, TreeNode> children = parent.getChildren();
		Set<Integer> keySet = children.keySet();
		Iterator<Integer> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			TreeNode child = children.get(iterator.next());// iterator.next();
			count += child.count();
			if (child.hasChildren())
				count += countChildren(child);
		}
		return count;
	}

}
