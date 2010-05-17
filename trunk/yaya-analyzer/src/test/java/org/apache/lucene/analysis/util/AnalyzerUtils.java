/**
 * 
 */
package org.apache.lucene.analysis.util;

import java.io.*;
import java.util.ArrayList;

import org.apache.lucene.analysis.*;

/**
 * @author Jolestar
 */
public class AnalyzerUtils
{
	public static Token[] tokensFromAnalysis(Analyzer analyzer, String text)
			throws IOException
	{
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
		ArrayList tokenList = new ArrayList();
		while (true)
		{
			Token token = stream.next();
			if (token == null)
				break;
			tokenList.add(token);
		}
		return (Token[]) tokenList.toArray(new Token[0]);
	}

	public static void displayTokens(Analyzer analyzer, String text)
			throws IOException
	{
		Token[] tokens = tokensFromAnalysis(analyzer, text);
		for (int i = 0; i < tokens.length; i++)
		{
			Token token = tokens[i];
			System.out.print("[" + token.termText() + "] ");
		}
	}
}
