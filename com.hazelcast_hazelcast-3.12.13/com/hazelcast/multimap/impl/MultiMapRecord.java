/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class MultiMapRecord
implements IdentifiedDataSerializable {
    private long recordId = -1L;
    private Object object;

    public MultiMapRecord() {
    }

    public MultiMapRecord(Object object) {
        this.object = object;
    }

    public MultiMapRecord(long recordId, Object object) {
        this.recordId = recordId;
        this.object = object;
    }

    public long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiMapRecord)) {
            return false;
        }
        MultiMapRecord record = (MultiMapRecord)o;
        return this.object.equals(record.object);
    }

    public int hashCode() {
        return this.object.hashCode();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.recordId);
        IOUtil.writeObject(out, this.object);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.recordId = in.readLong();
        this.object = IOUtil.readObject(in);
    }

    @Override
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 44;
    }
}

