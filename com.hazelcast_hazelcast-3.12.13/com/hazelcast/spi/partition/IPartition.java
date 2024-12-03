/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.partition;

import com.hazelcast.nio.Address;

public interface IPartition {
    public static final int MAX_BACKUP_COUNT = 6;

    public boolean isLocal();

    public int getPartitionId();

    public Address getOwnerOrNull();

    public boolean isMigrating();

    public Address getReplicaAddress(int var1);

    public boolean isOwnerOrBackup(Address var1);
}

