/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.client.dao;

import java.util.List;
import javax.annotation.Nonnull;

public interface RedirectUriDao {
    public void create(@Nonnull String var1, @Nonnull List<String> var2);

    public List<String> findByClientId(@Nonnull String var1);

    public void updateRedirectUris(@Nonnull String var1, @Nonnull List<String> var2);

    public void removeByClientId(@Nonnull String var1);
}

