/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.standard;

import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ClassicFilterFactory
extends TokenFilterFactory {
    public ClassicFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TokenFilter create(TokenStream input) {
        return new ClassicFilter(input);
    }
}

