/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.WhitespaceTokenizer
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.search.v2.lucene.ConfluenceAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.tokenfilter.AnalyzerFilter;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.tokenfilter.FilenameFilter;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

public class ConfluenceFilenameAnalyzer
extends Analyzer {
    private ConfluenceAnalyzer confluenceAnalyzer;

    public ConfluenceFilenameAnalyzer(ConfluenceAnalyzer confluenceAnalyzer) {
        this.confluenceAnalyzer = confluenceAnalyzer;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer(LuceneConstants.LUCENE_VERSION, reader);
        TokenFilter result = new FilenameFilter((TokenStream)tokenizer);
        result = new LowerCaseFilter(LuceneConstants.LUCENE_VERSION, (TokenStream)result);
        result = new AnalyzerFilter((TokenStream)result, fieldName, (Analyzer)this.confluenceAnalyzer);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
    }
}

