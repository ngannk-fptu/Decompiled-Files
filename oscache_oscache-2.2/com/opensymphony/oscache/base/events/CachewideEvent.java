/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.events.CacheEvent;
import java.util.Date;

public final class CachewideEvent
extends CacheEvent {
    private Cache cache = null;
    private Date date = null;

    public CachewideEvent(Cache cache, Date date, String origin) {
        super(origin);
        this.date = date;
        this.cache = cache;
    }

    public Cache getCache() {
        return this.cache;
    }

    public Date getDate() {
        return this.date;
    }
}

