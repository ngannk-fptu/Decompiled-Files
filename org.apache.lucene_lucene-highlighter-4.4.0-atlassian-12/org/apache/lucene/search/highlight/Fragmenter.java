/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;

public interface Fragmenter {
    public void start(String var1, TokenStream var2);

    public boolean isNewFragment();
}

