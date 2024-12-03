/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ApplinksRequestCache {
    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String var1, @Nonnull Class<K> var2, @Nonnull Class<V> var3);

    public static interface Cache<K, V> {
        public void put(@Nonnull K var1, @Nonnull V var2);

        @Nullable
        public V get(@Nonnull K var1);
    }
}

