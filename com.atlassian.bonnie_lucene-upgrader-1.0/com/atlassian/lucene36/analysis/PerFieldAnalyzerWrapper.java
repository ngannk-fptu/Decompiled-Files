/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.document.Fieldable;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PerFieldAnalyzerWrapper
extends Analyzer {
    private final Analyzer defaultAnalyzer;
    private final Map<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();

    public PerFieldAnalyzerWrapper(Analyzer defaultAnalyzer) {
        this(defaultAnalyzer, null);
    }

    public PerFieldAnalyzerWrapper(Analyzer defaultAnalyzer, Map<String, Analyzer> fieldAnalyzers) {
        this.defaultAnalyzer = defaultAnalyzer;
        if (fieldAnalyzers != null) {
            this.analyzerMap.putAll(fieldAnalyzers);
        }
    }

    @Deprecated
    public void addAnalyzer(String fieldName, Analyzer analyzer) {
        this.analyzerMap.put(fieldName, analyzer);
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        Analyzer analyzer = this.analyzerMap.get(fieldName);
        if (analyzer == null) {
            analyzer = this.defaultAnalyzer;
        }
        return analyzer.tokenStream(fieldName, reader);
    }

    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        Analyzer analyzer = this.analyzerMap.get(fieldName);
        if (analyzer == null) {
            analyzer = this.defaultAnalyzer;
        }
        return analyzer.reusableTokenStream(fieldName, reader);
    }

    @Override
    public int getPositionIncrementGap(String fieldName) {
        Analyzer analyzer = this.analyzerMap.get(fieldName);
        if (analyzer == null) {
            analyzer = this.defaultAnalyzer;
        }
        return analyzer.getPositionIncrementGap(fieldName);
    }

    @Override
    public int getOffsetGap(Fieldable field) {
        Analyzer analyzer = this.analyzerMap.get(field.name());
        if (analyzer == null) {
            analyzer = this.defaultAnalyzer;
        }
        return analyzer.getOffsetGap(field);
    }

    public String toString() {
        return "PerFieldAnalyzerWrapper(" + this.analyzerMap + ", default=" + this.defaultAnalyzer + ")";
    }
}

