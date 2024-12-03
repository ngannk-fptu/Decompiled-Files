/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.MigrationInfo;
import java.util.Collection;
import java.util.EventListener;

public abstract class InternalMigrationListener
implements EventListener {
    public void onMigrationStart(MigrationParticipant participant, MigrationInfo migrationInfo) {
    }

    public void onMigrationComplete(MigrationParticipant participant, MigrationInfo migrationInfo, boolean success) {
    }

    public void onMigrationCommit(MigrationParticipant participant, MigrationInfo migrationInfo) {
    }

    public void onMigrationRollback(MigrationParticipant participant, MigrationInfo migrationInfo) {
    }

    public void onPromotionStart(MigrationParticipant participant, Collection<MigrationInfo> migrationInfos) {
    }

    public void onPromotionComplete(MigrationParticipant participant, Collection<MigrationInfo> migrationInfos, boolean success) {
    }

    public static class NopInternalMigrationListener
    extends InternalMigrationListener {
        @Override
        public void onMigrationStart(MigrationParticipant participant, MigrationInfo migrationInfo) {
        }

        @Override
        public void onMigrationComplete(MigrationParticipant participant, MigrationInfo migrationInfo, boolean success) {
        }

        @Override
        public void onMigrationCommit(MigrationParticipant participant, MigrationInfo migrationInfo) {
        }

        @Override
        public void onMigrationRollback(MigrationParticipant participant, MigrationInfo migrationInfo) {
        }
    }

    public static enum MigrationParticipant {
        MASTER,
        SOURCE,
        DESTINATION;

    }
}

