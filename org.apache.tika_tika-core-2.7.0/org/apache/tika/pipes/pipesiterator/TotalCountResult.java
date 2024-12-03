/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.pipesiterator;

public class TotalCountResult {
    public static TotalCountResult UNSUPPORTED = new TotalCountResult(-1L, STATUS.UNSUPPORTED);
    private long totalCount;
    private STATUS status;

    public TotalCountResult() {
        this.totalCount = 0L;
        this.status = STATUS.NOT_COMPLETED;
    }

    public TotalCountResult(long totalCount, STATUS status) {
        this.totalCount = totalCount;
        this.status = status;
    }

    public long getTotalCount() {
        return this.totalCount;
    }

    public STATUS getStatus() {
        return this.status;
    }

    public String toString() {
        return "TotalCountResult{totalCount=" + this.totalCount + ", status=" + (Object)((Object)this.status) + '}';
    }

    public static enum STATUS {
        UNSUPPORTED,
        EXCEPTION,
        NOT_COMPLETED,
        COMPLETED;

    }
}

