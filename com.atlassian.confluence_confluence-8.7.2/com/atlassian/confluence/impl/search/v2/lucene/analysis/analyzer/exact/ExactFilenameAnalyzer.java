/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.Version
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.exact;

import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.exact.ExactAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.tokenfilter.FilenameFilter;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

public class ExactFilenameAnalyzer
extends Analyzer {
    private final Version version;

    public ExactFilenameAnalyzer(Version version) {
        this.version = version;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        try (ExactAnalyzer exactAnalyzer = new ExactAnalyzer(this.version);){
            Analyzer.TokenStreamComponents exactAnalyzerStreamComponents = exactAnalyzer.createComponents(fieldName, reader);
            FilenameFilter result = new FilenameFilter(exactAnalyzerStreamComponents.getTokenStream(), ".", false);
            Analyzer.TokenStreamComponents tokenStreamComponents = new Analyzer.TokenStreamComponents(exactAnalyzerStreamComponents.getTokenizer(), (TokenStream)result);
            return tokenStreamComponents;
        }
    }
}

