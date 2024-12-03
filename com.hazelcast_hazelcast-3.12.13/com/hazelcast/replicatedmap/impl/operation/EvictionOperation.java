/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.util.scheduler.ScheduledEntry;
import java.util.Collection;

public class EvictionOperation
extends AbstractNamedSerializableOperation
implements MutatingOperation {
    private ReplicatedRecordStore store;
    private Collection<ScheduledEntry<Object, Object>> entries;

    public EvictionOperation() {
    }

    public EvictionOperation(ReplicatedRecordStore store, Collection<ScheduledEntry<Object, Object>> entries) {
        this.store = store;
        this.entries = entries;
    }

    @Override
    public void run() throws Exception {
        for (ScheduledEntry<Object, Object> entry : this.entries) {
            Object key = entry.getKey();
            this.store.evict(key);
        }
    }

    @Override
    public boolean validatesTarget() {
        return false;
    }

    @Override
    public int getId() {
        return 16;
    }

    @Override
    public String getName() {
        return this.store.getName();
    }
}

