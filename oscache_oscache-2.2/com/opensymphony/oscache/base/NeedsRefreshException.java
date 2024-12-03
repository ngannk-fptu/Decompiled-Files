/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base;

public final class NeedsRefreshException
extends Exception {
    private Object cacheContent = null;

    public NeedsRefreshException(Object cacheContent) {
        this.cacheContent = cacheContent;
    }

    public Object getCacheContent() {
        return this.cacheContent;
    }
}

