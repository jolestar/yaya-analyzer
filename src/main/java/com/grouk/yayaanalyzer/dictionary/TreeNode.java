package com.grouk.yayaanalyzer.dictionary;

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
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * Description: TreeNode for WordTree Copyright: Copyright (c) 2007
 * 
 * @author jolestar@gmail.com
 * @version 1.0
 * 
 */
public class TreeNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 保存一个字
	private char chineseChar;

	// 该节点是否为一个词的结尾
	private boolean isWordEnd = false;

	// 该节点的父节点
	private TreeNode parent = null;

	private Map<Integer, TreeNode> children = new HashMap<Integer, TreeNode>();

	/**
	 * 
	 */
	public TreeNode() {
	}

	/**
	 * @param chineseCharCode
	 * @param isWordEnd
	 * @param parent
	 */
	public TreeNode(char chineseChar, boolean isWordEnd, TreeNode parent) {
		this.chineseChar = chineseChar;
		this.isWordEnd = isWordEnd;
		this.parent = parent;
	}

	/**
	 * @return the chineseChar
	 */
	public char getChineseChar() {
		return chineseChar;
	}

	/**
	 * @param chineseChar
	 *            the chineseCharCode to set
	 */
	public void setChineseChar(char chineseCharCode) {
		this.chineseChar = chineseCharCode;
	}

	/**
	 * @return the isWordEnd
	 */
	public boolean isWordEnd() {
		return isWordEnd;
	}

	/**
	 * @param isWordEnd
	 *            the isWordEnd to set
	 */
	public void setWordEnd(boolean isWordEnd) {
		this.isWordEnd = isWordEnd;
	}

	/**
	 * @return the parent
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public void addChild(TreeNode child) {
		child.parent = this;
		this.children.put(child.hashCode(), child);
	}

	public boolean hasChildren() {
		return this.children.size() == 0 ? false : true;
	}

	public TreeNode getByChar(char chineseChar) {

		return this.children.get(new Integer(chineseChar));
	}

	public boolean containsChild(TreeNode child) {
		return this.children.containsKey(child.hashCode());
	}

	public TreeNode removeChild(TreeNode child) {
		return this.children.remove(child);
	}

	public void removeChildren() {
		this.children.clear();
	}

	/**
	 * @return
	 */
	public String getWord() {
		if (!this.isWordEnd) {
			return null;
		}
		StringBuilder word = new StringBuilder();
		TreeNode tempNode = this;
		while (tempNode.parent != null) {
			word.insert(0, tempNode.chineseChar);
			tempNode = tempNode.parent;
		}
		return word.toString();
	}

	public int count() {
		return this.children.size();
	}

	/**
	 * @return the children
	 */
	public Map<Integer, TreeNode> getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj == this)
			return true;

		if (obj instanceof TreeNode) {
			TreeNode another = (TreeNode) obj;
			// 如果一个节点包含的字与本节点相等,则认为这两个节点是同一个节点
			if (another.chineseChar == this.chineseChar)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.chineseChar;
	}

	@Override
	public String toString() {
		return Character.toString(this.chineseChar);
	}

}
