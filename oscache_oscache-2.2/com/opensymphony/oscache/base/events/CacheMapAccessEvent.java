/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.events.CacheEvent;
import com.opensymphony.oscache.base.events.CacheMapAccessEventType;

public final class CacheMapAccessEvent
extends CacheEvent {
    private CacheEntry entry = null;
    private CacheMapAccessEventType eventType = null;

    public CacheMapAccessEvent(CacheMapAccessEventType eventType, CacheEntry entry) {
        this(eventType, entry, null);
    }

    public CacheMapAccessEvent(CacheMapAccessEventType eventType, CacheEntry entry, String origin) {
        super(origin);
        this.eventType = eventType;
        this.entry = entry;
    }

    public CacheEntry getCacheEntry() {
        return this.entry;
    }

    public String getCacheEntryKey() {
        return this.entry.getKey();
    }

    public CacheMapAccessEventType getEventType() {
        return this.eventType;
    }
}

