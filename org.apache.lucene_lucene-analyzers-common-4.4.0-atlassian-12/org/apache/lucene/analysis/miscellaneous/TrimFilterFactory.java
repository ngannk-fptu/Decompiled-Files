/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TrimFilterFactory
extends TokenFilterFactory {
    protected final boolean updateOffsets;

    public TrimFilterFactory(Map<String, String> args) {
        super(args);
        this.updateOffsets = this.getBoolean(args, "updateOffsets", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public TrimFilter create(TokenStream input) {
        TrimFilter filter = new TrimFilter(this.luceneMatchVersion, input, this.updateOffsets);
        return filter;
    }
}

