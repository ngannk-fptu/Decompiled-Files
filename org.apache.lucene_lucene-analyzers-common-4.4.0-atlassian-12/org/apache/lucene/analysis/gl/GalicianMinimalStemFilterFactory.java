/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.gl;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.gl.GalicianMinimalStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class GalicianMinimalStemFilterFactory
extends TokenFilterFactory {
    public GalicianMinimalStemFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new GalicianMinimalStemFilter(input);
    }
}

