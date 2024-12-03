/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.sv;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.sv.SwedishLightStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class SwedishLightStemFilterFactory
extends TokenFilterFactory {
    public SwedishLightStemFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new SwedishLightStemFilter(input);
    }
}

