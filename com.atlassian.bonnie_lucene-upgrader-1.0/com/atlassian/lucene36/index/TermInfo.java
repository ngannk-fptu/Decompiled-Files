/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

class TermInfo {
    int docFreq = 0;
    long freqPointer = 0L;
    long proxPointer = 0L;
    int skipOffset;

    TermInfo() {
    }

    TermInfo(int df, long fp, long pp) {
        this.docFreq = df;
        this.freqPointer = fp;
        this.proxPointer = pp;
    }

    TermInfo(TermInfo ti) {
        this.docFreq = ti.docFreq;
        this.freqPointer = ti.freqPointer;
        this.proxPointer = ti.proxPointer;
        this.skipOffset = ti.skipOffset;
    }

    final void set(int docFreq, long freqPointer, long proxPointer, int skipOffset) {
        this.docFreq = docFreq;
        this.freqPointer = freqPointer;
        this.proxPointer = proxPointer;
        this.skipOffset = skipOffset;
    }

    final void set(TermInfo ti) {
        this.docFreq = ti.docFreq;
        this.freqPointer = ti.freqPointer;
        this.proxPointer = ti.proxPointer;
        this.skipOffset = ti.skipOffset;
    }
}

