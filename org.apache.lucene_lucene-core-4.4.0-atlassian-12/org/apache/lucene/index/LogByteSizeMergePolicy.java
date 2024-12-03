/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.SegmentInfoPerCommit;

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

    @Override
    protected long size(SegmentInfoPerCommit info) throws IOException {
        return this.sizeBytes(info);
    }

    public void setMaxMergeMB(double mb) {
        this.maxMergeSize = (long)(mb * 1024.0 * 1024.0);
    }

    public double getMaxMergeMB() {
        return (double)this.maxMergeSize / 1024.0 / 1024.0;
    }

    public void setMaxMergeMBForForcedMerge(double mb) {
        this.maxMergeSizeForForcedMerge = (long)(mb * 1024.0 * 1024.0);
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

