/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cluster;

public enum ClusterState {
    ACTIVE(true, true, true),
    NO_MIGRATION(true, false, true),
    FROZEN(false, false, false),
    PASSIVE(false, false, false),
    IN_TRANSITION(false, false, false);

    private final boolean joinAllowed;
    private final boolean migrationAllowed;
    private final boolean partitionPromotionAllowed;

    private ClusterState(boolean joinAllowed, boolean migrationAllowed, boolean partitionPromotionAllowed) {
        this.joinAllowed = joinAllowed;
        this.migrationAllowed = migrationAllowed;
        this.partitionPromotionAllowed = partitionPromotionAllowed;
    }

    public boolean isJoinAllowed() {
        return this.joinAllowed;
    }

    public boolean isMigrationAllowed() {
        return this.migrationAllowed;
    }

    public boolean isPartitionPromotionAllowed() {
        return this.partitionPromotionAllowed;
    }
}

