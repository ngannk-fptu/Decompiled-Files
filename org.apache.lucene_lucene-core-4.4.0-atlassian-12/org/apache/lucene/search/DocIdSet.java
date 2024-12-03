/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;

public abstract class DocIdSet {
    public abstract DocIdSetIterator iterator() throws IOException;

    public Bits bits() throws IOException {
        return null;
    }

    public boolean isCacheable() {
        return false;
    }
}

