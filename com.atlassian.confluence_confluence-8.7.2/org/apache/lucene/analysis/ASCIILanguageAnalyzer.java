/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter
 */
package org.apache.lucene.analysis;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

public class ASCIILanguageAnalyzer
extends Analyzer {
    private final Analyzer delegate;

    public ASCIILanguageAnalyzer(Analyzer delegate) {
        this.delegate = delegate;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Analyzer.TokenStreamComponents components = this.delegate.createComponents(fieldName, reader);
        return new Analyzer.TokenStreamComponents(components.source, (TokenStream)new ASCIIFoldingFilter(components.sink));
    }
}

