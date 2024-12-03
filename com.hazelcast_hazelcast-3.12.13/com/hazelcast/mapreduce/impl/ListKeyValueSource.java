/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.collection.impl.list.ListService;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceSimpleEntry;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.partition.strategy.StringAndPartitionAwarePartitioningStrategy;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@BinaryInterface
public class ListKeyValueSource<V>
extends KeyValueSource<String, V>
implements IdentifiedDataSerializable {
    private final MapReduceSimpleEntry<String, V> simpleEntry = new MapReduceSimpleEntry();
    private String listName;
    private transient SerializationService ss;
    private transient Iterator<CollectionItem> iterator;
    private transient CollectionItem nextElement;

    public ListKeyValueSource() {
    }

    public ListKeyValueSource(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return this.listName;
    }

    @Override
    public boolean open(NodeEngine nodeEngine) {
        Object data;
        int partitionId;
        NodeEngineImpl nei = (NodeEngineImpl)nodeEngine;
        this.ss = nei.getSerializationService();
        Address thisAddress = nei.getThisAddress();
        InternalPartitionService ps = nei.getPartitionService();
        Address partitionOwner = ps.getPartitionOwner(partitionId = ps.getPartitionId((Data)(data = this.ss.toData(this.listName, StringAndPartitionAwarePartitioningStrategy.INSTANCE))));
        if (partitionOwner == null) {
            return false;
        }
        if (thisAddress.equals(partitionOwner)) {
            ListService listService = (ListService)nei.getService("hz:impl:listService");
            ListContainer listContainer = listService.getOrCreateContainer(this.listName, false);
            ArrayList items = new ArrayList(listContainer.getCollection());
            this.iterator = items.iterator();
        }
        return true;
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = this.iterator == null ? false : this.iterator.hasNext();
        this.nextElement = hasNext ? this.iterator.next() : null;
        return hasNext;
    }

    @Override
    public String key() {
        return this.listName;
    }

    @Override
    public Map.Entry<String, V> element() {
        Data value = this.nextElement.getValue();
        if (value != null) {
            value = this.ss.toObject(value);
        }
        this.simpleEntry.setKey(this.listName);
        this.simpleEntry.setValue(value);
        return this.simpleEntry;
    }

    @Override
    public boolean reset() {
        this.iterator = null;
        this.nextElement = null;
        return true;
    }

    @Override
    public void close() throws IOException {
        this.iterator = null;
        this.nextElement = null;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.listName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.listName = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }
}

