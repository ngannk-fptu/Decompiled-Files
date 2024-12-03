/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.ar.ArabicAnalyzer
 *  org.apache.lucene.analysis.ar.ArabicNormalizationFilter
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.StopFilter
 *  org.apache.lucene.analysis.standard.StandardTokenizer
 *  org.apache.lucene.util.Version
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class ArabicAnalyzer
extends Analyzer {
    private final Version version;

    public ArabicAnalyzer(Version luceneVersion) {
        this.version = luceneVersion;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer source = new StandardTokenizer(this.version, reader);
        LowerCaseFilter result = new LowerCaseFilter(this.version, (TokenStream)source);
        result = new StopFilter(this.version, (TokenStream)result, org.apache.lucene.analysis.ar.ArabicAnalyzer.getDefaultStopSet());
        result = new ArabicNormalizationFilter((TokenStream)result);
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }
}

