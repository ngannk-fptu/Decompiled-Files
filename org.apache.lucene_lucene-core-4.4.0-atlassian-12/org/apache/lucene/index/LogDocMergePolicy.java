/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.SegmentInfoPerCommit;

public class LogDocMergePolicy
extends LogMergePolicy {
    public static final int DEFAULT_MIN_MERGE_DOCS = 1000;

    public LogDocMergePolicy() {
        this.minMergeSize = 1000L;
        this.maxMergeSize = Long.MAX_VALUE;
        this.maxMergeSizeForForcedMerge = Long.MAX_VALUE;
    }

    @Override
    protected long size(SegmentInfoPerCommit info) throws IOException {
        return this.sizeDocs(info);
    }

    public void setMinMergeDocs(int minMergeDocs) {
        this.minMergeSize = minMergeDocs;
    }

    public int getMinMergeDocs() {
        return (int)this.minMergeSize;
    }
}

