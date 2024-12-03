/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.LimitTokenPositionFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LimitTokenPositionFilterFactory
extends TokenFilterFactory {
    public static final String MAX_TOKEN_POSITION_KEY = "maxTokenPosition";
    public static final String CONSUME_ALL_TOKENS_KEY = "consumeAllTokens";
    final int maxTokenPosition;
    final boolean consumeAllTokens;

    public LimitTokenPositionFilterFactory(Map<String, String> args) {
        super(args);
        this.maxTokenPosition = this.requireInt(args, MAX_TOKEN_POSITION_KEY);
        this.consumeAllTokens = this.getBoolean(args, CONSUME_ALL_TOKENS_KEY, false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new LimitTokenPositionFilter(input, this.maxTokenPosition, this.consumeAllTokens);
    }
}

