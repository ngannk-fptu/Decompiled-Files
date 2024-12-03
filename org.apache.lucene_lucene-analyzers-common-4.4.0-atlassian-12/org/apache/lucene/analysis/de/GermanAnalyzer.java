/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.de;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.de.GermanLightStemFilter;
import org.apache.lucene.analysis.de.GermanNormalizationFilter;
import org.apache.lucene.analysis.de.GermanStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.German2Stemmer;

public final class GermanAnalyzer
extends StopwordAnalyzerBase {
    @Deprecated
    private static final String[] GERMAN_STOP_WORDS = new String[]{"einer", "eine", "eines", "einem", "einen", "der", "die", "das", "dass", "da\u00df", "du", "er", "sie", "es", "was", "wer", "wie", "wir", "und", "oder", "ohne", "mit", "am", "im", "in", "aus", "auf", "ist", "sein", "war", "wird", "ihr", "ihre", "ihres", "als", "f\u00fcr", "von", "mit", "dich", "dir", "mich", "mir", "mein", "sein", "kein", "durch", "wegen", "wird"};
    public static final String DEFAULT_STOPWORD_FILE = "german_stop.txt";
    private final CharArraySet exclusionSet;

    public static final CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_SET;
    }

    public GermanAnalyzer(Version matchVersion) {
        this(matchVersion, matchVersion.onOrAfter(Version.LUCENE_31) ? DefaultSetHolder.DEFAULT_SET : DefaultSetHolder.DEFAULT_SET_30);
    }

    public GermanAnalyzer(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    public GermanAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(matchVersion, stopwords);
        this.exclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionSet));
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
        TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
        result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        result = new SetKeywordMarkerFilter((TokenStream)result, this.exclusionSet);
        if (this.matchVersion.onOrAfter(Version.LUCENE_36)) {
            result = new GermanNormalizationFilter((TokenStream)result);
            result = new GermanLightStemFilter((TokenStream)result);
        } else {
            result = this.matchVersion.onOrAfter(Version.LUCENE_31) ? new SnowballFilter((TokenStream)result, new German2Stemmer()) : new GermanStemFilter((TokenStream)result);
        }
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }

    static /* synthetic */ String[] access$100() {
        return GERMAN_STOP_WORDS;
    }

    private static class DefaultSetHolder {
        @Deprecated
        private static final CharArraySet DEFAULT_SET_30 = CharArraySet.unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList(GermanAnalyzer.access$100()), false));
        private static final CharArraySet DEFAULT_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, (String)GermanAnalyzer.DEFAULT_STOPWORD_FILE, (Charset)IOUtils.CHARSET_UTF_8), Version.LUCENE_CURRENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

