/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.standard;

import com.atlassian.lucene36.analysis.LowerCaseFilter;
import com.atlassian.lucene36.analysis.ReusableAnalyzerBase;
import com.atlassian.lucene36.analysis.StopAnalyzer;
import com.atlassian.lucene36.analysis.StopFilter;
import com.atlassian.lucene36.analysis.StopwordAnalyzerBase;
import com.atlassian.lucene36.analysis.TokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.WordlistLoader;
import com.atlassian.lucene36.analysis.standard.StandardFilter;
import com.atlassian.lucene36.analysis.standard.StandardTokenizer;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.Version;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class StandardAnalyzer
extends StopwordAnalyzerBase {
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength = 255;
    private final boolean replaceInvalidAcronym;
    public static final Set<?> STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

    public StandardAnalyzer(Version matchVersion, Set<?> stopWords) {
        super(matchVersion, stopWords);
        this.replaceInvalidAcronym = matchVersion.onOrAfter(Version.LUCENE_24);
    }

    public StandardAnalyzer(Version matchVersion) {
        this(matchVersion, STOP_WORDS_SET);
    }

    @Deprecated
    public StandardAnalyzer(Version matchVersion, File stopwords) throws IOException {
        this(matchVersion, WordlistLoader.getWordSet(IOUtils.getDecodingReader(stopwords, IOUtils.CHARSET_UTF_8), matchVersion));
    }

    public StandardAnalyzer(Version matchVersion, Reader stopwords) throws IOException {
        this(matchVersion, WordlistLoader.getWordSet(stopwords, matchVersion));
    }

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    @Override
    protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final StandardTokenizer src = new StandardTokenizer(this.matchVersion, reader);
        src.setMaxTokenLength(this.maxTokenLength);
        src.setReplaceInvalidAcronym(this.replaceInvalidAcronym);
        TokenFilter tok = new StandardFilter(this.matchVersion, src);
        tok = new LowerCaseFilter(this.matchVersion, tok);
        tok = new StopFilter(this.matchVersion, (TokenStream)tok, this.stopwords);
        return new ReusableAnalyzerBase.TokenStreamComponents(src, tok){

            protected boolean reset(Reader reader) throws IOException {
                src.setMaxTokenLength(StandardAnalyzer.this.maxTokenLength);
                return super.reset(reader);
            }
        };
    }
}

