/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.core.Member;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.internal.partition.PartitionServiceProxy;
import com.hazelcast.internal.partition.PartitionTableView;
import com.hazelcast.internal.partition.impl.PartitionReplicaStateChecker;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.GracefulShutdownAwareService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.partition.IPartitionService;
import java.util.List;

public interface InternalPartitionService
extends IPartitionService,
ManagedService,
GracefulShutdownAwareService {
    public static final int MIGRATION_RETRY_COUNT = 12;
    public static final long MIGRATION_RETRY_PAUSE = 10000L;
    public static final String MIGRATION_EVENT_TOPIC = ".migration";
    public static final String PARTITION_LOST_EVENT_TOPIC = ".partitionLost";

    @Override
    public InternalPartition getPartition(int var1);

    @Override
    public InternalPartition getPartition(int var1, boolean var2);

    public int getMemberGroupsSize();

    public void pauseMigration();

    public void resumeMigration();

    public void memberAdded(Member var1);

    public void memberRemoved(Member var1);

    public InternalPartition[] getInternalPartitions();

    public PartitionRuntimeState firstArrangement();

    public PartitionRuntimeState createPartitionState();

    public PartitionReplicaVersionManager getPartitionReplicaVersionManager();

    public PartitionTableView createPartitionTableView();

    public List<Integer> getMemberPartitionsIfAssigned(Address var1);

    public PartitionServiceProxy getPartitionServiceProxy();

    public PartitionReplicaStateChecker getPartitionReplicaStateChecker();
}

