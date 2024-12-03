/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.monitor.LocalRecordStoreStats;

public class LocalRecordStoreStatsImpl
implements LocalRecordStoreStats {
    private long hits;
    private long lastAccess;
    private long lastUpdate;

    @Override
    public long getHits() {
        return this.hits;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccess;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdate;
    }

    @Override
    public void increaseHits() {
        ++this.hits;
    }

    @Override
    public void increaseHits(long hits) {
        this.hits += hits;
    }

    @Override
    public void decreaseHits(long hits) {
        this.hits -= hits;
    }

    @Override
    public void setLastAccessTime(long time) {
        this.lastAccess = Math.max(this.lastAccess, time);
    }

    @Override
    public void setLastUpdateTime(long time) {
        this.lastUpdate = Math.max(this.lastUpdate, time);
    }

    public void reset() {
        this.hits = 0L;
        this.lastAccess = 0L;
        this.lastUpdate = 0L;
    }
}

