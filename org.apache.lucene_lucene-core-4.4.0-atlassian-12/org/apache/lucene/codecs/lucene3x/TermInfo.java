/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

@Deprecated
class TermInfo {
    public int docFreq = 0;
    public long freqPointer = 0L;
    public long proxPointer = 0L;
    public int skipOffset;

    public TermInfo() {
    }

    public TermInfo(int df, long fp, long pp) {
        this.docFreq = df;
        this.freqPointer = fp;
        this.proxPointer = pp;
    }

    public TermInfo(TermInfo ti) {
        this.docFreq = ti.docFreq;
        this.freqPointer = ti.freqPointer;
        this.proxPointer = ti.proxPointer;
        this.skipOffset = ti.skipOffset;
    }

    public final void set(int docFreq, long freqPointer, long proxPointer, int skipOffset) {
        this.docFreq = docFreq;
        this.freqPointer = freqPointer;
        this.proxPointer = proxPointer;
        this.skipOffset = skipOffset;
    }

    public final void set(TermInfo ti) {
        this.docFreq = ti.docFreq;
        this.freqPointer = ti.freqPointer;
        this.proxPointer = ti.proxPointer;
        this.skipOffset = ti.skipOffset;
    }
}

