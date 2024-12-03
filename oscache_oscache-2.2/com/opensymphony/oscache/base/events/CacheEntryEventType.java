/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

public final class CacheEntryEventType {
    public static CacheEntryEventType ENTRY_ADDED = new CacheEntryEventType();
    public static CacheEntryEventType ENTRY_UPDATED = new CacheEntryEventType();
    public static CacheEntryEventType ENTRY_FLUSHED = new CacheEntryEventType();
    public static CacheEntryEventType ENTRY_REMOVED = new CacheEntryEventType();
    public static CacheEntryEventType GROUP_FLUSHED = new CacheEntryEventType();
    public static CacheEntryEventType PATTERN_FLUSHED = new CacheEntryEventType();

    private CacheEntryEventType() {
    }
}

