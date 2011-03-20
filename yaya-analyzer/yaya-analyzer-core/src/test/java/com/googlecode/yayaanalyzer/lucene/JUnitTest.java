/**
 * 
 */
package com.googlecode.yayaanalyzer.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jolestar@gmail.com
 * 
 */
public class JUnitTest {

	private String[] filenames = new String[] { "test.txt", "test2.txt" };
	private String[] texts;
	private List<Analyzer> analyzers = new ArrayList<Analyzer>();

	/**
     *
     */
	public JUnitTest() {
	}

	@Before
	public void before() {
		texts = new String[filenames.length];
		for (int i = 0; i < filenames.length; i++) {
			texts[i] = ChineseDemo.readText(getClass().getResourceAsStream(
					"/" + filenames[i]));
		}
		analyzers.add(new YayaAnalyzer());
		analyzers.add(new CJKAnalyzer(Version.LUCENE_30));
		analyzers.add(new StandardAnalyzer(Version.LUCENE_30));
		analyzers.add(new ChineseAnalyzer());
	}

	@Test
	public void testAnalyzers() throws IOException {
		for (Analyzer analyzer : analyzers) {
			for (String text : texts) {
				out(analyzer, text);
			}
		}
	}

	public void out(Analyzer analyzer, String text) throws IOException {
		System.out.println(analyzer.getClass().getSimpleName());

		TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));

		while (stream.incrementToken()) {
			TermAttribute term = stream.getAttribute(TermAttribute.class);
			TypeAttribute type = null;
			if (stream.hasAttribute(TypeAttribute.class)) {
				type = (TypeAttribute) stream.getAttribute(TypeAttribute.class);
			}
			System.out.println("[" + term.term()
					+ (type == null ? "" : "/" + type.type()) + "]");
		}
		System.out.println();

	}
}
