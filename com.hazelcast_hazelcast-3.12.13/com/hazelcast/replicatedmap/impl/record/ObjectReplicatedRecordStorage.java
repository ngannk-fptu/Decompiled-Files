/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.AbstractReplicatedRecordStore;

public class ObjectReplicatedRecordStorage<K, V>
extends AbstractReplicatedRecordStore<K, V> {
    public ObjectReplicatedRecordStorage(String name, ReplicatedMapService replicatedMapService, int partitionId) {
        super(name, replicatedMapService, partitionId);
    }

    @Override
    public Object unmarshall(Object key) {
        return this.nodeEngine.toObject(key);
    }

    @Override
    public Object marshall(Object key) {
        return this.nodeEngine.toObject(key);
    }
}

