/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.event;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.event.ICacheEvent;
import com.hazelcast.core.Member;
import java.util.EventObject;

public abstract class AbstractICacheEvent
extends EventObject
implements ICacheEvent {
    protected final String name;
    private final CacheEventType cacheEventType;
    private final Member member;

    public AbstractICacheEvent(Object source, Member member, int eventType) {
        super(source);
        this.name = (String)source;
        this.member = member;
        this.cacheEventType = CacheEventType.getByType(eventType);
    }

    @Override
    public Object getSource() {
        return this.name;
    }

    @Override
    public Member getMember() {
        return this.member;
    }

    @Override
    public CacheEventType getEventType() {
        return this.cacheEventType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("entryEventType=%s, member=%s, name='%s'", new Object[]{this.cacheEventType, this.member, this.name});
    }
}

