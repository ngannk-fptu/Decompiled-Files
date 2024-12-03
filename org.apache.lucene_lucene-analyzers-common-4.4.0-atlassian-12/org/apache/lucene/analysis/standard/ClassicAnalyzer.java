/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.standard;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class ClassicAnalyzer
extends StopwordAnalyzerBase {
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength = 255;
    public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

    public ClassicAnalyzer(Version matchVersion, CharArraySet stopWords) {
        super(matchVersion, stopWords);
    }

    public ClassicAnalyzer(Version matchVersion) {
        this(matchVersion, STOP_WORDS_SET);
    }

    public ClassicAnalyzer(Version matchVersion, Reader stopwords) throws IOException {
        this(matchVersion, ClassicAnalyzer.loadStopwordSet(stopwords, matchVersion));
    }

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final ClassicTokenizer src = new ClassicTokenizer(this.matchVersion, reader);
        src.setMaxTokenLength(this.maxTokenLength);
        TokenFilter tok = new ClassicFilter((TokenStream)src);
        tok = new LowerCaseFilter(this.matchVersion, (TokenStream)tok);
        tok = new StopFilter(this.matchVersion, (TokenStream)tok, this.stopwords);
        return new Analyzer.TokenStreamComponents(src, (TokenStream)tok){

            protected void setReader(Reader reader) throws IOException {
                src.setMaxTokenLength(ClassicAnalyzer.this.maxTokenLength);
                super.setReader(reader);
            }
        };
    }
}

