/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.events.CacheEvent;

public final class CacheGroupEvent
extends CacheEvent {
    private Cache map = null;
    private String group = null;

    public CacheGroupEvent(Cache map, String group) {
        this(map, group, null);
    }

    public CacheGroupEvent(Cache map, String group, String origin) {
        super(origin);
        this.map = map;
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }

    public Cache getMap() {
        return this.map;
    }

    public String toString() {
        return "groupName=" + this.group;
    }
}

