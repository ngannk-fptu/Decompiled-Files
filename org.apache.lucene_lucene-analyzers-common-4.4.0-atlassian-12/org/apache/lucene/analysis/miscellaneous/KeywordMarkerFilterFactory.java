/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PatternKeywordMarkerFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class KeywordMarkerFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    public static final String PROTECTED_TOKENS = "protected";
    public static final String PATTERN = "pattern";
    private final String wordFiles;
    private final String stringPattern;
    private final boolean ignoreCase;
    private Pattern pattern;
    private CharArraySet protectedWords;

    public KeywordMarkerFilterFactory(Map<String, String> args) {
        super(args);
        this.wordFiles = this.get(args, PROTECTED_TOKENS);
        this.stringPattern = this.get(args, PATTERN);
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        if (this.wordFiles != null) {
            this.protectedWords = this.getWordSet(loader, this.wordFiles, this.ignoreCase);
        }
        if (this.stringPattern != null) {
            this.pattern = this.ignoreCase ? Pattern.compile(this.stringPattern, 66) : Pattern.compile(this.stringPattern);
        }
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    @Override
    public TokenStream create(TokenStream input) {
        if (this.pattern != null) {
            input = new PatternKeywordMarkerFilter((TokenStream)input, this.pattern);
        }
        if (this.protectedWords != null) {
            input = new SetKeywordMarkerFilter((TokenStream)input, this.protectedWords);
        }
        return input;
    }
}

