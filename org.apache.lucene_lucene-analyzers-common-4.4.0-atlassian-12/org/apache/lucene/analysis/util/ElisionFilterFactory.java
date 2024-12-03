/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ElisionFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware,
MultiTermAwareComponent {
    private final String articlesFile;
    private final boolean ignoreCase;
    private CharArraySet articles;

    public ElisionFilterFactory(Map<String, String> args) {
        super(args);
        this.articlesFile = this.get(args, "articles");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        this.articles = this.articlesFile == null ? FrenchAnalyzer.DEFAULT_ARTICLES : this.getWordSet(loader, this.articlesFile, this.ignoreCase);
    }

    public ElisionFilter create(TokenStream input) {
        return new ElisionFilter(input, this.articles);
    }

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}

