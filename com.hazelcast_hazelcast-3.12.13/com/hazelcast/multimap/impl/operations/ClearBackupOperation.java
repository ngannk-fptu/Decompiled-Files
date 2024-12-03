/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.spi.BackupOperation;

public class ClearBackupOperation
extends AbstractMultiMapOperation
implements BackupOperation {
    public ClearBackupOperation() {
    }

    public ClearBackupOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        container.clear();
        this.response = true;
    }

    @Override
    public int getId() {
        return 2;
    }
}

