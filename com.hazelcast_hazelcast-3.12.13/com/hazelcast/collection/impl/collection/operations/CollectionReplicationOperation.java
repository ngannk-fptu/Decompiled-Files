/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionDataSerializerHook;
import com.hazelcast.collection.impl.collection.CollectionService;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.Map;

public abstract class CollectionReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    protected Map<String, CollectionContainer> migrationData;

    public CollectionReplicationOperation() {
    }

    public CollectionReplicationOperation(Map<String, CollectionContainer> migrationData, int partitionId, int replicaIndex) {
        this.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        this.migrationData = migrationData;
    }

    @Override
    public void run() throws Exception {
        CollectionService service = (CollectionService)this.getService();
        for (Map.Entry<String, CollectionContainer> entry : this.migrationData.entrySet()) {
            String name = entry.getKey();
            CollectionContainer container = entry.getValue();
            container.init(this.getNodeEngine());
            service.addContainer(name, container);
        }
    }

    @Override
    public int getFactoryId() {
        return CollectionDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (Map.Entry<String, CollectionContainer> entry : this.migrationData.entrySet()) {
            out.writeUTF(entry.getKey());
            CollectionContainer container = entry.getValue();
            container.writeData(out);
        }
    }
}

