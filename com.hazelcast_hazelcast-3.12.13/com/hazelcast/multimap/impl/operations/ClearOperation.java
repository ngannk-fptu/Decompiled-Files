/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.multimap.impl.operations.ClearBackupOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;

public class ClearOperation
extends AbstractMultiMapOperation
implements BackupAwareOperation,
PartitionAwareOperation,
MutatingOperation {
    private transient MultiMapContainer container;
    private transient boolean shouldBackup;

    public ClearOperation() {
    }

    public ClearOperation(String name) {
        super(name);
    }

    @Override
    public void beforeRun() throws Exception {
        this.container = this.getOrCreateContainer();
        this.shouldBackup = this.container.size() > 0;
    }

    @Override
    public void run() throws Exception {
        this.container = this.getOrCreateContainer();
        this.response = this.container.clear();
    }

    @Override
    public void afterRun() throws Exception {
        ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementOtherOperations();
        if (this.shouldBackup) {
            this.container.update();
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public Operation getBackupOperation() {
        return new ClearBackupOperation(this.name);
    }

    @Override
    public int getId() {
        return 3;
    }
}

