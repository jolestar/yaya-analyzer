# 依赖库 #

  1. org.apache.lucene.lucene-core.3.0.3.jar
  1. org.apache.lucene.lucene-analyzers.3.0.3.jar
  1. junit.junit.4.5.jar


# 代码演示 #

使用默认词库:
```
Analyzer analyzer = new YayaAnalyzer();
TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
TermAttribute term = stream.getAttribute(TermAttribute.class);

while (stream.incrementToken()) {
    System.out.println(term.term());
}
```

使用自定义词库:
```
Analyzer analyzer = new YayaAnalyzer(WordTreeFactory.createInstance(new File("词库文件路径")));
TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
TermAttribute term = stream.getAttribute(TermAttribute.class);

while (stream.incrementToken()) {
    System.out.println(term.term());
}
```