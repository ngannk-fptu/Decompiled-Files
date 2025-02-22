/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.wan;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.wan.ReplicationEventObject;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;
import com.hazelcast.wan.impl.WanDataSerializerHook;
import java.io.IOException;

public class MapReplicationRemove
implements ReplicationEventObject,
IdentifiedDataSerializable {
    private String mapName;
    private Data key;
    private long removeTime;

    public MapReplicationRemove(String mapName, Data key, long removeTime) {
        this.mapName = mapName;
        this.key = key;
        this.removeTime = removeTime;
    }

    public MapReplicationRemove() {
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public Data getKey() {
        return this.key;
    }

    public void setKey(Data key) {
        this.key = key;
    }

    public long getRemoveTime() {
        return this.removeTime;
    }

    public void setRemoveTime(long removeTime) {
        this.removeTime = removeTime;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        out.writeLong(this.removeTime);
        out.writeData(this.key);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        this.removeTime = in.readLong();
        this.key = in.readData();
    }

    @Override
    public int getFactoryId() {
        return WanDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void incrementEventCount(DistributedServiceWanEventCounters counters) {
        counters.incrementRemove(this.mapName);
    }
}

