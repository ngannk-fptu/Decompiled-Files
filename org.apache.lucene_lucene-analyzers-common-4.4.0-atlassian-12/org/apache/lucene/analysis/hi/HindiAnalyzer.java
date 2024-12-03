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
package org.apache.lucene.analysis.hi;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.hi.HindiNormalizationFilter;
import org.apache.lucene.analysis.hi.HindiStemFilter;
import org.apache.lucene.analysis.in.IndicNormalizationFilter;
import org.apache.lucene.analysis.in.IndicTokenizer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class HindiAnalyzer
extends StopwordAnalyzerBase {
    private final CharArraySet stemExclusionSet;
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private static final String STOPWORDS_COMMENT = "#";

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public HindiAnalyzer(Version version, CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(version, stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(this.matchVersion, stemExclusionSet));
    }

    public HindiAnalyzer(Version version, CharArraySet stopwords) {
        this(version, stopwords, CharArraySet.EMPTY_SET);
    }

    public HindiAnalyzer(Version version) {
        this(version, DefaultSetHolder.DEFAULT_STOP_SET);
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = this.matchVersion.onOrAfter(Version.LUCENE_36) ? new StandardTokenizer(this.matchVersion, reader) : new IndicTokenizer(this.matchVersion, reader);
        TokenFilter result = new LowerCaseFilter(this.matchVersion, (TokenStream)source);
        if (!this.stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionSet);
        }
        result = new IndicNormalizationFilter((TokenStream)result);
        result = new HindiNormalizationFilter((TokenStream)result);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        result = new HindiStemFilter((TokenStream)result);
        return new Analyzer.TokenStreamComponents(source, (TokenStream)result);
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = HindiAnalyzer.loadStopwordSet(false, HindiAnalyzer.class, HindiAnalyzer.DEFAULT_STOPWORD_FILE, HindiAnalyzer.STOPWORDS_COMMENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

