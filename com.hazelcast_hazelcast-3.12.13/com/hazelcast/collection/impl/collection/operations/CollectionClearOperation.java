/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionBackupAwareOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionClearBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.util.Map;

public class CollectionClearOperation
extends CollectionBackupAwareOperation
implements MutatingOperation {
    private Map<Long, Data> itemIdMap;

    public CollectionClearOperation() {
    }

    public CollectionClearOperation(String name) {
        super(name);
    }

    @Override
    public boolean shouldBackup() {
        return this.itemIdMap != null && !this.itemIdMap.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionClearBackupOperation(this.name, this.itemIdMap.keySet());
    }

    @Override
    public void run() throws Exception {
        CollectionContainer container = this.getOrCreateContainer();
        this.itemIdMap = container.clear(true);
    }

    @Override
    public void afterRun() throws Exception {
        for (Data value : this.itemIdMap.values()) {
            this.publishEvent(ItemEventType.REMOVED, value);
        }
    }

    @Override
    public int getId() {
        return 8;
    }
}

