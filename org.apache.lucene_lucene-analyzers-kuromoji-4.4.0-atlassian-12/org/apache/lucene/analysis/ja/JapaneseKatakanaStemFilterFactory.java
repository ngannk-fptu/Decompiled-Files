/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.util.TokenFilterFactory
 */
package org.apache.lucene.analysis.ja;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseKatakanaStemFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class JapaneseKatakanaStemFilterFactory
extends TokenFilterFactory {
    private static final String MINIMUM_LENGTH_PARAM = "minimumLength";
    private final int minimumLength;

    public JapaneseKatakanaStemFilterFactory(Map<String, String> args) {
        super(args);
        this.minimumLength = this.getInt(args, MINIMUM_LENGTH_PARAM, 4);
        if (this.minimumLength < 2) {
            throw new IllegalArgumentException("Illegal minimumLength " + this.minimumLength + " (must be 2 or greater)");
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TokenStream create(TokenStream input) {
        return new JapaneseKatakanaStemFilter(input, this.minimumLength);
    }
}

