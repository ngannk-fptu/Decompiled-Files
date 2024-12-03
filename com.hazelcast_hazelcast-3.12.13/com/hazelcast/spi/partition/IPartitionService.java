/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.partition;

import com.hazelcast.core.MigrationListener;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.CoreService;
import com.hazelcast.spi.partition.IPartition;
import java.util.List;
import java.util.Map;

public interface IPartitionService
extends CoreService {
    public static final String SERVICE_NAME = "hz:core:partitionService";

    public Address getPartitionOwner(int var1);

    public Address getPartitionOwnerOrWait(int var1);

    public IPartition getPartition(int var1);

    public IPartition getPartition(int var1, boolean var2);

    public int getPartitionId(Data var1);

    public int getPartitionId(Object var1);

    public int getPartitionCount();

    public List<Integer> getMemberPartitions(Address var1);

    public Map<Address, List<Integer>> getMemberPartitionsMap();

    public String addMigrationListener(MigrationListener var1);

    public boolean removeMigrationListener(String var1);

    public String addPartitionLostListener(PartitionLostListener var1);

    public String addLocalPartitionLostListener(PartitionLostListener var1);

    public boolean removePartitionLostListener(String var1);

    public long getMigrationQueueSize();

    public boolean isMemberStateSafe();

    public int getMaxAllowedBackupCount();

    public int getPartitionStateVersion();

    public boolean hasOnGoingMigration();

    public boolean hasOnGoingMigrationLocal();

    public boolean isPartitionOwner(int var1);

    public IPartition[] getPartitions();
}

