/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.LogMergePolicy;
import com.atlassian.lucene36.index.SegmentInfo;
import java.io.IOException;

public class LogByteSizeMergePolicy
extends LogMergePolicy {
    public static final double DEFAULT_MIN_MERGE_MB = 1.6;
    public static final double DEFAULT_MAX_MERGE_MB = 2048.0;
    public static final double DEFAULT_MAX_MERGE_MB_FOR_FORCED_MERGE = 9.223372036854776E18;

    public LogByteSizeMergePolicy() {
        this.minMergeSize = 0x199999L;
        this.maxMergeSize = 0x80000000L;
        this.maxMergeSizeForForcedMerge = Long.MAX_VALUE;
    }

    protected long size(SegmentInfo info) throws IOException {
        return this.sizeBytes(info);
    }

    public void setMaxMergeMB(double mb) {
        this.maxMergeSize = (long)(mb * 1024.0 * 1024.0);
    }

    public double getMaxMergeMB() {
        return (double)this.maxMergeSize / 1024.0 / 1024.0;
    }

    @Deprecated
    public void setMaxMergeMBForOptimize(double mb) {
        this.setMaxMergeMBForForcedMerge(mb);
    }

    public void setMaxMergeMBForForcedMerge(double mb) {
        this.maxMergeSizeForForcedMerge = (long)(mb * 1024.0 * 1024.0);
    }

    @Deprecated
    public double getMaxMergeMBForOptimize() {
        return this.getMaxMergeMBForForcedMerge();
    }

    public double getMaxMergeMBForForcedMerge() {
        return (double)this.maxMergeSizeForForcedMerge / 1024.0 / 1024.0;
    }

    public void setMinMergeMB(double mb) {
        this.minMergeSize = (long)(mb * 1024.0 * 1024.0);
    }

    public double getMinMergeMB() {
        return (double)this.minMergeSize / 1024.0 / 1024.0;
    }
}

