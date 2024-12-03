/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

public final class CacheMapAccessEventType {
    public static CacheMapAccessEventType HIT = new CacheMapAccessEventType();
    public static CacheMapAccessEventType MISS = new CacheMapAccessEventType();
    public static CacheMapAccessEventType STALE_HIT = new CacheMapAccessEventType();

    private CacheMapAccessEventType() {
    }
}

