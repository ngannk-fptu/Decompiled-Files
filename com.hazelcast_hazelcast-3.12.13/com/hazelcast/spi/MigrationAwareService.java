/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;

public interface MigrationAwareService {
    public Operation prepareReplicationOperation(PartitionReplicationEvent var1);

    public void beforeMigration(PartitionMigrationEvent var1);

    public void commitMigration(PartitionMigrationEvent var1);

    public void rollbackMigration(PartitionMigrationEvent var1);
}

