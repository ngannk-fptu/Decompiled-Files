/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapEntries;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class EntrySetOperation
extends AbstractNamedSerializableOperation
implements ReadonlyOperation {
    private String name;
    private transient Object response;

    public EntrySetOperation() {
    }

    public EntrySetOperation(String name) {
        this.name = name;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        Collection<ReplicatedRecordStore> stores = service.getAllReplicatedRecordStores(this.name);
        ArrayList entries = new ArrayList();
        for (ReplicatedRecordStore store : stores) {
            entries.addAll(store.entrySet(false));
        }
        ArrayList<Map.Entry<Data, Data>> dataEntries = new ArrayList<Map.Entry<Data, Data>>(entries.size());
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        for (Map.Entry entry : entries) {
            Object key = serializationService.toData(entry.getKey());
            Object value = serializationService.toData(((ReplicatedRecord)entry.getValue()).getValue());
            dataEntries.add(new AbstractMap.SimpleImmutableEntry(key, value));
        }
        this.response = new ReplicatedMapEntries(dataEntries);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    public int getId() {
        return 15;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

