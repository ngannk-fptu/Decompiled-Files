/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.cjk.CJKWidthFilter
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.StopFilter
 *  org.apache.lucene.analysis.util.CharArraySet
 *  org.apache.lucene.analysis.util.StopwordAnalyzerBase
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.ja.JapaneseBaseFormFilter;
import org.apache.lucene.analysis.ja.JapaneseKatakanaStemFilter;
import org.apache.lucene.analysis.ja.JapanesePartOfSpeechStopFilter;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.dict.UserDictionary;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public class JapaneseAnalyzer
extends StopwordAnalyzerBase {
    private final JapaneseTokenizer.Mode mode;
    private final Set<String> stoptags;
    private final UserDictionary userDict;

    public JapaneseAnalyzer(Version matchVersion) {
        this(matchVersion, null, JapaneseTokenizer.DEFAULT_MODE, DefaultSetHolder.DEFAULT_STOP_SET, DefaultSetHolder.DEFAULT_STOP_TAGS);
    }

    public JapaneseAnalyzer(Version matchVersion, UserDictionary userDict, JapaneseTokenizer.Mode mode, CharArraySet stopwords, Set<String> stoptags) {
        super(matchVersion, stopwords);
        this.userDict = userDict;
        this.mode = mode;
        this.stoptags = stoptags;
    }

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public static Set<String> getDefaultStopTags() {
        return DefaultSetHolder.DEFAULT_STOP_TAGS;
    }

    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        JapaneseTokenizer tokenizer = new JapaneseTokenizer(reader, this.userDict, true, this.mode);
        Object stream = new JapaneseBaseFormFilter((TokenStream)tokenizer);
        stream = new JapanesePartOfSpeechStopFilter(this.matchVersion, (TokenStream)stream, this.stoptags);
        stream = new CJKWidthFilter((TokenStream)stream);
        stream = new StopFilter(this.matchVersion, (TokenStream)stream, this.stopwords);
        stream = new JapaneseKatakanaStemFilter((TokenStream)stream);
        stream = new LowerCaseFilter(this.matchVersion, (TokenStream)stream);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)stream);
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;
        static final Set<String> DEFAULT_STOP_TAGS;

        private DefaultSetHolder() {
        }

        static {
            try {
                DEFAULT_STOP_SET = JapaneseAnalyzer.loadStopwordSet((boolean)true, (Class)JapaneseAnalyzer.class, (String)"stopwords.txt", (String)"#");
                CharArraySet tagset = JapaneseAnalyzer.loadStopwordSet((boolean)false, (Class)JapaneseAnalyzer.class, (String)"stoptags.txt", (String)"#");
                DEFAULT_STOP_TAGS = new HashSet<String>();
                for (Object element : tagset) {
                    char[] chars = (char[])element;
                    DEFAULT_STOP_TAGS.add(new String(chars));
                }
            }
            catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword or stoptag set");
            }
        }
    }
}

