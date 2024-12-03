/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.partition.PartitionEventListener;

class MigrationListenerAdapter
implements PartitionEventListener<MigrationEvent> {
    private final MigrationListener migrationListener;

    public MigrationListenerAdapter(MigrationListener migrationListener) {
        this.migrationListener = migrationListener;
    }

    @Override
    public void onEvent(MigrationEvent migrationEvent) {
        MigrationEvent.MigrationStatus status = migrationEvent.getStatus();
        switch (status) {
            case STARTED: {
                this.migrationListener.migrationStarted(migrationEvent);
                break;
            }
            case COMPLETED: {
                this.migrationListener.migrationCompleted(migrationEvent);
                break;
            }
            case FAILED: {
                this.migrationListener.migrationFailed(migrationEvent);
                break;
            }
            default: {
                throw new IllegalArgumentException("Not a known MigrationStatus: " + (Object)((Object)status));
            }
        }
    }
}

