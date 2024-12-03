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
import org.apache.lucene.analysis.ja.JapaneseBaseFormFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class JapaneseBaseFormFilterFactory
extends TokenFilterFactory {
    public JapaneseBaseFormFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TokenStream create(TokenStream input) {
        return new JapaneseBaseFormFilter(input);
    }
}

