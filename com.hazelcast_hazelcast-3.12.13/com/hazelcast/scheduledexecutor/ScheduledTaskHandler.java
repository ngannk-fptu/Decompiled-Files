/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;

public abstract class ScheduledTaskHandler
implements IdentifiedDataSerializable {
    public abstract Address getAddress();

    public abstract int getPartitionId();

    public abstract String getSchedulerName();

    public abstract String getTaskName();

    public abstract boolean isAssignedToPartition();

    public abstract boolean isAssignedToMember();

    public abstract String toUrn();

    public static ScheduledTaskHandler of(String urn) {
        return ScheduledTaskHandlerImpl.of(urn);
    }
}

