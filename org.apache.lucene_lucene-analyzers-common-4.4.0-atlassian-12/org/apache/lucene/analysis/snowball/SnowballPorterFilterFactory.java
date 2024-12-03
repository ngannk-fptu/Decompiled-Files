/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.snowball;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.tartarus.snowball.SnowballProgram;

public class SnowballPorterFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    public static final String PROTECTED_TOKENS = "protected";
    private final String language;
    private final String wordFiles;
    private Class<? extends SnowballProgram> stemClass;
    private CharArraySet protectedWords = null;

    public SnowballPorterFilterFactory(Map<String, String> args) {
        super(args);
        this.language = this.get(args, "language", "English");
        this.wordFiles = this.get(args, PROTECTED_TOKENS);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        String className = "org.tartarus.snowball.ext." + this.language + "Stemmer";
        this.stemClass = loader.newInstance(className, SnowballProgram.class).getClass();
        if (this.wordFiles != null) {
            this.protectedWords = this.getWordSet(loader, this.wordFiles, false);
        }
    }

    public TokenFilter create(TokenStream input) {
        SnowballProgram program;
        try {
            program = this.stemClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Error instantiating stemmer for language " + this.language + "from class " + this.stemClass, e);
        }
        if (this.protectedWords != null) {
            input = new SetKeywordMarkerFilter((TokenStream)input, this.protectedWords);
        }
        return new SnowballFilter((TokenStream)input, program);
    }
}

