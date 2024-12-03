/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.internal.management.JsonSerializable;
import java.util.List;

public interface MemberPartitionState
extends JsonSerializable {
    public List<Integer> getPartitions();

    public boolean isMemberStateSafe();

    public long getMigrationQueueSize();
}

