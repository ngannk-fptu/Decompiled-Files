/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;

public class TinySpaceEvent
extends TinyEvent {
    private static final long serialVersionUID = 4488756005455099009L;
    private String key;
    private String name;

    protected TinySpaceEvent(EventType eventType) {
        super(eventType);
    }

    public static TinyEvent added(String key, String name) {
        TinySpaceEvent e = new TinySpaceEvent(EventType.SPACE_ADDED);
        e.key = key;
        e.name = name;
        return e;
    }

    public static TinyEvent update(String key, String name) {
        TinySpaceEvent e = new TinySpaceEvent(EventType.SPACE_UPDATED);
        e.key = key;
        e.name = name;
        return e;
    }

    public static TinyEvent delete(String key) {
        TinySpaceEvent e = new TinySpaceEvent(EventType.SPACE_DELETED);
        e.key = key;
        return e;
    }

    public static TinyEvent archived(String key) {
        TinySpaceEvent e = new TinySpaceEvent(EventType.SPACE_ARCHIVED);
        e.key = key;
        return e;
    }

    public static TinyEvent unarchived(String key) {
        TinySpaceEvent e = new TinySpaceEvent(EventType.SPACE_UNARCHIVED);
        e.key = key;
        return e;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.eventType + "{key='" + this.key + "', name='" + this.name + "'}";
    }
}

