/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.el.GreekLowerCaseFilter
 *  org.apache.lucene.analysis.standard.StandardFilter
 *  org.apache.lucene.analysis.standard.StandardTokenizer
 *  org.apache.lucene.util.Version
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.el.GreekLowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class GreekAnalyzer
extends Analyzer {
    private final Version version;

    public GreekAnalyzer(Version luceneVersion) {
        this.version = luceneVersion;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer tokenizer = new StandardTokenizer(this.version, reader);
        StandardFilter result = new StandardFilter(this.version, (TokenStream)tokenizer);
        result = new GreekLowerCaseFilter(this.version, (TokenStream)result);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
    }
}

