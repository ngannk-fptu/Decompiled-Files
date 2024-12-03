/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

public interface LocalRecordStoreStats {
    public long getHits();

    public long getLastAccessTime();

    public long getLastUpdateTime();

    public void increaseHits();

    public void increaseHits(long var1);

    public void decreaseHits(long var1);

    public void setLastAccessTime(long var1);

    public void setLastUpdateTime(long var1);
}

