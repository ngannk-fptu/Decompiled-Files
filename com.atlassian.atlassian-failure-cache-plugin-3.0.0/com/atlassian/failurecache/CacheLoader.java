/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.util.concurrent.ListenableFuture
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.ExpiringValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;

public interface CacheLoader<K, V> {
    public ImmutableSet<K> getKeys();

    public ListenableFuture<ExpiringValue<V>> loadValue(K var1);
}

