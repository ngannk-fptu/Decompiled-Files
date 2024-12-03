/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.StopFilter
 *  org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter
 *  org.apache.lucene.analysis.standard.StandardFilter
 *  org.apache.lucene.analysis.standard.StandardTokenizer
 *  org.apache.lucene.analysis.util.CharArraySet
 *  org.apache.lucene.analysis.util.StopwordAnalyzerBase
 *  org.apache.lucene.analysis.util.WordlistLoader
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.pl;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.stempel.StempelFilter;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.egothor.stemmer.Trie;

public final class PolishAnalyzer
extends StopwordAnalyzerBase {
    private final CharArraySet stemExclusionSet;
    private final Trie stemTable = DefaultsHolder.DEFAULT_TABLE;
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    public static final String DEFAULT_STEMMER_FILE = "stemmer_20000.tbl";

    public static CharArraySet getDefaultStopSet() {
        return DefaultsHolder.DEFAULT_STOP_SET;
    }

    public static Trie getDefaultTable() {
        return DefaultsHolder.DEFAULT_TABLE;
    }

    public PolishAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultsHolder.DEFAULT_STOP_SET);
    }

    public PolishAnalyzer(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    public PolishAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(matchVersion, stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet((CharArraySet)CharArraySet.copy((Version)matchVersion, (Set)stemExclusionSet));
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
        Object result = new StandardFilter(this.matchVersion, (TokenStream)source);
        result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        if (!this.stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionSet);
        }
        result = new StempelFilter((TokenStream)result, new StempelStemmer(this.stemTable));
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }

    private static class DefaultsHolder {
        static final CharArraySet DEFAULT_STOP_SET;
        static final Trie DEFAULT_TABLE;

        private DefaultsHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getWordSet((Reader)IOUtils.getDecodingReader(PolishAnalyzer.class, (String)PolishAnalyzer.DEFAULT_STOPWORD_FILE, (Charset)IOUtils.CHARSET_UTF_8), (String)"#", (Version)Version.LUCENE_CURRENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set", ex);
            }
            try {
                DEFAULT_TABLE = StempelStemmer.load(PolishAnalyzer.class.getResourceAsStream(PolishAnalyzer.DEFAULT_STEMMER_FILE));
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stemming tables", ex);
            }
        }
    }
}

