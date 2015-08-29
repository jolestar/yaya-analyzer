/**
 * 
 */
package com.grouk.yayaanalyzer.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.AnalyzerUtils;

import java.io.*;

/**
 * @author Jolestar
 */
public class ChineseDemo {

	private static Analyzer[] analyzers = { new YayaAnalyzer(),
			new StandardAnalyzer(), new CJKAnalyzer() };

	public static void analyzerTest(String filePath) {
		try {
			String text = readText(filePath);
			for (Analyzer analyzer : analyzers)
				analyze(text, analyzer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readText(String filePath) {
        try {
            return readText(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
        }
	}

    public static String readText(InputStream input){
        try {

			InputStreamReader inReader = new InputStreamReader(input, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inReader);
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				buffer.append(line);
			}
			return buffer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

	private static void analyze(String string, Analyzer analyzer)
			throws IOException {
		StringBuffer buffer = new StringBuffer();
		String[] tokens = AnalyzerUtils.tokensFromAnalysis(analyzer, string);
		for (int i = 0; i < tokens.length; i++) {
			buffer.append("[");
			buffer.append(tokens[i]);
			buffer.append("] ");
		}
		String name = analyzer.getClass().getName();

		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(name + ".txt"), "UTF-8");
		writer.write(buffer.toString());
		writer.flush();
		writer.close();

		System.out.println(name);
		System.out.print(buffer.toString());
		System.out.println();
	}

	public static void main(String args[]) throws Exception {
        if(args.length!=1){
            throw new IllegalArgumentException("ChineseDemo需要一个参数指定分词文件路径。");
        }
		analyzerTest(args[0]);
	}

}
