/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.nl;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilter;
import org.apache.lucene.analysis.nl.DutchStemFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.DutchStemmer;

public final class DutchAnalyzer
extends Analyzer {
    public static final String DEFAULT_STOPWORD_FILE = "dutch_stop.txt";
    private final CharArraySet stoptable;
    private CharArraySet excltable = CharArraySet.EMPTY_SET;
    private final StemmerOverrideFilter.StemmerOverrideMap stemdict;
    private final CharArrayMap<String> origStemdict;
    private final Version matchVersion;

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public DutchAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET, CharArraySet.EMPTY_SET, DefaultSetHolder.DEFAULT_STEM_DICT);
    }

    public DutchAnalyzer(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET, matchVersion.onOrAfter(Version.LUCENE_36) ? DefaultSetHolder.DEFAULT_STEM_DICT : CharArrayMap.emptyMap());
    }

    public DutchAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionTable) {
        this(matchVersion, stopwords, stemExclusionTable, matchVersion.onOrAfter(Version.LUCENE_36) ? DefaultSetHolder.DEFAULT_STEM_DICT : CharArrayMap.emptyMap());
    }

    public DutchAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionTable, CharArrayMap<String> stemOverrideDict) {
        this.matchVersion = matchVersion;
        this.stoptable = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stopwords));
        this.excltable = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionTable));
        if (stemOverrideDict.isEmpty() || !matchVersion.onOrAfter(Version.LUCENE_31)) {
            this.stemdict = null;
            this.origStemdict = CharArrayMap.unmodifiableMap(CharArrayMap.copy(matchVersion, stemOverrideDict));
        } else {
            this.origStemdict = null;
            StemmerOverrideFilter.Builder builder = new StemmerOverrideFilter.Builder(false);
            CharArrayMap.EntryIterator iter = stemOverrideDict.entrySet().iterator();
            CharsRef spare = new CharsRef();
            while (iter.hasNext()) {
                char[] nextKey = iter.nextKey();
                spare.copyChars(nextKey, 0, nextKey.length);
                builder.add((CharSequence)spare, (CharSequence)iter.currentValue());
            }
            try {
                this.stemdict = builder.build();
            }
            catch (IOException ex) {
                throw new RuntimeException("can not build stem dict", ex);
            }
        }
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader aReader) {
        if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
            StandardTokenizer source = new StandardTokenizer(this.matchVersion, aReader);
            TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
            result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
            result = new StopFilter(this.matchVersion, (TokenStream)result, this.stoptable);
            if (!this.excltable.isEmpty()) {
                result = new SetKeywordMarkerFilter((TokenStream)result, this.excltable);
            }
            if (this.stemdict != null) {
                result = new StemmerOverrideFilter((TokenStream)result, this.stemdict);
            }
            result = new SnowballFilter((TokenStream)result, new DutchStemmer());
            return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
        }
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, aReader);
        TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stoptable);
        if (!this.excltable.isEmpty()) {
            result = new SetKeywordMarkerFilter((TokenStream)result, this.excltable);
        }
        result = new DutchStemFilter((TokenStream)result, this.origStemdict);
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;
        static final CharArrayMap<String> DEFAULT_STEM_DICT;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, (String)DutchAnalyzer.DEFAULT_STOPWORD_FILE, (Charset)IOUtils.CHARSET_UTF_8), Version.LUCENE_CURRENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
            DEFAULT_STEM_DICT = new CharArrayMap(Version.LUCENE_CURRENT, 4, false);
            DEFAULT_STEM_DICT.put("fiets", "fiets");
            DEFAULT_STEM_DICT.put("bromfiets", "bromfiets");
            DEFAULT_STEM_DICT.put("ei", "eier");
            DEFAULT_STEM_DICT.put("kind", "kinder");
        }
    }
}

