/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.authentication.api.config.oidc;

import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.google.common.collect.ImmutableList;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OidcConfig
extends AbstractIdpConfig {
    private final String clientId;
    private final String clientSecret;
    private final String authorizationEndpoint;
    private final String tokenEndpoint;
    private final String userInfoEndpoint;
    private final boolean discoveryEnabled;
    private final List<String> additionalScopes;
    private final String usernameClaim;

    private OidcConfig(Long id, String name, boolean enabled, boolean includeCustomerLogins, boolean enableRememberMe, @Nullable ZonedDateTime lastUpdated, @Nonnull String buttonText, boolean discoveryEnabled, @Nonnull String clientId, @Nonnull String clientSecret, @Nonnull String issuer, @Nullable String authorizationEndpoint, @Nullable String tokenEndpoint, @Nullable String userInfoEndpoint, @Nonnull Iterable<String> additionalScopes, @Nullable String usernameClaim, @Nonnull JustInTimeConfig justInTimeConfig) {
        super(id, name, enabled, issuer, includeCustomerLogins, enableRememberMe, lastUpdated, buttonText, justInTimeConfig);
        this.discoveryEnabled = discoveryEnabled;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.userInfoEndpoint = userInfoEndpoint;
        this.additionalScopes = ImmutableList.copyOf(additionalScopes);
        this.usernameClaim = usernameClaim;
    }

    @Override
    @Nonnull
    public SsoType getSsoType() {
        return SsoType.OIDC;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    public String getUserInfoEndpoint() {
        return this.userInfoEndpoint;
    }

    public boolean isDiscoveryEnabled() {
        return this.discoveryEnabled;
    }

    @Nonnull
    public List<String> getAdditionalScopes() {
        return this.additionalScopes;
    }

    @Nullable
    public String getUsernameClaim() {
        return this.usernameClaim;
    }

    public static Optional<OidcConfig> from(IdpConfig config) {
        return config instanceof OidcConfig ? Optional.of((OidcConfig)config) : Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OidcConfig that = (OidcConfig)o;
        return this.discoveryEnabled == that.discoveryEnabled && Objects.equals(this.clientId, that.clientId) && Objects.equals(this.clientSecret, that.clientSecret) && Objects.equals(this.authorizationEndpoint, that.authorizationEndpoint) && Objects.equals(this.tokenEndpoint, that.tokenEndpoint) && Objects.equals(this.userInfoEndpoint, that.userInfoEndpoint) && Objects.equals(this.additionalScopes, that.additionalScopes) && Objects.equals(this.usernameClaim, that.usernameClaim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.clientId, this.clientSecret, this.authorizationEndpoint, this.tokenEndpoint, this.userInfoEndpoint, this.discoveryEnabled, this.additionalScopes, this.usernameClaim);
    }

    public String toString() {
        return "OidcConfig{clientId='" + this.clientId + '\'' + ", clientSecret='" + this.clientSecret + '\'' + ", issuerUrl='" + this.getIssuer() + '\'' + ", authorizationEndpoint='" + this.authorizationEndpoint + '\'' + ", tokenEndpoint='" + this.tokenEndpoint + '\'' + ", userInfoEndpoint='" + this.userInfoEndpoint + '\'' + ", discoveryEnabled=" + this.discoveryEnabled + ", additionalScopes=" + this.additionalScopes + ", usernameClaim='" + this.usernameClaim + '\'' + ", enabled='" + this.isEnabled() + '\'' + '}';
    }

    @Override
    public Builder toBuilder() {
        return OidcConfig.builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull OidcConfig oidcConfig) {
        return new Builder(oidcConfig);
    }

    public static class Builder
    extends AbstractIdpConfig.Builder<Builder> {
        private String clientId;
        private String clientSecret;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private String userInfoEndpoint;
        private boolean discoveryEnabled;
        private List<String> additionalScopes = Collections.emptyList();
        private String usernameClaim;

        private Builder() {
        }

        private Builder(@Nonnull OidcConfig oidcConfig) {
            super(oidcConfig);
            this.clientId = oidcConfig.getClientId();
            this.clientSecret = oidcConfig.getClientSecret();
            this.authorizationEndpoint = oidcConfig.getAuthorizationEndpoint();
            this.tokenEndpoint = oidcConfig.getTokenEndpoint();
            this.userInfoEndpoint = oidcConfig.getUserInfoEndpoint();
            this.discoveryEnabled = oidcConfig.isDiscoveryEnabled();
            this.additionalScopes = ImmutableList.copyOf(oidcConfig.getAdditionalScopes());
            this.usernameClaim = oidcConfig.getUsernameClaim();
            this.justInTimeConfig = oidcConfig.getJustInTimeConfig();
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder setAuthorizationEndpoint(String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
            return this;
        }

        public Builder setTokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public Builder setUserInfoEndpoint(String userInfoEndpoint) {
            this.userInfoEndpoint = userInfoEndpoint;
            return this;
        }

        public Builder setDiscoveryEnabled(boolean discoveryEnabled) {
            this.discoveryEnabled = discoveryEnabled;
            return this;
        }

        public Builder setAdditionalScopes(Iterable<String> additionalScopes) {
            this.additionalScopes = additionalScopes != null ? ImmutableList.copyOf(additionalScopes) : Collections.emptyList();
            return this;
        }

        public Builder setUsernameClaim(String usernameClaim) {
            this.usernameClaim = usernameClaim;
            return this;
        }

        @Override
        public Builder setJustInTimeConfig(JustInTimeConfig justInTimeConfig) {
            this.justInTimeConfig = justInTimeConfig;
            return this;
        }

        @Override
        @Nonnull
        public OidcConfig build() {
            return new OidcConfig(this.id, this.name, this.enabled, this.includeCustomerLogins, this.enableRememberMe, this.lastUpdated, this.buttonText, this.discoveryEnabled, this.clientId, this.clientSecret, this.issuer, this.authorizationEndpoint, this.tokenEndpoint, this.userInfoEndpoint, this.additionalScopes, this.usernameClaim, this.justInTimeConfig);
        }
    }
}

