/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.api;

import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import java.util.List;
import javax.annotation.Nonnull;

public interface ClientConfiguration {
    @Nonnull
    public ProviderType getProviderType();

    @Nonnull
    public String getClientId();

    @Nonnull
    public String getClientSecret();

    @Nonnull
    public String getAuthorizationEndpoint();

    @Nonnull
    public String getTokenEndpoint();

    @Nonnull
    public List<String> getScopes();
}

