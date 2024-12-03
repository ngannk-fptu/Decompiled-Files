/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.th;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.th.ThaiWordFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ThaiWordFilterFactory
extends TokenFilterFactory {
    public ThaiWordFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public ThaiWordFilter create(TokenStream input) {
        return new ThaiWordFilter(this.luceneMatchVersion, input);
    }
}

