/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;

public class TinyGroupEvent
extends TinyEvent {
    private static final long serialVersionUID = 2216456040501944204L;
    private String groupName;

    protected TinyGroupEvent(EventType eventType) {
        super(eventType);
    }

    public static TinyEvent added(String groupName) {
        TinyGroupEvent e = new TinyGroupEvent(EventType.GROUP_ADDED);
        e.groupName = groupName;
        return e;
    }

    public static TinyEvent deleted(String groupName) {
        TinyGroupEvent e = new TinyGroupEvent(EventType.GROUP_DELETED);
        e.groupName = groupName;
        return e;
    }

    public static TinyEvent updated() {
        TinyGroupEvent e = new TinyGroupEvent(EventType.GROUP_UPDATED);
        return e;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String toString() {
        return this.eventType + "{groupName='" + this.groupName + "'}";
    }
}

