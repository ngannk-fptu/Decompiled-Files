/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.SegmentMergeInfo;
import com.atlassian.lucene36.util.PriorityQueue;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class SegmentMergeQueue
extends PriorityQueue<SegmentMergeInfo> {
    SegmentMergeQueue(int size) {
        this.initialize(size);
    }

    @Override
    protected final boolean lessThan(SegmentMergeInfo stiA, SegmentMergeInfo stiB) {
        int comparison = stiA.term.compareTo(stiB.term);
        if (comparison == 0) {
            return stiA.base < stiB.base;
        }
        return comparison < 0;
    }

    final void close() throws IOException {
        while (this.top() != null) {
            ((SegmentMergeInfo)this.pop()).close();
        }
    }
}

