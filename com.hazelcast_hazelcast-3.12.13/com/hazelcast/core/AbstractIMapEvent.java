/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.Member;
import java.util.EventObject;

public abstract class AbstractIMapEvent
extends EventObject
implements IMapEvent {
    protected final String name;
    private final EntryEventType entryEventType;
    private final Member member;

    public AbstractIMapEvent(Object source, Member member, int eventType) {
        super(source);
        this.name = (String)source;
        this.member = member;
        this.entryEventType = EntryEventType.getByType(eventType);
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
    public EntryEventType getEventType() {
        return this.entryEventType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("entryEventType=%s, member=%s, name='%s'", new Object[]{this.entryEventType, this.member, this.name});
    }
}

