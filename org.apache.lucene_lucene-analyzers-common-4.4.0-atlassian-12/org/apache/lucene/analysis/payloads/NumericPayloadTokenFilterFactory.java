/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.payloads;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.NumericPayloadTokenFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class NumericPayloadTokenFilterFactory
extends TokenFilterFactory {
    private final float payload;
    private final String typeMatch;

    public NumericPayloadTokenFilterFactory(Map<String, String> args) {
        super(args);
        this.payload = this.requireFloat(args, "payload");
        this.typeMatch = this.require(args, "typeMatch");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public NumericPayloadTokenFilter create(TokenStream input) {
        return new NumericPayloadTokenFilter(input, this.payload, this.typeMatch);
    }
}

