/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;

public abstract class AnalyzerWrapper
extends Analyzer {
    protected AnalyzerWrapper() {
        super(new Analyzer.PerFieldReuseStrategy());
    }

    protected abstract Analyzer getWrappedAnalyzer(String var1);

    protected abstract Analyzer.TokenStreamComponents wrapComponents(String var1, Analyzer.TokenStreamComponents var2);

    @Override
    protected final Analyzer.TokenStreamComponents createComponents(String fieldName, Reader aReader) {
        return this.wrapComponents(fieldName, this.getWrappedAnalyzer(fieldName).createComponents(fieldName, aReader));
    }

    @Override
    public final int getPositionIncrementGap(String fieldName) {
        return this.getWrappedAnalyzer(fieldName).getPositionIncrementGap(fieldName);
    }

    @Override
    public final int getOffsetGap(String fieldName) {
        return this.getWrappedAnalyzer(fieldName).getOffsetGap(fieldName);
    }

    @Override
    public final Reader initReader(String fieldName, Reader reader) {
        return this.getWrappedAnalyzer(fieldName).initReader(fieldName, reader);
    }
}

