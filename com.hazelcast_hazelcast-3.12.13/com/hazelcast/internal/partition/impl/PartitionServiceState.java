/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

public enum PartitionServiceState {
    SAFE,
    MIGRATION_LOCAL,
    MIGRATION_ON_MASTER,
    REPLICA_NOT_SYNC,
    REPLICA_NOT_OWNED,
    FETCHING_PARTITION_TABLE;

}

