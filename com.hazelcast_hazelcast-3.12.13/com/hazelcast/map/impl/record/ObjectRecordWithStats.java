/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.AbstractRecordWithStats;

class ObjectRecordWithStats
extends AbstractRecordWithStats<Object> {
    private volatile Object value;

    ObjectRecordWithStats() {
    }

    ObjectRecordWithStats(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public long getCost() {
        return 0L;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        ObjectRecordWithStats that = (ObjectRecordWithStats)o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}

