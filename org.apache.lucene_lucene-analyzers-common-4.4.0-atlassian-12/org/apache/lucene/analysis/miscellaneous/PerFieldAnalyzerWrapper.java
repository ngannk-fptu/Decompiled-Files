/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.AnalyzerWrapper
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Collections;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;

public final class PerFieldAnalyzerWrapper
extends AnalyzerWrapper {
    private final Analyzer defaultAnalyzer;
    private final Map<String, Analyzer> fieldAnalyzers;

    public PerFieldAnalyzerWrapper(Analyzer defaultAnalyzer) {
        this(defaultAnalyzer, null);
    }

    public PerFieldAnalyzerWrapper(Analyzer defaultAnalyzer, Map<String, Analyzer> fieldAnalyzers) {
        this.defaultAnalyzer = defaultAnalyzer;
        this.fieldAnalyzers = fieldAnalyzers != null ? fieldAnalyzers : Collections.emptyMap();
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        Analyzer analyzer = this.fieldAnalyzers.get(fieldName);
        return analyzer != null ? analyzer : this.defaultAnalyzer;
    }

    protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents components) {
        return components;
    }

    public String toString() {
        return "PerFieldAnalyzerWrapper(" + this.fieldAnalyzers + ", default=" + this.defaultAnalyzer + ")";
    }
}

