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
package org.apache.lucene.analysis.cjk;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.cjk.CJKTokenizer;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class CJKAnalyzer
extends StopwordAnalyzerBase {
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public CJKAnalyzer(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET);
    }

    public CJKAnalyzer(Version matchVersion, CharArraySet stopwords) {
        super(matchVersion, stopwords);
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        if (this.matchVersion.onOrAfter(Version.LUCENE_36)) {
            StandardTokenizer source = new StandardTokenizer(this.matchVersion, reader);
            TokenFilter result = new CJKWidthFilter((TokenStream)source);
            result = new LowerCaseFilter(this.matchVersion, (TokenStream)result);
            result = new CJKBigramFilter((TokenStream)result);
            return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)new StopFilter(this.matchVersion, (TokenStream)result, this.stopwords));
        }
        CJKTokenizer source = new CJKTokenizer(reader);
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)new StopFilter(this.matchVersion, (TokenStream)source, this.stopwords));
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = CJKAnalyzer.loadStopwordSet(false, CJKAnalyzer.class, CJKAnalyzer.DEFAULT_STOPWORD_FILE, "#");
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}

