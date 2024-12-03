/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.cache;

import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public interface CacheLoader<K, V> {
    public V load(K var1);
}

