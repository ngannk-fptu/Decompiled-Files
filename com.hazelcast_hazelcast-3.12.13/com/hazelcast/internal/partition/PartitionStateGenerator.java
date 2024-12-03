/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.partition.membergroup.MemberGroup;
import java.util.Collection;

public interface PartitionStateGenerator {
    public PartitionReplica[][] arrange(Collection<MemberGroup> var1, InternalPartition[] var2);

    public PartitionReplica[][] arrange(Collection<MemberGroup> var1, InternalPartition[] var2, Collection<Integer> var3);
}

