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
package org.apache.lucene.analysis.ru;

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
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.ru.RussianLetterTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.RussianStemmer;

public final class RussianAnalyzer
extends StopwordAnalyzerBase {
    @Deprecated
    private static final String[] RUSSIAN_STOP_WORDS_30 = new String[]{"\u0430", "\u0431\u0435\u0437", "\u0431\u043e\u043b\u0435\u0435", "\u0431\u044b", "\u0431\u044b\u043b", "\u0431\u044b\u043b\u0430", "\u0431\u044b\u043b\u0438", "\u0431\u044b\u043b\u043e", "\u0431\u044b\u0442\u044c", "\u0432", "\u0432\u0430\u043c", "\u0432\u0430\u0441", "\u0432\u0435\u0441\u044c", "\u0432\u043e", "\u0432\u043e\u0442", "\u0432\u0441\u0435", "\u0432\u0441\u0435\u0433\u043e", "\u0432\u0441\u0435\u0445", "\u0432\u044b", "\u0433\u0434\u0435", "\u0434\u0430", "\u0434\u0430\u0436\u0435", "\u0434\u043b\u044f", "\u0434\u043e", "\u0435\u0433\u043e", "\u0435\u0435", "\u0435\u0439", "\u0435\u044e", "\u0435\u0441\u043b\u0438", "\u0435\u0441\u0442\u044c", "\u0435\u0449\u0435", "\u0436\u0435", "\u0437\u0430", "\u0437\u0434\u0435\u0441\u044c", "\u0438", "\u0438\u0437", "\u0438\u043b\u0438", "\u0438\u043c", "\u0438\u0445", "\u043a", "\u043a\u0430\u043a", "\u043a\u043e", "\u043a\u043e\u0433\u0434\u0430", "\u043a\u0442\u043e", "\u043b\u0438", "\u043b\u0438\u0431\u043e", "\u043c\u043d\u0435", "\u043c\u043e\u0436\u0435\u0442", "\u043c\u044b", "\u043d\u0430", "\u043d\u0430\u0434\u043e", "\u043d\u0430\u0448", "\u043d\u0435", "\u043d\u0435\u0433\u043e", "\u043d\u0435\u0435", "\u043d\u0435\u0442", "\u043d\u0438", "\u043d\u0438\u0445", "\u043d\u043e", "\u043d\u0443", "\u043e", "\u043e\u0431", "\u043e\u0434\u043d\u0430\u043a\u043e", "\u043e\u043d", "\u043e\u043d\u0430", "\u043e\u043d\u0438", "\u043e\u043d\u043e", "\u043e\u0442", "\u043e\u0447\u0435\u043d\u044c", "\u043f\u043e", "\u043f\u043e\u0434", "\u043f\u0440\u0438", "\u0441", "\u0441\u043e", "\u0442\u0430\u043a", "\u0442\u0430\u043a\u0436\u0435", "\u0442\u0430\u043a\u043e\u0439", "\u0442\u0430\u043c", "\u0442\u0435", "\u0442\u0435\u043c", "\u0442\u043e", "\u0442\u043e\u0433\u043e", "\u0442\u043e\u0436\u0435", "\u0442\u043e\u0439", "\u0442\u043e\u043b\u044c\u043a\u043e", "\u0442\u043e\u043c", "\u0442\u044b", "\u0443", "\u0443\u0436\u0435", "\u0445\u043e\u0442\u044f", "\u0447\u0435\u0433\u043e", "\u0447\u0435\u0439", "\u0447\u0435\u043c", "\u0447\u0442\u043e", "\u0447\u0442\u043e\u0431\u044b", "\u0447\u044c\u0435", "\u0447\u044c\u044f", "\u044d\u0442\u0430", "\u044d\u0442\u0438", "\u044d\u0442\u043e", "\u044f"};
    public static final String DEFAULT_STOPWORD_FILE = "russian_stop.txt";
    private final CharArraySet stemExclusionSet;

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public RussianAnalyzer(Version matchVersion) {
        this(matchVersion, matchVersion.onOrAfter(Version.LUCENE_31) ? DefaultSetHolder.DEFAULT_STOP_SET : DefaultSetHolder.DEFAULT_STOP_SET_30);
    }

    public RussianAnalyzer(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    public RussianAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(matchVersion, stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionSet));
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
            StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
            TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
            result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
            result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
            if (!this.stemExclusionSet.isEmpty()) {
                result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionSet);
            }
            result = new SnowballFilter((TokenStream)result, new RussianStemmer());
            return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
        }
        RussianLetterTokenizer source = new RussianLetterTokenizer(this.matchVersion, reader);
        TokenFilter result = new LowerCaseFilter(this.matchVersion, (TokenStream)source);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        if (!this.stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionSet);
        }
        result = new SnowballFilter((TokenStream)result, new RussianStemmer());
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }

    static /* synthetic */ String[] access$000() {
        return RUSSIAN_STOP_WORDS_30;
    }

    private static class DefaultSetHolder {
        @Deprecated
        static final CharArraySet DEFAULT_STOP_SET_30 = CharArraySet.unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList(RussianAnalyzer.access$000()), false));
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, (String)RussianAnalyzer.DEFAULT_STOPWORD_FILE, (Charset)IOUtils.CHARSET_UTF_8), Version.LUCENE_CURRENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set", ex);
            }
        }
    }
}

