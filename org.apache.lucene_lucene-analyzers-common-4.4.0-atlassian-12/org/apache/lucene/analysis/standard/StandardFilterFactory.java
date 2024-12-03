/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.standard;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StandardFilterFactory
extends TokenFilterFactory {
    public StandardFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public StandardFilter create(TokenStream input) {
        return new StandardFilter(this.luceneMatchVersion, input);
    }
}

