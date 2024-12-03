/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LengthFilterFactory
extends TokenFilterFactory {
    final int min;
    final int max;
    final boolean enablePositionIncrements;
    public static final String MIN_KEY = "min";
    public static final String MAX_KEY = "max";

    public LengthFilterFactory(Map<String, String> args) {
        super(args);
        this.min = this.requireInt(args, MIN_KEY);
        this.max = this.requireInt(args, MAX_KEY);
        this.enablePositionIncrements = this.getBoolean(args, "enablePositionIncrements", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public LengthFilter create(TokenStream input) {
        LengthFilter filter = new LengthFilter(this.luceneMatchVersion, this.enablePositionIncrements, input, this.min, this.max);
        return filter;
    }
}

