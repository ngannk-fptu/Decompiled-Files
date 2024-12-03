/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CachedReferenceEvent
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CachedReferenceEvent;

public class DefaultCachedReferenceEvent<V>
implements CachedReferenceEvent<V> {
    private final V value;

    public DefaultCachedReferenceEvent(V value) {
        this.value = value;
    }

    public V getValue() {
        return this.value;
    }
}

