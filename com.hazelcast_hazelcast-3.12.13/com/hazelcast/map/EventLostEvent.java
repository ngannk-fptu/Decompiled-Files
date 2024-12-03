/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.Member;

public class EventLostEvent
implements IMapEvent {
    public static final int EVENT_TYPE = EventLostEvent.getNextEntryEventTypeId();
    private final int partitionId;
    private final String source;
    private final Member member;

    public EventLostEvent(String source, Member member, int partitionId) {
        this.source = source;
        this.member = member;
        this.partitionId = partitionId;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public Member getMember() {
        return this.member;
    }

    @Override
    public EntryEventType getEventType() {
        return null;
    }

    @Override
    public String getName() {
        return this.source;
    }

    private static int getNextEntryEventTypeId() {
        EntryEventType[] values;
        int higherTypeId = Integer.MIN_VALUE;
        int i = 0;
        for (EntryEventType value : values = EntryEventType.values()) {
            int typeId = value.getType();
            if (i == 0) {
                higherTypeId = typeId;
            } else if (typeId > higherTypeId) {
                higherTypeId = typeId;
            }
            ++i;
        }
        int eventFlagPosition = Integer.numberOfTrailingZeros(higherTypeId);
        return 1 << ++eventFlagPosition;
    }

    public String toString() {
        return "EventLostEvent{partitionId=" + this.partitionId + ", source='" + this.source + '\'' + ", member=" + this.member + '}';
    }
}

