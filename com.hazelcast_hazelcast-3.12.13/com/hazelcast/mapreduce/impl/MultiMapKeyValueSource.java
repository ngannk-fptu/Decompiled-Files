/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.PartitionIdAware;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceSimpleEntry;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.MultiMapValue;
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
public class MultiMapKeyValueSource<K, V>
extends KeyValueSource<K, V>
implements IdentifiedDataSerializable,
PartitionIdAware {
    private final MapReduceSimpleEntry<K, V> simpleEntry = new MapReduceSimpleEntry();
    private String multiMapName;
    private transient int partitionId;
    private transient SerializationService ss;
    private transient MultiMapContainer multiMapContainer;
    private transient boolean isBinary;
    private transient K key;
    private transient Iterator<Data> keyIterator;
    private transient Iterator<MultiMapRecord> valueIterator;
    private transient MultiMapRecord multiMapRecord;

    MultiMapKeyValueSource() {
    }

    public MultiMapKeyValueSource(String multiMapName) {
        this.multiMapName = multiMapName;
    }

    public String getMultiMapName() {
        return this.multiMapName;
    }

    @Override
    public boolean open(NodeEngine nodeEngine) {
        NodeEngineImpl nei = (NodeEngineImpl)nodeEngine;
        InternalPartitionService ps = nei.getPartitionService();
        MultiMapService multiMapService = (MultiMapService)nei.getService("hz:impl:multiMapService");
        this.ss = nei.getSerializationService();
        Address partitionOwner = ps.getPartitionOwner(this.partitionId);
        if (partitionOwner == null) {
            return false;
        }
        this.multiMapContainer = multiMapService.getOrCreateCollectionContainer(this.partitionId, this.multiMapName);
        this.isBinary = this.multiMapContainer.getConfig().isBinary();
        this.keyIterator = this.multiMapContainer.keySet().iterator();
        return true;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean hasNext() {
        if (this.valueIterator != null) {
            boolean hasNext = this.valueIterator.hasNext();
            MultiMapRecord multiMapRecord = this.multiMapRecord = hasNext ? this.valueIterator.next() : null;
            if (hasNext) {
                return true;
            }
        }
        if (this.keyIterator != null && this.keyIterator.hasNext()) {
            Data dataKey = this.keyIterator.next();
            this.key = this.ss.toObject(dataKey);
            MultiMapValue wrapper = this.multiMapContainer.getMultiMapValueOrNull(dataKey);
            this.valueIterator = wrapper.getCollection(true).iterator();
            return this.hasNext();
        }
        return false;
    }

    @Override
    public K key() {
        if (this.multiMapRecord == null) {
            throw new IllegalStateException("no more elements");
        }
        return this.key;
    }

    @Override
    public Map.Entry<K, V> element() {
        if (this.multiMapRecord == null) {
            throw new IllegalStateException("no more elements");
        }
        this.simpleEntry.setKey(this.key);
        Object value = this.multiMapRecord.getObject();
        this.simpleEntry.setValue(this.isBinary ? this.ss.toObject((Data)value) : value);
        return this.simpleEntry;
    }

    @Override
    public boolean reset() {
        this.key = null;
        this.keyIterator = null;
        this.valueIterator = null;
        this.multiMapRecord = null;
        return false;
    }

    @Override
    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.multiMapName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.multiMapName = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}

