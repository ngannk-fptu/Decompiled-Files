/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.util.ArrayUtil;

class ParallelPostingsArray {
    static final int BYTES_PER_POSTING = 12;
    final int size;
    final int[] textStarts;
    final int[] intStarts;
    final int[] byteStarts;

    ParallelPostingsArray(int size) {
        this.size = size;
        this.textStarts = new int[size];
        this.intStarts = new int[size];
        this.byteStarts = new int[size];
    }

    int bytesPerPosting() {
        return 12;
    }

    ParallelPostingsArray newInstance(int size) {
        return new ParallelPostingsArray(size);
    }

    final ParallelPostingsArray grow() {
        int newSize = ArrayUtil.oversize(this.size + 1, this.bytesPerPosting());
        ParallelPostingsArray newArray = this.newInstance(newSize);
        this.copyTo(newArray, this.size);
        return newArray;
    }

    void copyTo(ParallelPostingsArray toArray, int numToCopy) {
        System.arraycopy(this.textStarts, 0, toArray.textStarts, 0, numToCopy);
        System.arraycopy(this.intStarts, 0, toArray.intStarts, 0, numToCopy);
        System.arraycopy(this.byteStarts, 0, toArray.byteStarts, 0, numToCopy);
    }
}

