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
package org.apache.lucene.analysis.cz;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.cz.CzechStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

public final class CzechAnalyzer
extends StopwordAnalyzerBase {
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private final CharArraySet stemExclusionTable;

    public static final CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_SET;
    }

    public CzechAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_SET);
    }

    public CzechAnalyzer(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    public CzechAnalyzer(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionTable) {
        super(matchVersion, stopwords);
        this.stemExclusionTable = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionTable));
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
        TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)source);
        result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
        result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords);
        if (this.matchVersion.onOrAfter(Version.LUCENE_31)) {
            if (!this.stemExclusionTable.isEmpty()) {
                result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionTable);
            }
            result = new CzechStemFilter((TokenStream)result);
        }
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)result);
    }

    private static class DefaultSetHolder {
        private static final CharArraySet DEFAULT_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_SET = WordlistLoader.getWordSet(IOUtils.getDecodingReader(CzechAnalyzer.class, (String)CzechAnalyzer.DEFAULT_STOPWORD_FILE, (Charset)IOUtils.CHARSET_UTF_8), "#", Version.LUCENE_CURRENT);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

