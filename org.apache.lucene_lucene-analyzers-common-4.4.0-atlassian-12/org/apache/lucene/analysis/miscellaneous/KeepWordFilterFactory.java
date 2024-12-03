/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.KeepWordFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class KeepWordFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private final boolean ignoreCase;
    private final boolean enablePositionIncrements;
    private final String wordFiles;
    private CharArraySet words;

    public KeepWordFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        this.wordFiles = this.get(args, "words");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        this.enablePositionIncrements = this.getBoolean(args, "enablePositionIncrements", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        if (this.wordFiles != null) {
            this.words = this.getWordSet(loader, this.wordFiles, this.ignoreCase);
        }
    }

    public boolean isEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    public CharArraySet getWords() {
        return this.words;
    }

    @Override
    public TokenStream create(TokenStream input) {
        if (this.words == null) {
            return input;
        }
        KeepWordFilter filter = new KeepWordFilter(this.luceneMatchVersion, this.enablePositionIncrements, input, this.words);
        return filter;
    }
}

