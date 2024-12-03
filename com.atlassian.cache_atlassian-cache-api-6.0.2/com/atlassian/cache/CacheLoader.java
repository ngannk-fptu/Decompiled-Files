/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicSpi;
import javax.annotation.Nonnull;

@PublicSpi
public interface CacheLoader<K, V> {
    @Nonnull
    public V load(@Nonnull K var1);
}

