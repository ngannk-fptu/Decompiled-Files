/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.api.storage.config;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientConfigurationEntity
implements ClientConfiguration {
    private final String id;
    private final String name;
    private final ProviderType providerType;
    private final String description;
    private final String clientId;
    private final String clientSecret;
    private final String authorizationEndpoint;
    private final String tokenEndpoint;
    private final List<String> scopes;

    private ClientConfigurationEntity(Builder builder) {
        this.id = builder.id;
        this.name = Objects.requireNonNull(builder.name, "Name cannot be null");
        this.description = builder.description;
        this.providerType = Objects.requireNonNull(builder.providerType, "Provider type cannot be null");
        this.clientId = Objects.requireNonNull(builder.clientId, "Client ID cannot be null");
        this.clientSecret = Objects.requireNonNull(builder.clientSecret, "Client secret cannot be null");
        this.authorizationEndpoint = Objects.requireNonNull(builder.authorizationEndpoint, "Authorization endpoint cannot be null");
        this.tokenEndpoint = Objects.requireNonNull(builder.tokenEndpoint, "Token endpoint cannot be null");
        Preconditions.checkArgument((builder.scopes != null && !builder.scopes.isEmpty() ? 1 : 0) != 0, (Object)"At least one scope has to be provided");
        this.scopes = ImmutableList.copyOf((Collection)builder.scopes);
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Override
    @Nonnull
    public ProviderType getProviderType() {
        return this.providerType;
    }

    @Override
    @Nonnull
    public String getClientId() {
        return this.clientId;
    }

    @Override
    @Nonnull
    public String getClientSecret() {
        return this.clientSecret;
    }

    @Override
    @Nonnull
    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    @Override
    @Nonnull
    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    @Override
    @Nonnull
    public List<String> getScopes() {
        return this.scopes;
    }

    @Nonnull
    public Builder toBuilder() {
        return ClientConfigurationEntity.builder(this);
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public static Builder builder(@Nonnull ClientConfiguration data) {
        return new Builder(data);
    }

    @Nonnull
    public static Builder builder(@Nonnull ClientConfigurationEntity data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientConfigurationEntity that = (ClientConfigurationEntity)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getDescription(), that.getDescription()) && Objects.equals((Object)this.getProviderType(), (Object)that.getProviderType()) && Objects.equals(this.getClientId(), that.getClientId()) && Objects.equals(this.getClientSecret(), that.getClientSecret()) && Objects.equals(this.getAuthorizationEndpoint(), that.getAuthorizationEndpoint()) && Objects.equals(this.getTokenEndpoint(), that.getTokenEndpoint()) && Objects.equals(this.getScopes(), that.getScopes());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getId(), this.getName(), this.getDescription(), this.getProviderType(), this.getClientId(), this.getClientSecret(), this.getAuthorizationEndpoint(), this.getTokenEndpoint(), this.getScopes()});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("name", (Object)this.getName()).add("description", (Object)this.getDescription()).add("providerType", (Object)this.getProviderType()).add("clientId", (Object)this.getClientId()).add("clientSecret", (Object)"*****").add("authorizationEndpoint", (Object)this.getAuthorizationEndpoint()).add("tokenEndpoint", (Object)this.getTokenEndpoint()).add("scopes", this.getScopes()).toString();
    }

    public static final class Builder {
        private String id;
        private String name;
        private String description;
        private ProviderType providerType;
        private String clientId;
        private String clientSecret;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private List<String> scopes = new ArrayList<String>();

        private Builder() {
        }

        private Builder(@Nonnull ClientConfiguration initialData) {
            this.clientId = initialData.getClientId();
            this.clientSecret = initialData.getClientSecret();
            this.authorizationEndpoint = initialData.getAuthorizationEndpoint();
            this.tokenEndpoint = initialData.getTokenEndpoint();
            this.scopes = new ArrayList<String>(initialData.getScopes());
            this.providerType = initialData.getProviderType();
        }

        private Builder(@Nonnull ClientConfigurationEntity initialData) {
            this((ClientConfiguration)initialData);
            this.id = initialData.getId();
            this.description = initialData.getDescription();
            this.name = initialData.getName();
        }

        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        public Builder name(@Nonnull String name) {
            this.name = name;
            return this;
        }

        public Builder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public Builder providerType(@Nonnull ProviderType providerType) {
            this.providerType = providerType;
            return this;
        }

        public Builder clientId(@Nonnull String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(@Nonnull String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder authorizationEndpoint(@Nonnull String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
            return this;
        }

        public Builder tokenEndpoint(@Nonnull String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public Builder scopes(@Nonnull List<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder addScope(@Nonnull String scope) {
            this.scopes.add(scope);
            return this;
        }

        public ClientConfigurationEntity build() {
            return new ClientConfigurationEntity(this);
        }
    }
}

