/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

public final class ReaderSlice {
    public static final ReaderSlice[] EMPTY_ARRAY = new ReaderSlice[0];
    public final int start;
    public final int length;
    public final int readerIndex;

    public ReaderSlice(int start, int length, int readerIndex) {
        this.start = start;
        this.length = length;
        this.readerIndex = readerIndex;
    }

    public String toString() {
        return "slice start=" + this.start + " length=" + this.length + " readerIndex=" + this.readerIndex;
    }
}

