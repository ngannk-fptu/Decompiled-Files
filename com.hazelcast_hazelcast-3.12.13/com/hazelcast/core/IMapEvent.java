/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.Member;

public interface IMapEvent {
    public Member getMember();

    public EntryEventType getEventType();

    public String getName();
}

