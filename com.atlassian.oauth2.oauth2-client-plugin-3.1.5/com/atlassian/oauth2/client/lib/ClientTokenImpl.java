/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.lib;

import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientTokenImpl
implements ClientToken,
Serializable {
    private static final long serialVersionUID = 3636715502656798667L;
    private final String accessToken;
    private final Instant accessTokenExpiration;
    private final String refreshToken;
    private final Instant refreshTokenExpiration;

    private ClientTokenImpl(@Nonnull String accessToken, @Nonnull Instant accessTokenExpiration, @Nullable String refreshToken, @Nullable Instant refreshTokenExpiration) {
        this.accessToken = Objects.requireNonNull(accessToken, "Access token cannot be null");
        this.accessTokenExpiration = Objects.requireNonNull(accessTokenExpiration, "Expiration time of the access token cannot be null");
        this.refreshToken = refreshToken;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Nonnull
    public String getAccessToken() {
        return this.accessToken;
    }

    @Nonnull
    public Instant getAccessTokenExpiration() {
        return this.accessTokenExpiration;
    }

    @Nullable
    public String getRefreshToken() {
        return this.refreshToken;
    }

    @Nullable
    public Instant getRefreshTokenExpiration() {
        return this.refreshTokenExpiration;
    }

    public static ClientTokenImpl from(ClientToken clientToken) {
        return clientToken instanceof ClientTokenImpl ? (ClientTokenImpl)clientToken : ClientTokenImpl.builder(clientToken).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ClientToken data) {
        return new Builder(data);
    }

    public static Builder builder(ClientTokenEntity data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientTokenImpl that = (ClientTokenImpl)o;
        return Objects.equals(this.getAccessToken(), that.getAccessToken()) && Objects.equals(this.getAccessTokenExpiration(), that.getAccessTokenExpiration()) && Objects.equals(this.getRefreshToken(), that.getRefreshToken()) && Objects.equals(this.getRefreshTokenExpiration(), that.getRefreshTokenExpiration());
    }

    public int hashCode() {
        return Objects.hash(this.getAccessToken(), this.getAccessTokenExpiration(), this.getRefreshToken(), this.getRefreshTokenExpiration());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("accessToken", (Object)"*****").add("accessTokenExpiration", (Object)this.getAccessTokenExpiration()).add("refreshToken", (Object)"*****").add("refreshTokenExpiration", (Object)this.getRefreshTokenExpiration()).toString();
    }

    public static final class Builder {
        private String accessToken;
        private Instant accessTokenExpiration;
        private String refreshToken;
        private Instant refreshTokenExpiration;

        private Builder() {
        }

        private Builder(ClientToken initialData) {
            this.accessToken = initialData.getAccessToken();
            this.accessTokenExpiration = initialData.getAccessTokenExpiration();
            this.refreshToken = initialData.getRefreshToken();
            this.refreshTokenExpiration = initialData.getRefreshTokenExpiration();
        }

        private Builder(ClientTokenEntity initialData) {
            this.accessToken = initialData.getAccessToken();
            this.accessTokenExpiration = initialData.getAccessTokenExpiration();
            this.refreshToken = initialData.getRefreshToken();
            this.refreshTokenExpiration = initialData.getRefreshTokenExpiration();
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder accessTokenExpiration(Instant accessTokenExpiration) {
            this.accessTokenExpiration = accessTokenExpiration;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder refreshTokenExpiration(Instant refreshTokenExpiration) {
            this.refreshTokenExpiration = refreshTokenExpiration;
            return this;
        }

        public ClientTokenImpl build() {
            return new ClientTokenImpl(this.accessToken, this.accessTokenExpiration, this.refreshToken, this.refreshTokenExpiration);
        }
    }
}

