/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.AnalyzerWrapper
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountFilter;

public final class LimitTokenCountAnalyzer
extends AnalyzerWrapper {
    private final Analyzer delegate;
    private final int maxTokenCount;
    private final boolean consumeAllTokens;

    public LimitTokenCountAnalyzer(Analyzer delegate, int maxTokenCount) {
        this(delegate, maxTokenCount, false);
    }

    public LimitTokenCountAnalyzer(Analyzer delegate, int maxTokenCount, boolean consumeAllTokens) {
        this.delegate = delegate;
        this.maxTokenCount = maxTokenCount;
        this.consumeAllTokens = consumeAllTokens;
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        return this.delegate;
    }

    protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents components) {
        return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)new LimitTokenCountFilter(components.getTokenStream(), this.maxTokenCount, this.consumeAllTokens));
    }

    public String toString() {
        return "LimitTokenCountAnalyzer(" + this.delegate.toString() + ", maxTokenCount=" + this.maxTokenCount + ", consumeAllTokens=" + this.consumeAllTokens + ")";
    }
}

