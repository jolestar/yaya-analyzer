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

import com.grouk.yayaanalyzer.dictionary.WordTree;
import com.grouk.yayaanalyzer.dictionary.WordTreeFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * Title: YayaAnalyzer
 * Description:
 *   Subclass of org.apache.lucene.analysis.Analyzer
 * @author jolestar@gmail.com
 * @version 1.0
 *
 */

public class YayaAnalyzer extends Analyzer {

	private WordTree tree;
    public YayaAnalyzer() {
    	this(WordTreeFactory.getDefaultInstance());
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new YayaTokenizer(this.tree);
        return new TokenStreamComponents(source);
    }

    public YayaAnalyzer(WordTree tree) {
    	this.tree = tree;
    }

//    public final TokenStream tokenStream(String fieldName, Reader reader) {
//    	 TokenStream result = new YayaTokenizer(tree,reader);
//         return result;
//    }
}