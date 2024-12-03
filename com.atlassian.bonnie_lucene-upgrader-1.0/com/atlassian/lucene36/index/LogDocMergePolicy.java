/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.LogMergePolicy;
import com.atlassian.lucene36.index.SegmentInfo;
import java.io.IOException;

public class LogDocMergePolicy
extends LogMergePolicy {
    public static final int DEFAULT_MIN_MERGE_DOCS = 1000;

    public LogDocMergePolicy() {
        this.minMergeSize = 1000L;
        this.maxMergeSize = Long.MAX_VALUE;
        this.maxMergeSizeForForcedMerge = Long.MAX_VALUE;
    }

    protected long size(SegmentInfo info) throws IOException {
        return this.sizeDocs(info);
    }

    public void setMinMergeDocs(int minMergeDocs) {
        this.minMergeSize = minMergeDocs;
    }

    public int getMinMergeDocs() {
        return (int)this.minMergeSize;
    }
}

