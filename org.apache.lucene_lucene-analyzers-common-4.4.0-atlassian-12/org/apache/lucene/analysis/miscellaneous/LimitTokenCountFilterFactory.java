/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LimitTokenCountFilterFactory
extends TokenFilterFactory {
    public static final String MAX_TOKEN_COUNT_KEY = "maxTokenCount";
    public static final String CONSUME_ALL_TOKENS_KEY = "consumeAllTokens";
    final int maxTokenCount;
    final boolean consumeAllTokens;

    public LimitTokenCountFilterFactory(Map<String, String> args) {
        super(args);
        this.maxTokenCount = this.requireInt(args, MAX_TOKEN_COUNT_KEY);
        this.consumeAllTokens = this.getBoolean(args, CONSUME_ALL_TOKENS_KEY, false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new LimitTokenCountFilter(input, this.maxTokenCount, this.consumeAllTokens);
    }
}

