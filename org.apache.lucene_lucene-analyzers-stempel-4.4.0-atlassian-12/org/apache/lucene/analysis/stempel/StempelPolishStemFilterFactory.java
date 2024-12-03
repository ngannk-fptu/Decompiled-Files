/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.util.TokenFilterFactory
 */
package org.apache.lucene.analysis.stempel;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.stempel.StempelFilter;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StempelPolishStemFilterFactory
extends TokenFilterFactory {
    public StempelPolishStemFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TokenStream create(TokenStream input) {
        return new StempelFilter(input, new StempelStemmer(PolishAnalyzer.getDefaultTable()));
    }
}

