/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.util.BytesRef;

public class TermStatistics {
    private final BytesRef term;
    private final long docFreq;
    private final long totalTermFreq;

    public TermStatistics(BytesRef term, long docFreq, long totalTermFreq) {
        assert (docFreq >= 0L);
        assert (totalTermFreq == -1L || totalTermFreq >= docFreq);
        this.term = term;
        this.docFreq = docFreq;
        this.totalTermFreq = totalTermFreq;
    }

    public final BytesRef term() {
        return this.term;
    }

    public final long docFreq() {
        return this.docFreq;
    }

    public final long totalTermFreq() {
        return this.totalTermFreq;
    }
}

