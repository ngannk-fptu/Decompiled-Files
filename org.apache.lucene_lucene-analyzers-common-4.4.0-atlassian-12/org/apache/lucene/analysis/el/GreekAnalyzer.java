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
package org.apache.lucene.analysis.el;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.el.GreekLowerCaseFilter;
import org.apache.lucene.analysis.el.GreekStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class GreekAnalyzer
extends StopwordAnalyzerBase {
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";

    public static final CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_SET;
    }

    public GreekAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_SET);
    }

    public GreekAnalyzer(Version matchVersion, CharArraySet stopwords) {
        super(matchVersion, stopwords);
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
        TokenFilter result = new GreekLowerCaseFilter(this.matchVersion, (TokenStream)source);
        if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
            result = new StandardFilter(this.matchVersion, (TokenStream)result);
        }
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
            result = new GreekStemFilter((TokenStream)result);
        }
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }

    private static class DefaultSetHolder {
        private static final CharArraySet DEFAULT_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_SET = GreekAnalyzer.loadStopwordSet(false, GreekAnalyzer.class, GreekAnalyzer.DEFAULT_STOPWORD_FILE, "#");
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

