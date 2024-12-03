/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.provider.api.client;

import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.scopes.api.Scope;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ClientService {
    @Nonnull
    public Client create(@Nonnull String var1, Scope var2, @Nonnull List<String> var3);

    public Optional<Client> updateClient(@Nonnull String var1, String var2, String var3, @Nonnull List<String> var4);

    public Optional<Client> resetClientSecret(@Nonnull String var1);

    public Optional<Client> getById(@Nonnull String var1);

    public Optional<Client> getByClientId(@Nonnull String var1);

    public List<String> findRedirectUrisByClientId(@Nonnull String var1);

    public List<Client> list();

    public Optional<Client> removeById(@Nonnull String var1);

    public boolean isClientNameUnique(@Nullable String var1, @Nonnull String var2);

    public boolean isClientSecretValid(@Nonnull String var1, @Nonnull String var2);
}

