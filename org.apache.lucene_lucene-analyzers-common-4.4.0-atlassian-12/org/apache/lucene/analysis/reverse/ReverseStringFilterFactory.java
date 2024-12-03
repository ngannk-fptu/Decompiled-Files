/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.reverse;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.reverse.ReverseStringFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ReverseStringFilterFactory
extends TokenFilterFactory {
    public ReverseStringFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public ReverseStringFilter create(TokenStream in) {
        return new ReverseStringFilter(this.luceneMatchVersion, in);
    }
}

