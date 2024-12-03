/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.ExpiringValue;

public interface MutableCache<K, V> {
    public boolean remove(K var1, ExpiringValue<V> var2);

    public boolean replace(K var1, ExpiringValue<V> var2, ExpiringValue<V> var3);
}

