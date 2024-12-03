/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.id;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.id.IndonesianStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class IndonesianStemFilterFactory
extends TokenFilterFactory {
    private final boolean stemDerivational;

    public IndonesianStemFilterFactory(Map<String, String> args) {
        super(args);
        this.stemDerivational = this.getBoolean(args, "stemDerivational", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new IndonesianStemFilter(input, this.stemDerivational);
    }
}

