/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.AbstractRecord;
import com.hazelcast.util.Clock;

abstract class AbstractRecordWithStats<V>
extends AbstractRecord<V> {
    private int lastStoredTime = -1;
    private int expirationTime = -1;

    AbstractRecordWithStats() {
    }

    @Override
    public final void onStore() {
        this.lastStoredTime = this.stripBaseTime(Clock.currentTimeMillis());
    }

    @Override
    public long getCost() {
        int numberOfIntFields = 2;
        return super.getCost() + 8L;
    }

    @Override
    public long getExpirationTime() {
        if (this.expirationTime == -1) {
            return 0L;
        }
        if (this.expirationTime == Integer.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return this.recomputeWithBaseTime(this.expirationTime);
    }

    @Override
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime == Long.MAX_VALUE ? Integer.MAX_VALUE : this.stripBaseTime(expirationTime);
    }

    @Override
    public long getLastStoredTime() {
        if (this.expirationTime == -1) {
            return 0L;
        }
        return this.recomputeWithBaseTime(this.lastStoredTime);
    }

    @Override
    public void setLastStoredTime(long lastStoredTime) {
        this.lastStoredTime = this.stripBaseTime(lastStoredTime);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        AbstractRecordWithStats that = (AbstractRecordWithStats)o;
        if (this.lastStoredTime != that.lastStoredTime) {
            return false;
        }
        return this.expirationTime == that.expirationTime;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.lastStoredTime;
        result = 31 * result + this.expirationTime;
        return result;
    }
}

