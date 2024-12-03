/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapKeys;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class KeySetOperation
extends AbstractNamedSerializableOperation
implements ReadonlyOperation {
    private String name;
    private transient Object response;

    public KeySetOperation() {
    }

    public KeySetOperation(String name) {
        this.name = name;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        Collection<ReplicatedRecordStore> stores = service.getAllReplicatedRecordStores(this.name);
        ArrayList keys = new ArrayList();
        for (ReplicatedRecordStore store : stores) {
            keys.addAll(store.keySet(false));
        }
        ArrayList<Data> dataKeys = new ArrayList<Data>(keys.size());
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        for (Object key : keys) {
            dataKeys.add((Data)serializationService.toData(key));
        }
        this.response = new ReplicatedMapKeys(dataKeys);
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
        return 18;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

