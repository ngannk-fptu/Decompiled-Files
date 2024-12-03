/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class CountingMigrationAwareService
implements FragmentedMigrationAwareService {
    static final int PRIMARY_REPLICA_INDEX = 0;
    static final int IN_FLIGHT_MIGRATION_STAMP = -1;
    private final FragmentedMigrationAwareService migrationAwareService;
    private final AtomicInteger ownerMigrationsStarted;
    private final AtomicInteger ownerMigrationsCompleted;

    public CountingMigrationAwareService(FragmentedMigrationAwareService migrationAwareService) {
        this.migrationAwareService = migrationAwareService;
        this.ownerMigrationsStarted = new AtomicInteger();
        this.ownerMigrationsCompleted = new AtomicInteger();
    }

    @Override
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
        return this.migrationAwareService.getAllServiceNamespaces(event);
    }

    @Override
    public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
        return this.migrationAwareService.isKnownServiceNamespace(namespace);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        return this.migrationAwareService.prepareReplicationOperation(event);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
        return this.migrationAwareService.prepareReplicationOperation(event, namespaces);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
        if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
            this.ownerMigrationsStarted.incrementAndGet();
        }
        this.migrationAwareService.beforeMigration(event);
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        try {
            this.migrationAwareService.commitMigration(event);
        }
        finally {
            if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
                int completed = this.ownerMigrationsCompleted.incrementAndGet();
                assert (completed <= this.ownerMigrationsStarted.get());
            }
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        try {
            this.migrationAwareService.rollbackMigration(event);
        }
        finally {
            if (CountingMigrationAwareService.isPrimaryReplicaMigrationEvent(event)) {
                int completed = this.ownerMigrationsCompleted.incrementAndGet();
                assert (completed <= this.ownerMigrationsStarted.get());
            }
        }
    }

    static boolean isPrimaryReplicaMigrationEvent(PartitionMigrationEvent event) {
        return event.getCurrentReplicaIndex() == 0 || event.getNewReplicaIndex() == 0;
    }

    public int getMigrationStamp() {
        int started;
        int completed = this.ownerMigrationsCompleted.get();
        return completed == (started = this.ownerMigrationsStarted.get()) ? completed : -1;
    }

    public boolean validateMigrationStamp(int stamp) {
        int completed = this.ownerMigrationsCompleted.get();
        int started = this.ownerMigrationsStarted.get();
        return stamp == completed && stamp == started;
    }
}

