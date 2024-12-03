/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.analysis.LimitTokenCountFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.document.Fieldable;
import java.io.IOException;
import java.io.Reader;

public final class LimitTokenCountAnalyzer
extends Analyzer {
    private final Analyzer delegate;
    private final int maxTokenCount;

    public LimitTokenCountAnalyzer(Analyzer delegate, int maxTokenCount) {
        this.delegate = delegate;
        this.maxTokenCount = maxTokenCount;
    }

    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new LimitTokenCountFilter(this.delegate.tokenStream(fieldName, reader), this.maxTokenCount);
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        return new LimitTokenCountFilter(this.delegate.reusableTokenStream(fieldName, reader), this.maxTokenCount);
    }

    public int getPositionIncrementGap(String fieldName) {
        return this.delegate.getPositionIncrementGap(fieldName);
    }

    public int getOffsetGap(Fieldable field) {
        return this.delegate.getOffsetGap(field);
    }

    public String toString() {
        return "LimitTokenCountAnalyzer(" + this.delegate.toString() + ", maxTokenCount=" + this.maxTokenCount + ")";
    }
}

