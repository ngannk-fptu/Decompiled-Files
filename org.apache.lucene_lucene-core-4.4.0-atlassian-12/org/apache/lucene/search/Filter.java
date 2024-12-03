/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.Bits;

public abstract class Filter {
    public abstract DocIdSet getDocIdSet(AtomicReaderContext var1, Bits var2) throws IOException;
}

