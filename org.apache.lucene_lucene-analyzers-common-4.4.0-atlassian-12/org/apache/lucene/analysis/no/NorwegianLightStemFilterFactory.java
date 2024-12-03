/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.no;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.no.NorwegianLightStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class NorwegianLightStemFilterFactory
extends TokenFilterFactory {
    private final int flags;

    public NorwegianLightStemFilterFactory(Map<String, String> args) {
        super(args);
        String variant = this.get(args, "variant");
        if (variant == null || "nb".equals(variant)) {
            this.flags = 1;
        } else if ("nn".equals(variant)) {
            this.flags = 2;
        } else if ("no".equals(variant)) {
            this.flags = 3;
        } else {
            throw new IllegalArgumentException("invalid variant: " + variant);
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new NorwegianLightStemFilter(input, this.flags);
    }
}

