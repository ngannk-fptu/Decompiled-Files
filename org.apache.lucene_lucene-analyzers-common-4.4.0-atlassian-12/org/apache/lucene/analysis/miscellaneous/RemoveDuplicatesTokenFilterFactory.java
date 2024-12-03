/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class RemoveDuplicatesTokenFilterFactory
extends TokenFilterFactory {
    public RemoveDuplicatesTokenFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public RemoveDuplicatesTokenFilter create(TokenStream input) {
        return new RemoveDuplicatesTokenFilter(input);
    }
}

