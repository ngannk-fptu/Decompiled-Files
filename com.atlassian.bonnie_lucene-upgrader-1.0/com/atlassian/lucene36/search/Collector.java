/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Scorer;
import java.io.IOException;

public abstract class Collector {
    public abstract void setScorer(Scorer var1) throws IOException;

    public abstract void collect(int var1) throws IOException;

    public abstract void setNextReader(IndexReader var1, int var2) throws IOException;

    public abstract boolean acceptsDocsOutOfOrder();
}

