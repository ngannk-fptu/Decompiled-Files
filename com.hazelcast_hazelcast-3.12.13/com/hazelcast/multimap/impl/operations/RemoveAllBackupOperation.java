/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;

public class RemoveAllBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation {
    public RemoveAllBackupOperation() {
    }

    public RemoveAllBackupOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        container.delete(this.dataKey);
        this.response = true;
    }

    @Override
    public int getId() {
        return 17;
    }
}

