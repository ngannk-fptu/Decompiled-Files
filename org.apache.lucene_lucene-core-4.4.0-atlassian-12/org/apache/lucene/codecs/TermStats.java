/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

public class TermStats {
    public final int docFreq;
    public final long totalTermFreq;

    public TermStats(int docFreq, long totalTermFreq) {
        this.docFreq = docFreq;
        this.totalTermFreq = totalTermFreq;
    }
}

