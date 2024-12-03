/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.br;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class BrazilianStemFilterFactory
extends TokenFilterFactory {
    public BrazilianStemFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public BrazilianStemFilter create(TokenStream in) {
        return new BrazilianStemFilter(in);
    }
}

