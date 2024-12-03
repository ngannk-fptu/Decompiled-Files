/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache.updates;

import com.atlassian.failurecache.MutableCache;

public interface MutateCacheAction<K, V> {
    public void apply(MutableCache<K, V> var1);
}

