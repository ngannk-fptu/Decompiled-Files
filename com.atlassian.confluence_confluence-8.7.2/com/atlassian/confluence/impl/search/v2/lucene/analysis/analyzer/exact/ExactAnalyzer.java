/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.WhitespaceTokenizer
 *  org.apache.lucene.util.Version
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.exact;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

public class ExactAnalyzer
extends Analyzer {
    private final Version version;

    public ExactAnalyzer(Version version) {
        this.version = version;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer(this.version, reader);
        LowerCaseFilter result = new LowerCaseFilter(this.version, (TokenStream)tokenizer);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
    }
}

