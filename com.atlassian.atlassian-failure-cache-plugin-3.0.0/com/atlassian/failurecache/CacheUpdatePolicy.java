/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.failurecache.updates.MutateCacheAction;
import javax.annotation.Nullable;

public interface CacheUpdatePolicy<K, V> {
    public boolean isUpdateRecommended(K var1, ExpiringValue<V> var2);

    public MutateCacheAction<K, V> evaluateResult(K var1, ExpiringValue<V> var2, @Nullable ExpiringValue<V> var3);
}

