/**
 * 
 */
package com.grouk.yayaanalyzer.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
		analyzers.add(new CJKAnalyzer());
		analyzers.add(new StandardAnalyzer());
		//analyzers.add(new ChineseAnalyzer());
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
		stream.reset();
		while (stream.incrementToken()) {
			CharTermAttribute term = stream.getAttribute(CharTermAttribute.class);
			TypeAttribute type = null;
			if (stream.hasAttribute(TypeAttribute.class)) {
				type = (TypeAttribute) stream.getAttribute(TypeAttribute.class);
			}
			System.out.println("[" + term.toString()
					+ (type == null ? "" : "/" + type.type()) + "]");
		}
		stream.close();
	}
}
