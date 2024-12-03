/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.cache;

import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public interface Cache<K, V> {
    public V get(K var1);

    public void put(K var1, V var2);
}

