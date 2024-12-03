/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.cjk;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CJKBigramFilterFactory
extends TokenFilterFactory {
    final int flags;
    final boolean outputUnigrams;

    public CJKBigramFilterFactory(Map<String, String> args) {
        super(args);
        int flags = 0;
        if (this.getBoolean(args, "han", true)) {
            flags |= 1;
        }
        if (this.getBoolean(args, "hiragana", true)) {
            flags |= 2;
        }
        if (this.getBoolean(args, "katakana", true)) {
            flags |= 4;
        }
        if (this.getBoolean(args, "hangul", true)) {
            flags |= 8;
        }
        this.flags = flags;
        this.outputUnigrams = this.getBoolean(args, "outputUnigrams", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new CJKBigramFilter(input, this.flags, this.outputUnigrams);
    }
}

