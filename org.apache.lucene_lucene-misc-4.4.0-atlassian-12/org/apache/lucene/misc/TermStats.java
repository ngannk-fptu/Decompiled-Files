/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.misc;

import org.apache.lucene.util.BytesRef;

public final class TermStats {
    public BytesRef termtext;
    public String field;
    public int docFreq;
    public long totalTermFreq;

    TermStats(String field, BytesRef termtext, int df) {
        this.termtext = BytesRef.deepCopyOf((BytesRef)termtext);
        this.field = field;
        this.docFreq = df;
    }

    TermStats(String field, BytesRef termtext, int df, long tf) {
        this.termtext = BytesRef.deepCopyOf((BytesRef)termtext);
        this.field = field;
        this.docFreq = df;
        this.totalTermFreq = tf;
    }

    String getTermText() {
        return this.termtext.utf8ToString();
    }

    public String toString() {
        return "TermStats: term=" + this.termtext.utf8ToString() + " docFreq=" + this.docFreq + " totalTermFreq=" + this.totalTermFreq;
    }
}

