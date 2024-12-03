/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.AbstractReplicatedRecordStore;
import com.hazelcast.spi.NodeEngine;

public class DataReplicatedRecordStore
extends AbstractReplicatedRecordStore<Data, Data> {
    private final NodeEngine nodeEngine;

    public DataReplicatedRecordStore(String name, ReplicatedMapService replicatedMapService, int partitionId) {
        super(name, replicatedMapService, partitionId);
        this.nodeEngine = replicatedMapService.getNodeEngine();
    }

    @Override
    public Object unmarshall(Object object) {
        return object == null ? null : this.nodeEngine.toObject(object);
    }

    @Override
    public Object marshall(Object object) {
        return this.nodeEngine.toData(object);
    }
}

