/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.core;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StopFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private CharArraySet stopWords;
    private final String stopWordFiles;
    private final String format;
    private final boolean ignoreCase;
    private final boolean enablePositionIncrements;

    public StopFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        this.stopWordFiles = this.get(args, "words");
        this.format = this.get(args, "format");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        this.enablePositionIncrements = this.getBoolean(args, "enablePositionIncrements", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        this.stopWords = this.stopWordFiles != null ? ("snowball".equalsIgnoreCase(this.format) ? this.getSnowballWordSet(loader, this.stopWordFiles, this.ignoreCase) : this.getWordSet(loader, this.stopWordFiles, this.ignoreCase)) : new CharArraySet(this.luceneMatchVersion, StopAnalyzer.ENGLISH_STOP_WORDS_SET, this.ignoreCase);
    }

    public boolean isEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    public CharArraySet getStopWords() {
        return this.stopWords;
    }

    @Override
    public TokenStream create(TokenStream input) {
        StopFilter stopFilter = new StopFilter(this.luceneMatchVersion, input, this.stopWords);
        stopFilter.setEnablePositionIncrements(this.enablePositionIncrements);
        return stopFilter;
    }
}

