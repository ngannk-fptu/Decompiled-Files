/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.ar;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ArabicStemFilterFactory
extends TokenFilterFactory {
    public ArabicStemFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public ArabicStemFilter create(TokenStream input) {
        return new ArabicStemFilter(input);
    }
}

