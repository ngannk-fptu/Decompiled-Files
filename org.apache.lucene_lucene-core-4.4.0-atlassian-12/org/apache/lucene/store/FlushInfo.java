/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

public class FlushInfo {
    public final int numDocs;
    public final long estimatedSegmentSize;

    public FlushInfo(int numDocs, long estimatedSegmentSize) {
        this.numDocs = numDocs;
        this.estimatedSegmentSize = estimatedSegmentSize;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.estimatedSegmentSize ^ this.estimatedSegmentSize >>> 32);
        result = 31 * result + this.numDocs;
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
        FlushInfo other = (FlushInfo)obj;
        if (this.estimatedSegmentSize != other.estimatedSegmentSize) {
            return false;
        }
        return this.numDocs == other.numDocs;
    }

    public String toString() {
        return "FlushInfo [numDocs=" + this.numDocs + ", estimatedSegmentSize=" + this.estimatedSegmentSize + "]";
    }
}

