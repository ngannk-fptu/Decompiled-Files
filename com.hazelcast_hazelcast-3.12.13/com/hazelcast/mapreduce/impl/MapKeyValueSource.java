/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.PartitionIdAware;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceSimpleEntry;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@BinaryInterface
public class MapKeyValueSource<K, V>
extends KeyValueSource<K, V>
implements IdentifiedDataSerializable,
PartitionIdAware {
    private final MapReduceSimpleEntry<K, V> cachedEntry = new MapReduceSimpleEntry();
    private String mapName;
    private transient int partitionId;
    private transient SerializationService ss;
    private transient Iterator<Record> iterator;
    private transient Record currentRecord;

    MapKeyValueSource() {
    }

    public MapKeyValueSource(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return this.mapName;
    }

    @Override
    public boolean open(NodeEngine nodeEngine) {
        NodeEngineImpl nei = (NodeEngineImpl)nodeEngine;
        InternalPartitionService ps = nei.getPartitionService();
        MapService mapService = (MapService)nei.getService("hz:impl:mapService");
        this.ss = nei.getSerializationService();
        Address partitionOwner = ps.getPartitionOwner(this.partitionId);
        if (partitionOwner == null) {
            return false;
        }
        RecordStore recordStore = mapService.getMapServiceContext().getRecordStore(this.partitionId, this.mapName);
        this.iterator = recordStore.iterator();
        return true;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = this.iterator.hasNext();
        this.currentRecord = hasNext ? this.iterator.next() : null;
        return hasNext;
    }

    @Override
    public K key() {
        if (this.currentRecord == null) {
            throw new IllegalStateException("no more elements");
        }
        Data keyData = this.currentRecord.getKey();
        Object key = this.ss.toObject(keyData);
        this.cachedEntry.setKeyData(keyData);
        this.cachedEntry.setKey(key);
        return (K)key;
    }

    @Override
    public Map.Entry<K, V> element() {
        if (this.currentRecord == null) {
            throw new IllegalStateException("no more elements");
        }
        if (!this.currentRecord.getKey().equals(this.cachedEntry.getKeyData())) {
            this.cachedEntry.setKey(this.ss.toObject(this.currentRecord.getKey()));
        }
        this.cachedEntry.setValue(this.ss.toObject(this.currentRecord.getValue()));
        return this.cachedEntry;
    }

    @Override
    public boolean reset() {
        this.iterator = null;
        this.currentRecord = null;
        return true;
    }

    @Override
    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }
}

