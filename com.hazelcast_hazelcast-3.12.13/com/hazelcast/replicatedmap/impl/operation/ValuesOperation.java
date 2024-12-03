/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapValueCollection;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ValuesOperation
extends AbstractNamedSerializableOperation
implements ReadonlyOperation {
    private String name;
    private transient Object response;

    public ValuesOperation() {
    }

    public ValuesOperation(String name) {
        this.name = name;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        Collection<ReplicatedRecordStore> stores = service.getAllReplicatedRecordStores(this.name);
        ArrayList values = new ArrayList();
        for (ReplicatedRecordStore store : stores) {
            values.addAll(store.values(false));
        }
        ArrayList<Data> dataValues = new ArrayList<Data>(values.size());
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        for (ReplicatedRecord value : values) {
            dataValues.add((Data)serializationService.toData(value.getValue()));
        }
        this.response = new ReplicatedMapValueCollection(dataValues);
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
        return 22;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

