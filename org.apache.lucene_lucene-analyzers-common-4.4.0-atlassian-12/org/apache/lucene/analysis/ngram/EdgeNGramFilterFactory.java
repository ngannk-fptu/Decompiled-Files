/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.ngram;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class EdgeNGramFilterFactory
extends TokenFilterFactory {
    private final int maxGramSize;
    private final int minGramSize;
    private final String side;

    public EdgeNGramFilterFactory(Map<String, String> args) {
        super(args);
        this.minGramSize = this.getInt(args, "minGramSize", 1);
        this.maxGramSize = this.getInt(args, "maxGramSize", 1);
        this.side = this.get(args, "side", EdgeNGramTokenFilter.Side.FRONT.getLabel());
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public EdgeNGramTokenFilter create(TokenStream input) {
        return new EdgeNGramTokenFilter(this.luceneMatchVersion, input, this.side, this.minGramSize, this.maxGramSize);
    }
}

