/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.StopFilter
 *  org.apache.lucene.analysis.standard.StandardFilter
 *  org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer
 *  org.apache.lucene.analysis.util.CharArraySet
 *  org.apache.lucene.util.Version
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.unstemmed;

import com.atlassian.confluence.impl.search.v2.lucene.analysis.tokenfilter.ExtendedDelimitersFilter;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class EuropeanAnalyzer
extends Analyzer {
    private final Version version;
    private final CharArraySet stopWords;

    public EuropeanAnalyzer(Version luceneVersion) {
        this(luceneVersion, null);
    }

    public EuropeanAnalyzer(Version luceneVersion, CharArraySet stopWords) {
        this.version = luceneVersion;
        this.stopWords = stopWords;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        UAX29URLEmailTokenizer tokenizer = new UAX29URLEmailTokenizer(this.version, reader);
        ExtendedDelimitersFilter result = new ExtendedDelimitersFilter((TokenStream)tokenizer, "_");
        result = new StandardFilter(this.version, (TokenStream)result);
        if (this.stopWords != null) {
            result = new StopFilter(this.version, (TokenStream)result, this.stopWords);
        }
        result = new LowerCaseFilter(this.version, (TokenStream)result);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
    }
}

