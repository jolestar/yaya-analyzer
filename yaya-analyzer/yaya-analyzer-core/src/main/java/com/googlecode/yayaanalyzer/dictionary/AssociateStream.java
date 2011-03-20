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

import java.util.Map;

/**
 * Description: Assoicate next character to compages a word. Copyright:
 * Copyright (c) 2007
 * 
 * @author jolestar@gmail.com
 * @version 1.0
 * 
 */

public class AssociateStream {

	private TreeNode currNode;
	private int step = 0;
	private TreeNode root = null;

	private boolean occurWord = false;

	/**
	 * 
	 */
	public AssociateStream(WordTree tree) {
		currNode = tree.getRoot();
		root = tree.getRoot();
	}

	public boolean isBegin() {
		return this.currNode.equals(root);
	}

	public boolean associate(char nextChar) {
		Map<Integer, TreeNode> child = currNode.getChildren();// createChildrenMap();
		step++;
		if (child.containsKey(new Integer(nextChar))) {
			this.currNode = child.get(new Integer(nextChar));
			if (currNode.isWordEnd())
				this.occurWord = true;

			return true;
		} else
			return false;
	}

	public boolean isOccurWord() {
		return this.occurWord;
	}

	public boolean canContinue() {
		return this.currNode.hasChildren();
	}

	public boolean isWordEnd() {
		return this.currNode.isWordEnd();
	}

	public void backToLastWordEnd() {

		TreeNode tempNode = currNode;
		while (tempNode != null) {
			step--;
			if (tempNode.isWordEnd()) {
				currNode = tempNode;
				return;
			}

			tempNode = tempNode.getParent();
		}

	}

	public int getSetp() {
		return this.step;
	}

	public String getWord() {
		return this.currNode.getWord();
	}

	public void reset() {
		this.step = 0;
		this.currNode = this.root;
	}
}
