/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.de;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class GermanStemFilterFactory
extends TokenFilterFactory {
    public GermanStemFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public GermanStemFilter create(TokenStream in) {
        return new GermanStemFilter(in);
    }
}

