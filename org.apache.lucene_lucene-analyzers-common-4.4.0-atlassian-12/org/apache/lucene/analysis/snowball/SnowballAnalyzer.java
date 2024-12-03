/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.snowball;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tr.TurkishLowerCaseFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

@Deprecated
public final class SnowballAnalyzer
extends Analyzer {
    private String name;
    private CharArraySet stopSet;
    private final Version matchVersion;

    public SnowballAnalyzer(Version matchVersion, String name) {
        this.name = name;
        this.matchVersion = matchVersion;
    }

    public SnowballAnalyzer(Version matchVersion, String name, CharArraySet stopWords) {
        this(matchVersion, name);
        this.stopSet = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stopWords));
    }

    public Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        StandardTokenizer tokenizer = new StandardTokenizer(this.matchVersion, reader);
        TokenFilter result = new StandardFilter(this.matchVersion, (TokenStream)tokenizer);
        if (this.matchVersion.onOrAfter(Version.LUCENE_31) && (this.name.equals("English") || this.name.equals("Porter") || this.name.equals("Lovins"))) {
            result = new EnglishPossessiveFilter((TokenStream)result);
        }
        result = this.matchVersion.onOrAfter(Version.LUCENE_31) && this.name.equals("Turkish") ? new TurkishLowerCaseFilter((TokenStream)result) : new LowerCaseFilter(this.matchVersion, (TokenStream)result);
        if (this.stopSet != null) {
            result = new StopFilter(this.matchVersion, (TokenStream)result, this.stopSet);
        }
        result = new SnowballFilter((TokenStream)result, this.name);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
    }
}

