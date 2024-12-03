/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Scorer;

public abstract class Collector {
    public abstract void setScorer(Scorer var1) throws IOException;

    public abstract void collect(int var1) throws IOException;

    public abstract void setNextReader(AtomicReaderContext var1) throws IOException;

    public abstract boolean acceptsDocsOutOfOrder();
}

