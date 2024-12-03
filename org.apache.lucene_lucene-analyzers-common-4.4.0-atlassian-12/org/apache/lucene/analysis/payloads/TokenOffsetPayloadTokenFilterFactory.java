/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.payloads;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.TokenOffsetPayloadTokenFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TokenOffsetPayloadTokenFilterFactory
extends TokenFilterFactory {
    public TokenOffsetPayloadTokenFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TokenOffsetPayloadTokenFilter create(TokenStream input) {
        return new TokenOffsetPayloadTokenFilter(input);
    }
}

