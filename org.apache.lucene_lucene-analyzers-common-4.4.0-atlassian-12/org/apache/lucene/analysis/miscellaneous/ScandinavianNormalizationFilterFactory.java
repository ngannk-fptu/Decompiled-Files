/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ScandinavianNormalizationFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ScandinavianNormalizationFilterFactory
extends TokenFilterFactory {
    public ScandinavianNormalizationFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public ScandinavianNormalizationFilter create(TokenStream input) {
        return new ScandinavianNormalizationFilter(input);
    }
}

