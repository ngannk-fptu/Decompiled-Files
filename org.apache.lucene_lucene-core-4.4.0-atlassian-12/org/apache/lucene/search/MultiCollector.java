/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public class MultiCollector
extends Collector {
    private final Collector[] collectors;

    public static Collector wrap(Collector ... collectors) {
        int n = 0;
        for (Collector c : collectors) {
            if (c == null) continue;
            ++n;
        }
        if (n == 0) {
            throw new IllegalArgumentException("At least 1 collector must not be null");
        }
        if (n == 1) {
            Collector col = null;
            for (Collector c : collectors) {
                if (c == null) continue;
                col = c;
                break;
            }
            return col;
        }
        if (n == collectors.length) {
            return new MultiCollector(collectors);
        }
        Collector[] colls = new Collector[n];
        n = 0;
        for (Collector c : collectors) {
            if (c == null) continue;
            colls[n++] = c;
        }
        return new MultiCollector(colls);
    }

    private MultiCollector(Collector ... collectors) {
        this.collectors = collectors;
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        for (Collector c : this.collectors) {
            if (c.acceptsDocsOutOfOrder()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void collect(int doc) throws IOException {
        for (Collector c : this.collectors) {
            c.collect(doc);
        }
    }

    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        for (Collector c : this.collectors) {
            c.setNextReader(context);
        }
    }

    @Override
    public void setScorer(Scorer s) throws IOException {
        for (Collector c : this.collectors) {
            c.setScorer(s);
        }
    }
}

