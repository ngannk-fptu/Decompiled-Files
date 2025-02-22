/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MultiMapMergeContainer
implements IdentifiedDataSerializable {
    private Data key;
    private Collection<MultiMapRecord> records;
    private long creationTime;
    private long lastAccessTime;
    private long lastUpdateTime;
    private long hits;

    public MultiMapMergeContainer() {
    }

    public MultiMapMergeContainer(Data key, Collection<MultiMapRecord> records, long creationTime, long lastAccessTime, long lastUpdateTime, long hits) {
        this.key = key;
        this.records = records;
        this.creationTime = creationTime;
        this.lastAccessTime = lastAccessTime;
        this.lastUpdateTime = lastUpdateTime;
        this.hits = hits;
    }

    public Data getKey() {
        return this.key;
    }

    public Collection<MultiMapRecord> getRecords() {
        return this.records;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public long getHits() {
        return this.hits;
    }

    @Override
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 48;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeData(this.key);
        out.writeInt(this.records.size());
        for (MultiMapRecord record : this.records) {
            out.writeObject(record);
        }
        out.writeLong(this.creationTime);
        out.writeLong(this.lastAccessTime);
        out.writeLong(this.lastUpdateTime);
        out.writeLong(this.hits);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = in.readData();
        int size = in.readInt();
        this.records = new ArrayList<MultiMapRecord>(size);
        for (int i = 0; i < size; ++i) {
            MultiMapRecord record = (MultiMapRecord)in.readObject();
            this.records.add(record);
        }
        this.creationTime = in.readLong();
        this.lastAccessTime = in.readLong();
        this.lastUpdateTime = in.readLong();
        this.hits = in.readLong();
    }
}

