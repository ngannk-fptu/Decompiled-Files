/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.ValueCollectionFactory;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import java.io.IOException;
import java.util.Collection;

public class MultiMapResponse
implements IdentifiedDataSerializable {
    private long nextRecordId = -1L;
    private MultiMapConfig.ValueCollectionType collectionType = MultiMapConfig.DEFAULT_VALUE_COLLECTION_TYPE;
    private Collection collection;

    public MultiMapResponse() {
    }

    public MultiMapResponse(Collection collection, MultiMapConfig.ValueCollectionType collectionType) {
        this.collection = collection;
        this.collectionType = collectionType;
    }

    public long getNextRecordId() {
        return this.nextRecordId;
    }

    public MultiMapResponse setNextRecordId(long recordId) {
        this.nextRecordId = recordId;
        return this;
    }

    public Collection getCollection() {
        return this.collection == null ? ValueCollectionFactory.emptyCollection(this.collectionType) : this.collection;
    }

    public Collection getObjectCollection(NodeEngine nodeEngine) {
        if (this.collection == null) {
            return ValueCollectionFactory.emptyCollection(this.collectionType);
        }
        Collection newCollection = ValueCollectionFactory.createCollection(this.collectionType, this.collection.size());
        for (Object obj : this.collection) {
            MultiMapRecord record = (MultiMapRecord)nodeEngine.toObject(obj);
            newCollection.add(nodeEngine.toObject(record.getObject()));
        }
        return newCollection;
    }

    public Collection<MultiMapRecord> getRecordCollection(NodeEngine nodeEngine) {
        if (this.collection == null) {
            return ValueCollectionFactory.emptyCollection(this.collectionType);
        }
        Collection<MultiMapRecord> newCollection = ValueCollectionFactory.createCollection(this.collectionType, this.collection.size());
        for (Object obj : this.collection) {
            MultiMapRecord record = (MultiMapRecord)nodeEngine.toObject(obj);
            newCollection.add(record);
        }
        return newCollection;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.collectionType.name());
        out.writeLong(this.nextRecordId);
        if (this.collection == null) {
            out.writeInt(-1);
            return;
        }
        out.writeInt(this.collection.size());
        for (Object obj : this.collection) {
            IOUtil.writeObject(out, obj);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        String collectionTypeName = in.readUTF();
        this.collectionType = MultiMapConfig.ValueCollectionType.valueOf(collectionTypeName);
        this.nextRecordId = in.readLong();
        int size = in.readInt();
        if (size == -1) {
            this.collection = ValueCollectionFactory.emptyCollection(this.collectionType);
            return;
        }
        this.collection = ValueCollectionFactory.createCollection(this.collectionType, size);
        for (int i = 0; i < size; ++i) {
            this.collection.add(IOUtil.readObject(in));
        }
    }

    @Override
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 46;
    }
}

