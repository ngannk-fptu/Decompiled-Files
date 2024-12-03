/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.event;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.core.Member;

public interface ICacheEvent {
    public Member getMember();

    public CacheEventType getEventType();

    public String getName();
}

