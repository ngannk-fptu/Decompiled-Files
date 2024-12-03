/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.wan;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.wan.WanMapEntryView;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.wan.ReplicationEventObject;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;
import com.hazelcast.wan.impl.WanDataSerializerHook;
import java.io.IOException;

public class MapReplicationUpdate
implements ReplicationEventObject,
IdentifiedDataSerializable {
    private String mapName;
    private Object mergePolicy;
    private WanMapEntryView<Data, Data> entryView;

    public MapReplicationUpdate() {
    }

    public MapReplicationUpdate(String mapName, Object mergePolicy, EntryView<Data, Data> entryView) {
        this.mergePolicy = mergePolicy;
        this.mapName = mapName;
        this.entryView = entryView instanceof WanMapEntryView ? (WanMapEntryView<Object, Object>)entryView : new WanMapEntryView<Data, Data>(entryView);
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Object getMergePolicy() {
        return this.mergePolicy;
    }

    public void setMergePolicy(Object mergePolicy) {
        this.mergePolicy = mergePolicy;
    }

    public WanMapEntryView<Data, Data> getEntryView() {
        return this.entryView;
    }

    public void setEntryView(WanMapEntryView<Data, Data> entryView) {
        this.entryView = entryView;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        out.writeObject(this.mergePolicy);
        out.writeObject(this.entryView);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        this.mergePolicy = in.readObject();
        EntryView entryView = (EntryView)in.readObject();
        this.entryView = entryView instanceof WanMapEntryView ? (WanMapEntryView)entryView : new WanMapEntryView(entryView);
    }

    @Override
    public int getFactoryId() {
        return WanDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void incrementEventCount(DistributedServiceWanEventCounters counters) {
        counters.incrementUpdate(this.mapName);
    }

    @Override
    public Data getKey() {
        return this.entryView.getKey();
    }
}

