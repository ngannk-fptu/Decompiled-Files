/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharArraySet;
import com.atlassian.lucene36.analysis.LowerCaseTokenizer;
import com.atlassian.lucene36.analysis.ReusableAnalyzerBase;
import com.atlassian.lucene36.analysis.StopFilter;
import com.atlassian.lucene36.analysis.StopwordAnalyzerBase;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.WordlistLoader;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.Version;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class StopAnalyzer
extends StopwordAnalyzerBase {
    public static final Set<?> ENGLISH_STOP_WORDS_SET;

    public StopAnalyzer(Version matchVersion) {
        this(matchVersion, ENGLISH_STOP_WORDS_SET);
    }

    public StopAnalyzer(Version matchVersion, Set<?> stopWords) {
        super(matchVersion, stopWords);
    }

    public StopAnalyzer(Version matchVersion, File stopwordsFile) throws IOException {
        this(matchVersion, WordlistLoader.getWordSet(IOUtils.getDecodingReader(stopwordsFile, IOUtils.CHARSET_UTF_8), matchVersion));
    }

    public StopAnalyzer(Version matchVersion, Reader stopwords) throws IOException {
        this(matchVersion, WordlistLoader.getWordSet(stopwords, matchVersion));
    }

    @Override
    protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        LowerCaseTokenizer source = new LowerCaseTokenizer(this.matchVersion, reader);
        return new ReusableAnalyzerBase.TokenStreamComponents(source, new StopFilter(this.matchVersion, (TokenStream)source, this.stopwords));
    }

    static {
        List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with");
        CharArraySet stopSet = new CharArraySet(Version.LUCENE_CURRENT, stopWords.size(), false);
        stopSet.addAll(stopWords);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
    }
}

