/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.AbstractIMapEvent;
import com.hazelcast.core.Member;

public class MapEvent
extends AbstractIMapEvent {
    private static final long serialVersionUID = -4948640313865667023L;
    private final int numberOfEntriesAffected;

    public MapEvent(Object source, Member member, int eventType, int numberOfEntriesAffected) {
        super(source, member, eventType);
        this.numberOfEntriesAffected = numberOfEntriesAffected;
    }

    public int getNumberOfEntriesAffected() {
        return this.numberOfEntriesAffected;
    }

    @Override
    public String toString() {
        return "MapEvent{" + super.toString() + ", numberOfEntriesAffected=" + this.numberOfEntriesAffected + '}';
    }
}

