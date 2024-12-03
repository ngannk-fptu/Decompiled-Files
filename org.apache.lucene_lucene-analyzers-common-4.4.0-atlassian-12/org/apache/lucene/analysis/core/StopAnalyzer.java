/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.core;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class StopAnalyzer
extends StopwordAnalyzerBase {
    public static final CharArraySet ENGLISH_STOP_WORDS_SET;

    public StopAnalyzer(Version matchVersion) {
        this(matchVersion, ENGLISH_STOP_WORDS_SET);
    }

    public StopAnalyzer(Version matchVersion, CharArraySet stopWords) {
        super(matchVersion, stopWords);
    }

    public StopAnalyzer(Version matchVersion, File stopwordsFile) throws IOException {
        this(matchVersion, StopAnalyzer.loadStopwordSet(stopwordsFile, matchVersion));
    }

    public StopAnalyzer(Version matchVersion, Reader stopwords) throws IOException {
        this(matchVersion, StopAnalyzer.loadStopwordSet(stopwords, matchVersion));
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        LowerCaseTokenizer source = new LowerCaseTokenizer(this.matchVersion, reader);
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)new StopFilter(this.matchVersion, (TokenStream)source, this.stopwords));
    }

    static {
        List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with");
        CharArraySet stopSet = new CharArraySet(Version.LUCENE_CURRENT, stopWords, false);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
    }
}

