/**
 * 
 */
package com.googlecode.yayaanalyzer.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.util.AnalyzerUtils;

import com.googlecode.yayaanalyzer.lucene.YayaAnalyzer;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jolestar
 *
 */
public class JUnitTest {

    private String text;
    private List<Analyzer> analyzers = new ArrayList<Analyzer>();

    /**
     *
     */
    public JUnitTest() {
    }

    @Before
    public void before() {
        text = ChineseDemo.readText(getClass().getResourceAsStream("/test.txt"));
        analyzers.add(new YayaAnalyzer());
        analyzers.add(new CJKAnalyzer());
        analyzers.add(new StandardAnalyzer());
        analyzers.add(new ChineseAnalyzer());
    }

    @Test
    public void testAnalyzers() throws IOException {
        for (Analyzer analyzer : analyzers) {
            out(analyzer);
        }
    }

    public void out(Analyzer analyzer) throws IOException {
        System.out.println(analyzer.getClass().getSimpleName());

        Token[] tokens = AnalyzerUtils.tokensFromAnalysis(
                analyzer, text);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append("[");
            buffer.append(tokens[i].term());
            buffer.append("] ");
        }
        System.out.println(buffer.toString());
        System.out.println();

    }
}
