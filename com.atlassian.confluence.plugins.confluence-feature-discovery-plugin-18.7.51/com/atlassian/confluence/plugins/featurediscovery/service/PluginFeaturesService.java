/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.featurediscovery.service;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.annotation.Nonnull;

@PublicApi
public interface PluginFeaturesService {
    public boolean isDiscovered(@Nonnull ConfluenceUser var1, @Nonnull String var2);

    public void markDiscovered(@Nonnull ConfluenceUser var1, @Nonnull String var2);

    public void markUndiscovered(@Nonnull ConfluenceUser var1, @Nonnull String var2);
}

