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
import org.apache.lucene.analysis.ja.JapaneseReadingFormFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class JapaneseReadingFormFilterFactory
extends TokenFilterFactory {
    private static final String ROMAJI_PARAM = "useRomaji";
    private final boolean useRomaji;

    public JapaneseReadingFormFilterFactory(Map<String, String> args) {
        super(args);
        this.useRomaji = this.getBoolean(args, ROMAJI_PARAM, false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TokenStream create(TokenStream input) {
        return new JapaneseReadingFormFilter(input, this.useRomaji);
    }
}

