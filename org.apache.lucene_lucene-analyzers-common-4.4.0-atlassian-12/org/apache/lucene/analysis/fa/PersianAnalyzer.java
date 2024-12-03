/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.fa;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ar.ArabicLetterTokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.fa.PersianCharFilter;
import org.apache.lucene.analysis.fa.PersianNormalizationFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class PersianAnalyzer
extends StopwordAnalyzerBase {
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    public static final String STOPWORDS_COMMENT = "#";

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public PersianAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET);
    }

    public PersianAnalyzer(Version matchVersion, CharArraySet stopwords) {
        super(matchVersion, stopwords);
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = this.matchVersion.onOrAfter(Version.LUCENE_31) ? new StandardTokenizer(this.matchVersion, reader) : new ArabicLetterTokenizer(this.matchVersion, reader);
        TokenFilter result = new LowerCaseFilter(this.matchVersion, (TokenStream)source);
        result = new ArabicNormalizationFilter((TokenStream)result);
        result = new PersianNormalizationFilter((TokenStream)result);
        return new Analyzer.TokenStreamComponents(source, (TokenStream)new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords));
    }

    protected Reader initReader(String fieldName, Reader reader) {
        return this.matchVersion.onOrAfter(Version.LUCENE_31) ? new PersianCharFilter(reader) : reader;
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = PersianAnalyzer.loadStopwordSet(false, PersianAnalyzer.class, PersianAnalyzer.DEFAULT_STOPWORD_FILE, PersianAnalyzer.STOPWORDS_COMMENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

