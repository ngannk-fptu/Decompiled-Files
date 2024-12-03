/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.AbstractRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.JVMUtil;

class DataRecord
extends AbstractRecord<Data> {
    protected volatile Data value;

    DataRecord(Data value) {
        this.value = value;
    }

    DataRecord() {
    }

    @Override
    public long getCost() {
        return super.getCost() + (long)JVMUtil.REFERENCE_COST_IN_BYTES + (long)(this.value == null ? 0 : this.value.getHeapCost());
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
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DataRecord that = (DataRecord)o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}

