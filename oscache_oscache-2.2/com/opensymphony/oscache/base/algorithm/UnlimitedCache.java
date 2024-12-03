/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.algorithm;

import com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache;

public final class UnlimitedCache
extends AbstractConcurrentReadCache {
    public UnlimitedCache() {
        this.maxEntries = 0x7FFFFFFE;
    }

    public void setMaxEntries(int maxEntries) {
    }

    protected void itemRetrieved(Object key) {
    }

    protected void itemPut(Object key) {
    }

    protected Object removeItem() {
        return null;
    }

    protected void itemRemoved(Object key) {
    }
}

