/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.highlight.Fragmenter;

public class NullFragmenter
implements Fragmenter {
    @Override
    public void start(String s, TokenStream tokenStream) {
    }

    @Override
    public boolean isNewFragment() {
        return false;
    }
}

