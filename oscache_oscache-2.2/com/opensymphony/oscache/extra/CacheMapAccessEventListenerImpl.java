/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.extra;

import com.opensymphony.oscache.base.events.CacheMapAccessEvent;
import com.opensymphony.oscache.base.events.CacheMapAccessEventListener;
import com.opensymphony.oscache.base.events.CacheMapAccessEventType;

public class CacheMapAccessEventListenerImpl
implements CacheMapAccessEventListener {
    private int hitCount = 0;
    private int missCount = 0;
    private int staleHitCount = 0;

    public int getHitCount() {
        return this.hitCount;
    }

    public int getMissCount() {
        return this.missCount;
    }

    public int getStaleHitCount() {
        return this.staleHitCount;
    }

    public void accessed(CacheMapAccessEvent event) {
        CacheMapAccessEventType type = event.getEventType();
        if (type == CacheMapAccessEventType.HIT) {
            ++this.hitCount;
        } else if (type == CacheMapAccessEventType.STALE_HIT) {
            ++this.staleHitCount;
        } else if (type == CacheMapAccessEventType.MISS) {
            ++this.missCount;
        } else {
            throw new IllegalArgumentException("Unknown Cache Map Access event received");
        }
    }

    public void reset() {
        this.hitCount = 0;
        this.staleHitCount = 0;
        this.missCount = 0;
    }

    public String toString() {
        return "Hit count = " + this.hitCount + ", stale hit count = " + this.staleHitCount + " and miss count = " + this.missCount;
    }
}

