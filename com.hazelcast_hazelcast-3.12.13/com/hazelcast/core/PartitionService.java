/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Member;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.core.Partition;
import com.hazelcast.partition.PartitionLostListener;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface PartitionService {
    public Set<Partition> getPartitions();

    public Partition getPartition(Object var1);

    @Deprecated
    public String randomPartitionKey();

    public String addMigrationListener(MigrationListener var1);

    public boolean removeMigrationListener(String var1);

    public String addPartitionLostListener(PartitionLostListener var1);

    public boolean removePartitionLostListener(String var1);

    public boolean isClusterSafe();

    public boolean isMemberSafe(Member var1);

    public boolean isLocalMemberSafe();

    public boolean forceLocalMemberToBeSafe(long var1, TimeUnit var3);
}

