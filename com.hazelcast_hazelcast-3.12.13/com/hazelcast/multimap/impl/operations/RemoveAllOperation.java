/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.operations.AbstractBackupAwareMultiMapOperation;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.multimap.impl.operations.RemoveAllBackupOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.util.Collection;

public class RemoveAllOperation
extends AbstractBackupAwareMultiMapOperation
implements MutatingOperation {
    private Collection<MultiMapRecord> coll;

    public RemoveAllOperation() {
    }

    public RemoveAllOperation(String name, Data dataKey, long threadId) {
        super(name, dataKey, threadId);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        this.coll = container.remove(this.dataKey, this.executedLocally());
        this.response = new MultiMapResponse(this.coll, this.getValueCollectionType(container));
    }

    @Override
    public void afterRun() throws Exception {
        if (this.coll != null) {
            this.getOrCreateContainer().update();
            for (MultiMapRecord record : this.coll) {
                this.publishEvent(EntryEventType.REMOVED, this.dataKey, null, record.getObject());
            }
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.coll != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new RemoveAllBackupOperation(this.name, this.dataKey);
    }

    @Override
    public void onWaitExpire() {
        MultiMapContainer container = this.getOrCreateContainer();
        MultiMapConfig.ValueCollectionType valueCollectionType = this.getValueCollectionType(container);
        this.sendResponse(new MultiMapResponse(null, valueCollectionType));
    }

    @Override
    public int getId() {
        return 18;
    }
}

