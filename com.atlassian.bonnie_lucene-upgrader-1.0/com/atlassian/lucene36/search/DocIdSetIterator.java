/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import java.io.IOException;

public abstract class DocIdSetIterator {
    public static final int NO_MORE_DOCS = Integer.MAX_VALUE;

    public abstract int docID();

    public abstract int nextDoc() throws IOException;

    public abstract int advance(int var1) throws IOException;
}

