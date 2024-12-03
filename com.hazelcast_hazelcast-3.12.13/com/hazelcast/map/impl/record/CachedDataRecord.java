/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.DataRecord;
import com.hazelcast.nio.serialization.Data;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class CachedDataRecord
extends DataRecord {
    private static final AtomicReferenceFieldUpdater<CachedDataRecord, Object> CACHED_VALUE = AtomicReferenceFieldUpdater.newUpdater(CachedDataRecord.class, Object.class, "cachedValue");
    private volatile transient Object cachedValue;

    CachedDataRecord() {
    }

    CachedDataRecord(Data value) {
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
        CachedDataRecord that = (CachedDataRecord)o;
        return this.cachedValue != null ? this.cachedValue.equals(that.cachedValue) : that.cachedValue == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.cachedValue != null ? this.cachedValue.hashCode() : 0);
        return result;
    }
}

