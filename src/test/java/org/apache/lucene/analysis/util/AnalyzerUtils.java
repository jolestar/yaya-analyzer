/**
 * 
 */
package org.apache.lucene.analysis.util;

import java.io.*;
import java.util.ArrayList;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * @author jolestar@gmail.com
 */
public class AnalyzerUtils {
	public static String[] tokensFromAnalysis(Analyzer analyzer, String text)
			throws IOException {
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
		ArrayList<String> tokenList = new ArrayList<String>();
		TermAttribute term = stream.getAttribute(TermAttribute.class);
		// TypeAttribute type =
		// (TypeAttribute)stream.getAttribute(TypeAttribute.class);
		while (stream.incrementToken()) {
			tokenList.add(term.term());
		}
		return tokenList.toArray(new String[0]);
	}

	public static void displayTokens(Analyzer analyzer, String text)
			throws IOException {
		String[] tokens = tokensFromAnalysis(analyzer, text);
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			System.out.print("[" + token + "] ");
		}
	}
}
