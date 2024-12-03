/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

public class MergeInfo {
    public final int totalDocCount;
    public final long estimatedMergeBytes;
    public final boolean isExternal;
    public final int mergeMaxNumSegments;

    public MergeInfo(int totalDocCount, long estimatedMergeBytes, boolean isExternal, int mergeMaxNumSegments) {
        this.totalDocCount = totalDocCount;
        this.estimatedMergeBytes = estimatedMergeBytes;
        this.isExternal = isExternal;
        this.mergeMaxNumSegments = mergeMaxNumSegments;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.estimatedMergeBytes ^ this.estimatedMergeBytes >>> 32);
        result = 31 * result + (this.isExternal ? 1231 : 1237);
        result = 31 * result + this.mergeMaxNumSegments;
        result = 31 * result + this.totalDocCount;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MergeInfo other = (MergeInfo)obj;
        if (this.estimatedMergeBytes != other.estimatedMergeBytes) {
            return false;
        }
        if (this.isExternal != other.isExternal) {
            return false;
        }
        if (this.mergeMaxNumSegments != other.mergeMaxNumSegments) {
            return false;
        }
        return this.totalDocCount == other.totalDocCount;
    }

    public String toString() {
        return "MergeInfo [totalDocCount=" + this.totalDocCount + ", estimatedMergeBytes=" + this.estimatedMergeBytes + ", isExternal=" + this.isExternal + ", mergeMaxNumSegments=" + this.mergeMaxNumSegments + "]";
    }
}

