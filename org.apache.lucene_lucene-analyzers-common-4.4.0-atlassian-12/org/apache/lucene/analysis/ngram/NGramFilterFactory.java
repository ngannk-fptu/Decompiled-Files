/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.ngram;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class NGramFilterFactory
extends TokenFilterFactory {
    private final int maxGramSize;
    private final int minGramSize;

    public NGramFilterFactory(Map<String, String> args) {
        super(args);
        this.minGramSize = this.getInt(args, "minGramSize", 1);
        this.maxGramSize = this.getInt(args, "maxGramSize", 2);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public NGramTokenFilter create(TokenStream input) {
        return new NGramTokenFilter(this.luceneMatchVersion, input, this.minGramSize, this.maxGramSize);
    }
}

