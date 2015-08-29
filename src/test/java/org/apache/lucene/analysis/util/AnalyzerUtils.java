/**
 * 
 */
package org.apache.lucene.analysis.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author jolestar@gmail.com
 */
public class AnalyzerUtils {
	public static String[] tokensFromAnalysis(Analyzer analyzer, String text)
			throws IOException {
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
		ArrayList<String> tokenList = new ArrayList<String>();
		CharTermAttribute term = stream.getAttribute(CharTermAttribute.class);
		// TypeAttribute type =
		// (TypeAttribute)stream.getAttribute(TypeAttribute.class);
		while (stream.incrementToken()) {
			tokenList.add(term.toString());
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
