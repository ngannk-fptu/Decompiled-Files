/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.HyphenatedWordsFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class HyphenatedWordsFilterFactory
extends TokenFilterFactory {
    public HyphenatedWordsFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public HyphenatedWordsFilter create(TokenStream input) {
        return new HyphenatedWordsFilter(input);
    }
}

