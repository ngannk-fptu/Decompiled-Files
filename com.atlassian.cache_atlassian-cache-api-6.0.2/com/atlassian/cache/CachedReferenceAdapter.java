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
import com.atlassian.cache.CachedReferenceListener;
import javax.annotation.Nonnull;

@PublicApi
public class CachedReferenceAdapter<V>
implements CachedReferenceListener<V> {
    @Override
    public void onEvict(@Nonnull CachedReferenceEvent<V> event) {
    }

    @Override
    public void onSet(@Nonnull CachedReferenceEvent<V> event) {
    }

    @Override
    public void onReset(@Nonnull CachedReferenceEvent<V> event) {
    }
}

