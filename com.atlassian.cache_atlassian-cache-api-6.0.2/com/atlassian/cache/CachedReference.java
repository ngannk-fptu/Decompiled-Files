/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.cache.CachedReferenceListener;
import java.util.Optional;
import javax.annotation.Nonnull;

@PublicApi
public interface CachedReference<V> {
    @Nonnull
    public V get();

    public void reset();

    public boolean isPresent();

    @Nonnull
    public Optional<V> getIfPresent();

    @Deprecated
    public void addListener(@Nonnull CachedReferenceListener<V> var1, boolean var2);

    @Deprecated
    public void removeListener(@Nonnull CachedReferenceListener<V> var1);
}

