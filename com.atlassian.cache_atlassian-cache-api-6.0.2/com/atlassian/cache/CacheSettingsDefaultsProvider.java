/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.cache.CacheSettings;
import javax.annotation.Nonnull;

@PublicSpi
public interface CacheSettingsDefaultsProvider {
    @Nonnull
    public CacheSettings getDefaults(@Nonnull String var1);
}

