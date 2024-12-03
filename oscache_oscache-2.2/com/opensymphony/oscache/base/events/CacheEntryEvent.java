/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.events.CacheEvent;

public final class CacheEntryEvent
extends CacheEvent {
    private Cache map = null;
    private CacheEntry entry = null;

    public CacheEntryEvent(Cache map, CacheEntry entry) {
        this(map, entry, null);
    }

    public CacheEntryEvent(Cache map, CacheEntry entry, String origin) {
        super(origin);
        this.map = map;
        this.entry = entry;
    }

    public CacheEntry getEntry() {
        return this.entry;
    }

    public String getKey() {
        return this.entry.getKey();
    }

    public Cache getMap() {
        return this.map;
    }

    public String toString() {
        return "key=" + this.entry.getKey();
    }
}

