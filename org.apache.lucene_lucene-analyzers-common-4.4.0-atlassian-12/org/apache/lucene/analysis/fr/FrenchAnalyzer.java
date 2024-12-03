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
package org.apache.lucene.analysis.fr;

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
import org.apache.lucene.analysis.fr.FrenchLightStemFilter;
import org.apache.lucene.analysis.fr.FrenchStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.FrenchStemmer;

public final class FrenchAnalyzer
extends StopwordAnalyzerBase {
    @Deprecated
    private static final String[] FRENCH_STOP_WORDS = new String[]{"a", "afin", "ai", "ainsi", "apr\u00e8s", "attendu", "au", "aujourd", "auquel", "aussi", "autre", "autres", "aux", "auxquelles", "auxquels", "avait", "avant", "avec", "avoir", "c", "car", "ce", "ceci", "cela", "celle", "celles", "celui", "cependant", "certain", "certaine", "certaines", "certains", "ces", "cet", "cette", "ceux", "chez", "ci", "combien", "comme", "comment", "concernant", "contre", "d", "dans", "de", "debout", "dedans", "dehors", "del\u00e0", "depuis", "derri\u00e8re", "des", "d\u00e9sormais", "desquelles", "desquels", "dessous", "dessus", "devant", "devers", "devra", "divers", "diverse", "diverses", "doit", "donc", "dont", "du", "duquel", "durant", "d\u00e8s", "elle", "elles", "en", "entre", "environ", "est", "et", "etc", "etre", "eu", "eux", "except\u00e9", "hormis", "hors", "h\u00e9las", "hui", "il", "ils", "j", "je", "jusqu", "jusque", "l", "la", "laquelle", "le", "lequel", "les", "lesquelles", "lesquels", "leur", "leurs", "lorsque", "lui", "l\u00e0", "ma", "mais", "malgr\u00e9", "me", "merci", "mes", "mien", "mienne", "miennes", "miens", "moi", "moins", "mon", "moyennant", "m\u00eame", "m\u00eames", "n", "ne", "ni", "non", "nos", "notre", "nous", "n\u00e9anmoins", "n\u00f4tre", "n\u00f4tres", "on", "ont", "ou", "outre", "o\u00f9", "par", "parmi", "partant", "pas", "pass\u00e9", "pendant", "plein", "plus", "plusieurs", "pour", "pourquoi", "proche", "pr\u00e8s", "puisque", "qu", "quand", "que", "quel", "quelle", "quelles", "quels", "qui", "quoi", "quoique", "revoici", "revoil\u00e0", "s", "sa", "sans", "sauf", "se", "selon", "seront", "ses", "si", "sien", "sienne", "siennes", "siens", "sinon", "soi", "soit", "son", "sont", "sous", "suivant", "sur", "ta", "te", "tes", "tien", "tienne", "tiennes", "tiens", "toi", "ton", "tous", "tout", "toute", "toutes", "tu", "un", "une", "va", "vers", "voici", "voil\u00e0", "vos", "votre", "vous", "vu", "v\u00f4tre", "v\u00f4tres", "y", "\u00e0", "\u00e7a", "\u00e8s", "\u00e9t\u00e9", "\u00eatre", "\u00f4"};
    public static final String DEFAULT_STOPWORD_FILE = "french_stop.txt";
    public static final CharArraySet DEFAULT_ARTICLES = CharArraySet.unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList("l", "m", "t", "qu", "n", "s", "j", "d", "c", "jusqu", "quoiqu", "lorsqu", "puisqu"), true));
    private final CharArraySet excltable;

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public FrenchAnalyzer(Version matchVersion) {
        this(matchVersion, matchVersion.onOrAfter(Version.LUCENE_31) ? DefaultSetHolder.DEFAULT_STOP_SET : DefaultSetHolder.DEFAULT_STOP_SET_30);
    }

    public FrenchAnalyzer(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    public FrenchAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclutionSet) {
        super(matchVersion, stopwords);
        this.excltable = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclutionSet));
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
            StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
            TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
            result = new ElisionFilter((TokenStream)result, DEFAULT_ARTICLES);
            result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
            result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
            if (!this.excltable.isEmpty()) {
                result = new SetKeywordMarkerFilter((TokenStream)result, this.excltable);
            }
            result = this.matchVersion.onOrAfter(Version.LUCENE_36) ? new FrenchLightStemFilter((TokenStream)result) : new SnowballFilter((TokenStream)result, new FrenchStemmer());
            return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
        }
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
        TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        if (!this.excltable.isEmpty()) {
            result = new SetKeywordMarkerFilter((TokenStream)result, this.excltable);
        }
        result = new FrenchStemFilter((TokenStream)result);
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)new LowerCaseFilter(this.matchVersion, (TokenStream)result));
    }

    static /* synthetic */ String[] access$000() {
        return FRENCH_STOP_WORDS;
    }

    private static class DefaultSetHolder {
        @Deprecated
        static final CharArraySet DEFAULT_STOP_SET_30 = CharArraySet.unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList(FrenchAnalyzer.access$000()), false));
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, (String)FrenchAnalyzer.DEFAULT_STOPWORD_FILE, (Charset)IOUtils.CHARSET_UTF_8), Version.LUCENE_CURRENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

