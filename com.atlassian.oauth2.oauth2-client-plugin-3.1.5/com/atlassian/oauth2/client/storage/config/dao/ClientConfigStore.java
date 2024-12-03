/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.storage.config.dao;

import com.atlassian.annotations.Internal;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public interface ClientConfigStore {
    @Nonnull
    public ClientConfigurationEntity create(@Nonnull ClientConfigurationEntity var1);

    @Nonnull
    public ClientConfigurationEntity update(@Nonnull ClientConfigurationEntity var1) throws ConfigurationNotFoundException;

    public void delete(@Nonnull String var1) throws ConfigurationNotFoundException;

    @Nullable
    public ClientConfigurationEntity getById(@Nonnull String var1);

    @Nonnull
    public ClientConfigurationEntity getByIdOrFail(@Nonnull String var1) throws ConfigurationNotFoundException;

    public Optional<ClientConfigurationEntity> getByName(String var1);

    @Nonnull
    public List<ClientConfigurationEntity> list();

    public boolean isNameUnique(@Nullable String var1, @Nonnull String var2);
}

