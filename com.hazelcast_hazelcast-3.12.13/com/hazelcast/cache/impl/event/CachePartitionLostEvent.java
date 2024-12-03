/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.event;

import com.hazelcast.cache.impl.event.AbstractICacheEvent;
import com.hazelcast.core.Member;

public class CachePartitionLostEvent
extends AbstractICacheEvent {
    private static final long serialVersionUID = -7445714640964238109L;
    private final int partitionId;

    public CachePartitionLostEvent(Object source, Member member, int eventType, int partitionId) {
        super(source, member, eventType);
        this.partitionId = partitionId;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' + super.toString() + ", partitionId=" + this.partitionId + '}';
    }
}

