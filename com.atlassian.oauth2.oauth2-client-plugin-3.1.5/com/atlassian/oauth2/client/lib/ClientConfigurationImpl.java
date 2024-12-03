/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.storage.config.ProviderType
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.lib;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ClientConfigurationImpl
implements ClientConfiguration,
Serializable {
    private static final long serialVersionUID = 2992149181956490064L;
    private final ProviderType providerType;
    private final String clientId;
    private final String clientSecret;
    private final String authorizationEndpoint;
    private final String tokenEndpoint;
    private final List<String> scopes;

    private ClientConfigurationImpl(Builder builder) {
        this.providerType = Objects.requireNonNull(builder.providerType, "Provider type cannot be null");
        this.clientId = Objects.requireNonNull(builder.clientId, "Client ID cannot be null");
        this.clientSecret = Objects.requireNonNull(builder.clientSecret, "Client secret cannot be null");
        this.authorizationEndpoint = Objects.requireNonNull(builder.authorizationEndpoint, "Authorization endpoint cannot be null");
        this.tokenEndpoint = Objects.requireNonNull(builder.tokenEndpoint, "Token endpoint cannot be null");
        this.scopes = ImmutableList.copyOf((Collection)builder.scopes);
    }

    @Nonnull
    public String getClientId() {
        return this.clientId;
    }

    @Nonnull
    public String getClientSecret() {
        return this.clientSecret;
    }

    @Nonnull
    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    @Nonnull
    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    @Nonnull
    public List<String> getScopes() {
        return this.scopes;
    }

    @Nonnull
    public ProviderType getProviderType() {
        return this.providerType;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public static Builder builder(ClientConfiguration data) {
        return new Builder(data);
    }

    @Nonnull
    public static ClientConfigurationImpl from(ClientConfiguration configuration) {
        return configuration instanceof ClientConfigurationImpl ? (ClientConfigurationImpl)configuration : ClientConfigurationImpl.builder(configuration).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientConfigurationImpl that = (ClientConfigurationImpl)o;
        return Objects.equals(this.getProviderType(), that.getProviderType()) && Objects.equals(this.getClientId(), that.getClientId()) && Objects.equals(this.getClientSecret(), that.getClientSecret()) && Objects.equals(this.getAuthorizationEndpoint(), that.getAuthorizationEndpoint()) && Objects.equals(this.getTokenEndpoint(), that.getTokenEndpoint()) && Objects.equals(this.getScopes(), that.getScopes());
    }

    public int hashCode() {
        return Objects.hash(this.getProviderType(), this.getClientId(), this.getClientSecret(), this.getAuthorizationEndpoint(), this.getTokenEndpoint(), this.getScopes());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("providerType", (Object)this.getProviderType()).add("clientId", (Object)this.getClientId()).add("clientSecret", (Object)"*****").add("authorizationEndpoint", (Object)this.getAuthorizationEndpoint()).add("tokenEndpoint", (Object)this.getTokenEndpoint()).add("scopes", this.getScopes()).toString();
    }

    public static final class Builder {
        private ProviderType providerType;
        private String clientId;
        private String clientSecret;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private List<String> scopes = new ArrayList<String>();

        private Builder() {
        }

        private Builder(ClientConfiguration initialData) {
            this.providerType = initialData.getProviderType();
            this.clientId = initialData.getClientId();
            this.clientSecret = initialData.getClientSecret();
            this.authorizationEndpoint = initialData.getAuthorizationEndpoint();
            this.tokenEndpoint = initialData.getTokenEndpoint();
            this.scopes = new ArrayList<String>(initialData.getScopes());
        }

        public Builder providerType(ProviderType providerType) {
            this.providerType = providerType;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder authorizationEndpoint(String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
            return this;
        }

        public Builder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public Builder scopes(List<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder addScope(String scope) {
            this.scopes.add(scope);
            return this;
        }

        public ClientConfigurationImpl build() {
            return new ClientConfigurationImpl(this);
        }
    }
}

