/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.api.storage.config;

import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ClientConfigStorageService {
    @Nonnull
    public ClientConfigurationEntity save(@Nonnull ClientConfigurationEntity var1) throws ConfigurationNotFoundException;

    public void delete(@Nonnull String var1) throws ConfigurationNotFoundException;

    @Nonnull
    public Optional<ClientConfigurationEntity> getById(@Nonnull String var1);

    @Nonnull
    public ClientConfigurationEntity getByIdOrFail(@Nonnull String var1) throws ConfigurationNotFoundException;

    @Nonnull
    public Optional<ClientConfigurationEntity> getByName(@Nonnull String var1);

    @Nonnull
    public List<ClientConfigurationEntity> list();

    public boolean isNameUnique(@Nullable String var1, @Nonnull String var2);
}

