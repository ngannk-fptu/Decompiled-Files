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
package org.apache.lucene.analysis.ca;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.CatalanStemmer;

public final class CatalanAnalyzer
extends StopwordAnalyzerBase {
    private final CharArraySet stemExclusionSet;
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private static final CharArraySet DEFAULT_ARTICLES = CharArraySet.unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList("d", "l", "m", "n", "s", "t"), true));

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public CatalanAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET);
    }

    public CatalanAnalyzer(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    public CatalanAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(matchVersion, stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionSet));
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
        TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
        if (this.matchVersion.onOrAfter(Version.LUCENE_36)) {
            result = new ElisionFilter((TokenStream)result, DEFAULT_ARTICLES);
        }
        result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        if (!this.stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionSet);
        }
        result = new SnowballFilter((TokenStream)result, new CatalanStemmer());
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = CatalanAnalyzer.loadStopwordSet(false, CatalanAnalyzer.class, CatalanAnalyzer.DEFAULT_STOPWORD_FILE, "#");
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

