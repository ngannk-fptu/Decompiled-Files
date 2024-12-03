/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.AbstractRecord;
import com.hazelcast.map.impl.record.Record;

class ObjectRecord
extends AbstractRecord<Object>
implements Record<Object> {
    private volatile Object value;

    ObjectRecord() {
    }

    ObjectRecord(Object value) {
        this.value = value;
    }

    @Override
    public long getCost() {
        return 0L;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object o) {
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
        ObjectRecord that = (ObjectRecord)o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}

