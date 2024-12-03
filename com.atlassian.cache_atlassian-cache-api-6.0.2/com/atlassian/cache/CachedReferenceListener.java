/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.cache.CachedReferenceEvent;
import javax.annotation.Nonnull;

@PublicApi
public interface CachedReferenceListener<V> {
    public void onEvict(@Nonnull CachedReferenceEvent<V> var1);

    public void onSet(@Nonnull CachedReferenceEvent<V> var1);

    public void onReset(@Nonnull CachedReferenceEvent<V> var1);
}

