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
package org.apache.lucene.analysis.th;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.th.ThaiWordFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class ThaiAnalyzer
extends StopwordAnalyzerBase {
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private static final String STOPWORDS_COMMENT = "#";

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public ThaiAnalyzer(Version matchVersion) {
        this(matchVersion, matchVersion.onOrAfter(Version.LUCENE_36) ? DefaultSetHolder.DEFAULT_STOP_SET : StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    public ThaiAnalyzer(Version matchVersion, CharArraySet stopwords) {
        super(matchVersion, stopwords);
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
        TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
        if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
            result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
        }
        result = new ThaiWordFilter(this.matchVersion, (TokenStream)result);
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords));
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = ThaiAnalyzer.loadStopwordSet(false, ThaiAnalyzer.class, ThaiAnalyzer.DEFAULT_STOPWORD_FILE, ThaiAnalyzer.STOPWORDS_COMMENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

