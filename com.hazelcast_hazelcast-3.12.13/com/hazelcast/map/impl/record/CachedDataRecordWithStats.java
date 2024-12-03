/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.DataRecordWithStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.JVMUtil;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class CachedDataRecordWithStats
extends DataRecordWithStats {
    private static final AtomicReferenceFieldUpdater<CachedDataRecordWithStats, Object> CACHED_VALUE = AtomicReferenceFieldUpdater.newUpdater(CachedDataRecordWithStats.class, Object.class, "cachedValue");
    private static final int CACHED_VALUE_REF_COST_IN_BYTES = JVMUtil.REFERENCE_COST_IN_BYTES;
    private volatile transient Object cachedValue;

    CachedDataRecordWithStats() {
    }

    CachedDataRecordWithStats(Data value) {
        super(value);
    }

    @Override
    public void setValue(Data o) {
        super.setValue(o);
        this.cachedValue = null;
    }

    @Override
    public Object getCachedValueUnsafe() {
        return this.cachedValue;
    }

    @Override
    public boolean casCachedValue(Object expectedValue, Object newValue) {
        return CACHED_VALUE.compareAndSet(this, expectedValue, newValue);
    }

    @Override
    public long getCost() {
        return super.getCost() + (long)CACHED_VALUE_REF_COST_IN_BYTES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CachedDataRecordWithStats that = (CachedDataRecordWithStats)o;
        return this.cachedValue != null ? this.cachedValue.equals(that.cachedValue) : that.cachedValue == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.cachedValue != null ? this.cachedValue.hashCode() : 0);
        return result;
    }
}

