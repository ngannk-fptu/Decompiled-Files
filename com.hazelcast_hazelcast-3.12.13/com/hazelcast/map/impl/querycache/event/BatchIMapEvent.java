/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.map.impl.querycache.event.BatchEventData;

public class BatchIMapEvent
implements IMapEvent {
    private final BatchEventData batchEventData;

    public BatchIMapEvent(BatchEventData batchEventData) {
        this.batchEventData = batchEventData;
    }

    public BatchEventData getBatchEventData() {
        return this.batchEventData;
    }

    @Override
    public Member getMember() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntryEventType getEventType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }
}

