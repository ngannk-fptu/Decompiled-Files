/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

public class RecordReplicationInfo
extends RecordInfo {
    private Data key;
    private Data value;

    public RecordReplicationInfo(Data key, Data value, RecordInfo recordInfo) {
        super(recordInfo);
        this.key = key;
        this.value = value;
    }

    public RecordReplicationInfo() {
    }

    public Data getKey() {
        return this.key;
    }

    public Data getValue() {
        return this.value;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.key);
        out.writeData(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.key = in.readData();
        this.value = in.readData();
    }

    @Override
    public String toString() {
        return "RecordReplicationInfo{key=" + this.key + ", value=" + this.value + "} " + super.toString();
    }

    @Override
    public int getId() {
        return 104;
    }
}

