/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.events.CacheEvent;

public final class CachePatternEvent
extends CacheEvent {
    private Cache map = null;
    private String pattern = null;

    public CachePatternEvent(Cache map, String pattern) {
        this(map, pattern, null);
    }

    public CachePatternEvent(Cache map, String pattern, String origin) {
        super(origin);
        this.map = map;
        this.pattern = pattern;
    }

    public Cache getMap() {
        return this.map;
    }

    public String getPattern() {
        return this.pattern;
    }

    public String toString() {
        return "pattern=" + this.pattern;
    }
}

