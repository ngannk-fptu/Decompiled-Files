/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.commongrams;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CommonGramsFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private CharArraySet commonWords;
    private final String commonWordFiles;
    private final String format;
    private final boolean ignoreCase;

    public CommonGramsFilterFactory(Map<String, String> args) {
        super(args);
        this.commonWordFiles = this.get(args, "words");
        this.format = this.get(args, "format");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        this.commonWords = this.commonWordFiles != null ? ("snowball".equalsIgnoreCase(this.format) ? this.getSnowballWordSet(loader, this.commonWordFiles, this.ignoreCase) : this.getWordSet(loader, this.commonWordFiles, this.ignoreCase)) : StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    public CharArraySet getCommonWords() {
        return this.commonWords;
    }

    public TokenFilter create(TokenStream input) {
        CommonGramsFilter commonGrams = new CommonGramsFilter(this.luceneMatchVersion, input, this.commonWords);
        return commonGrams;
    }
}

