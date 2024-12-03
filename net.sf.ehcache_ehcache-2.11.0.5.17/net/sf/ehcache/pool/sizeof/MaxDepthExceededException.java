/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.sizeof;

public class MaxDepthExceededException
extends RuntimeException {
    private long measuredSize;

    public MaxDepthExceededException(String msg) {
        super(msg);
    }

    public void addToMeasuredSize(long toAdd) {
        this.measuredSize += toAdd;
    }

    public long getMeasuredSize() {
        return this.measuredSize;
    }
}

