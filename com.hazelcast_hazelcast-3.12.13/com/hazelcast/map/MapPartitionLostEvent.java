/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import com.hazelcast.core.AbstractIMapEvent;
import com.hazelcast.core.Member;

public class MapPartitionLostEvent
extends AbstractIMapEvent {
    private static final long serialVersionUID = -7445734640964238109L;
    private final int partitionId;

    public MapPartitionLostEvent(Object source, Member member, int eventType, int partitionId) {
        super(source, member, eventType);
        this.partitionId = partitionId;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + super.toString() + ", partitionId=" + this.partitionId + '}';
    }
}

