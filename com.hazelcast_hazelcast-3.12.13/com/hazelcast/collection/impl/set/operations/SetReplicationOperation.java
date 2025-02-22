/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.set.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionReplicationOperation;
import com.hazelcast.collection.impl.set.SetContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class SetReplicationOperation
extends CollectionReplicationOperation {
    public SetReplicationOperation() {
    }

    public SetReplicationOperation(Map<String, CollectionContainer> migrationData, int partitionId, int replicaIndex) {
        super(migrationData, partitionId, replicaIndex);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.migrationData = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            String name = in.readUTF();
            SetContainer container = new SetContainer();
            container.readData(in);
            this.migrationData.put(name, container);
        }
    }

    @Override
    public int getId() {
        return 37;
    }
}

