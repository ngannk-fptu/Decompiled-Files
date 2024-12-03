/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.events.CacheEntryEvent;
import com.opensymphony.oscache.base.events.CacheEventListener;
import com.opensymphony.oscache.base.events.CacheGroupEvent;
import com.opensymphony.oscache.base.events.CachePatternEvent;
import com.opensymphony.oscache.base.events.CachewideEvent;

public interface CacheEntryEventListener
extends CacheEventListener {
    public void cacheEntryAdded(CacheEntryEvent var1);

    public void cacheEntryFlushed(CacheEntryEvent var1);

    public void cacheEntryRemoved(CacheEntryEvent var1);

    public void cacheEntryUpdated(CacheEntryEvent var1);

    public void cacheGroupFlushed(CacheGroupEvent var1);

    public void cachePatternFlushed(CachePatternEvent var1);

    public void cacheFlushed(CachewideEvent var1);
}

