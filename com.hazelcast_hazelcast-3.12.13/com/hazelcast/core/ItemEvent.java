/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.ItemEventType;
import com.hazelcast.core.Member;
import java.util.EventObject;

public class ItemEvent<E>
extends EventObject {
    protected E item;
    private final ItemEventType eventType;
    private final Member member;

    public ItemEvent(String name, ItemEventType itemEventType, E item, Member member) {
        super(name);
        this.item = item;
        this.eventType = itemEventType;
        this.member = member;
    }

    public ItemEventType getEventType() {
        return this.eventType;
    }

    public E getItem() {
        return this.item;
    }

    public Member getMember() {
        return this.member;
    }

    @Override
    public String toString() {
        return "ItemEvent{event=" + (Object)((Object)this.eventType) + ", item=" + this.getItem() + ", member=" + this.getMember() + "} ";
    }
}

