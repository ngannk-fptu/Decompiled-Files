/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.StopAnalyzer
 *  org.apache.lucene.analysis.core.StopFilter
 *  org.apache.lucene.analysis.en.KStemFilter
 *  org.apache.lucene.analysis.standard.StandardFilter
 *  org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer
 *  org.apache.lucene.util.Version
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.util.Version;

public class EnglishAnalyzer
extends Analyzer {
    private Version matchVersion = LuceneConstants.LUCENE_VERSION;

    @Deprecated
    public EnglishAnalyzer() {
    }

    public EnglishAnalyzer(Version matchVersion) {
        this.matchVersion = matchVersion;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        UAX29URLEmailTokenizer tokenizer = new UAX29URLEmailTokenizer(this.matchVersion, reader);
        StandardFilter result = new StandardFilter(this.matchVersion, (TokenStream)tokenizer);
        result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
        result = new StopFilter(this.matchVersion, (TokenStream)result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        result = new KStemFilter((TokenStream)result);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
    }
}

