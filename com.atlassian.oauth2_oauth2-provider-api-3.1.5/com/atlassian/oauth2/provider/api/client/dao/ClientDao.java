/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.provider.api.client.dao;

import com.atlassian.oauth2.provider.api.client.Client;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ClientDao {
    @Nonnull
    public Client create(@Nonnull Client var1);

    public Optional<Client> updateClient(@Nonnull String var1, String var2, String var3, @Nonnull List<String> var4);

    public Optional<Client> resetClientSecret(@Nonnull String var1, @Nonnull String var2);

    public Optional<Client> removeById(@Nonnull String var1);

    public boolean isClientNameUnique(@Nullable String var1, @Nonnull String var2);

    @Nonnull
    public List<Client> list();

    @Nonnull
    public Optional<Client> getById(@Nonnull String var1);

    @Nonnull
    public Optional<Client> getByClientId(@Nonnull String var1);

    @Nonnull
    public Optional<String> getClientIdById(@Nonnull String var1);
}

