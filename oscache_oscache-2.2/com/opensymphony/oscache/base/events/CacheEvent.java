/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

public abstract class CacheEvent {
    protected String origin = null;

    public CacheEvent() {
    }

    public CacheEvent(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return this.origin;
    }
}

