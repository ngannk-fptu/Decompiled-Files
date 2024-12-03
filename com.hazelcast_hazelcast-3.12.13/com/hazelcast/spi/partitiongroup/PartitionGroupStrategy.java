/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.partitiongroup;

import com.hazelcast.partition.membergroup.MemberGroup;

public interface PartitionGroupStrategy {
    public Iterable<MemberGroup> getMemberGroups();
}

