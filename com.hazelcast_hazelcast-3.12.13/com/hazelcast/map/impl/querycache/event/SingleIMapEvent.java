/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;

public class SingleIMapEvent
implements IMapEvent {
    private final QueryCacheEventData eventData;

    public SingleIMapEvent(QueryCacheEventData eventData) {
        this.eventData = eventData;
    }

    public QueryCacheEventData getEventData() {
        return this.eventData;
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

