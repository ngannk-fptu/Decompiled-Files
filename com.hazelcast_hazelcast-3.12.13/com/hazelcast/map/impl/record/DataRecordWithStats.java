/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.AbstractRecordWithStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.JVMUtil;

class DataRecordWithStats
extends AbstractRecordWithStats<Data> {
    protected volatile Data value;

    DataRecordWithStats() {
    }

    DataRecordWithStats(Data value) {
        this.value = value;
    }

    @Override
    public long getCost() {
        return super.getCost() + (long)JVMUtil.REFERENCE_COST_IN_BYTES + (this.value == null ? 0L : (long)this.value.getHeapCost());
    }

    @Override
    public Data getValue() {
        return this.value;
    }

    @Override
    public void setValue(Data o) {
        this.value = o;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        DataRecordWithStats that = (DataRecordWithStats)o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}

