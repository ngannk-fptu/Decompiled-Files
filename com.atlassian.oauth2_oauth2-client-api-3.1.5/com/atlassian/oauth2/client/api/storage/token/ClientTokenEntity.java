/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.client.api.storage.token;

import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.ClientTokenMetadata;
import com.google.common.base.MoreObjects;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientTokenEntity
implements ClientToken,
ClientTokenMetadata {
    public static final Instant MAX_TIMESTAMP = Instant.ofEpochMilli(Long.MAX_VALUE);
    private final String id;
    private final String configId;
    private final String accessToken;
    private final Instant accessTokenExpiration;
    private final String refreshToken;
    private final Instant refreshTokenExpiration;
    private final ClientTokenMetadata.ClientTokenStatus status;
    private final Instant lastRefreshed;
    private final int refreshCount;
    private final Instant lastStatusUpdated;

    private ClientTokenEntity(@Nullable String id, @Nonnull String configId, @Nonnull String accessToken, @Nonnull Instant accessTokenExpiration, @Nullable String refreshToken, @Nullable Instant refreshTokenExpiration, @Nonnull ClientTokenMetadata.ClientTokenStatus status, @Nullable Instant lastRefreshed, int refreshCount, @Nonnull Instant lastStatusUpdated) {
        this.id = id;
        this.configId = Objects.requireNonNull(configId, "Config ID cannot be null");
        this.accessToken = Objects.requireNonNull(accessToken, "Access token cannot be null");
        this.accessTokenExpiration = Objects.requireNonNull(accessTokenExpiration, "Expiration time of the access token cannot be null");
        this.refreshToken = ClientTokenEntity.maybeRequireNonNull(refreshToken, refreshTokenExpiration != null, "Refresh token cannot be null if it's expiration time is not null");
        this.refreshTokenExpiration = ClientTokenEntity.maybeRequireNonNull(refreshTokenExpiration, refreshToken != null, "Expiration time of the non-null refresh token cannot be null");
        this.status = Objects.requireNonNull(status, "Token status cannot be null");
        this.lastRefreshed = lastRefreshed;
        this.refreshCount = refreshCount;
        this.lastStatusUpdated = Objects.requireNonNull(lastStatusUpdated, "Last status updated cannot be null");
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getConfigId() {
        return this.configId;
    }

    @Override
    @Nonnull
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    @Nonnull
    public Instant getAccessTokenExpiration() {
        return this.accessTokenExpiration;
    }

    @Override
    @Nullable
    public String getRefreshToken() {
        return this.refreshToken;
    }

    @Override
    @Nullable
    public Instant getRefreshTokenExpiration() {
        return this.refreshTokenExpiration;
    }

    @Override
    @Nonnull
    public ClientTokenMetadata.ClientTokenStatus getStatus() {
        return this.status;
    }

    @Override
    @Nullable
    public Instant getLastRefreshed() {
        return this.lastRefreshed;
    }

    @Override
    public int getRefreshCount() {
        return this.refreshCount;
    }

    @Override
    @Nonnull
    public Instant getLastStatusUpdated() {
        return this.lastStatusUpdated;
    }

    @Nonnull
    public Builder toBuilder() {
        return ClientTokenEntity.builder(this);
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public static Builder builder(ClientToken data) {
        return new Builder(data);
    }

    @Nonnull
    public static Builder builder(ClientTokenMetadata data) {
        return new Builder(data);
    }

    @Nonnull
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
        ClientTokenEntity that = (ClientTokenEntity)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getConfigId(), that.getConfigId()) && Objects.equals(this.getAccessToken(), that.getAccessToken()) && Objects.equals(this.getAccessTokenExpiration(), that.getAccessTokenExpiration()) && Objects.equals(this.getRefreshToken(), that.getRefreshToken()) && Objects.equals(this.getRefreshTokenExpiration(), that.getRefreshTokenExpiration()) && Objects.equals((Object)this.getStatus(), (Object)that.getStatus()) && Objects.equals(this.getLastRefreshed(), that.getLastRefreshed()) && Objects.equals(this.getRefreshCount(), that.getRefreshCount()) && Objects.equals(this.getLastStatusUpdated(), that.getLastStatusUpdated());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getId(), this.getConfigId(), this.getAccessToken(), this.getAccessTokenExpiration(), this.getRefreshToken(), this.getRefreshTokenExpiration(), this.getStatus(), this.getLastRefreshed(), this.getRefreshCount(), this.getLastStatusUpdated()});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("configId", (Object)this.getConfigId()).add("accessToken", (Object)"*****").add("accessTokenExpiration", (Object)this.getAccessTokenExpiration()).add("refreshToken", (Object)"*****").add("refreshTokenExpiration", (Object)this.getRefreshTokenExpiration()).add("status", (Object)this.getStatus()).add("lastRefreshed", (Object)this.getLastRefreshed()).add("refreshCount", this.getRefreshCount()).add("lastStatusUpdated", (Object)this.getLastStatusUpdated()).toString();
    }

    private static <T> T maybeRequireNonNull(T obj, boolean requireNonNull, String message) {
        return requireNonNull ? Objects.requireNonNull(obj, message) : obj;
    }

    public static final class Builder {
        private String id;
        private String configId;
        private String accessToken;
        private Instant accessTokenExpiration;
        private String refreshToken;
        private Instant refreshTokenExpiration;
        private ClientTokenMetadata.ClientTokenStatus status = ClientTokenMetadata.ClientTokenStatus.UNKNOWN;
        private Instant lastRefreshed;
        private int refreshCount;
        private Instant lastStatusUpdated;

        private Builder() {
        }

        private Builder(@Nonnull ClientToken initialData) {
            this.updateFrom(initialData);
        }

        private Builder(@Nonnull ClientTokenMetadata initialData) {
            this.updateFrom(initialData);
        }

        private Builder(@Nonnull ClientTokenEntity initialData) {
            this.updateFrom(initialData);
        }

        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        public Builder configId(@Nonnull String configId) {
            this.configId = configId;
            return this;
        }

        public Builder accessToken(@Nonnull String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder accessTokenExpiration(@Nonnull Instant accessTokenExpiration) {
            this.accessTokenExpiration = accessTokenExpiration;
            return this;
        }

        public Builder refreshToken(@Nullable String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder refreshTokenExpiration(@Nullable Instant refreshTokenExpiration) {
            this.refreshTokenExpiration = refreshTokenExpiration;
            return this;
        }

        public Builder status(@Nonnull ClientTokenMetadata.ClientTokenStatus status) {
            this.status = status;
            return this;
        }

        public Builder lastStatusUpdated(@Nonnull Instant lastStatusUpdated) {
            this.lastStatusUpdated = lastStatusUpdated;
            return this;
        }

        public Builder lastRefreshed(@Nullable Instant lastRefreshed) {
            this.lastRefreshed = lastRefreshed;
            return this;
        }

        public Builder refreshCount(int refreshCount) {
            this.refreshCount = refreshCount;
            return this;
        }

        public Builder incrementRefreshCount() {
            ++this.refreshCount;
            return this;
        }

        public Builder updateFrom(ClientToken clientToken) {
            return this.accessToken(clientToken.getAccessToken()).accessTokenExpiration(clientToken.getAccessTokenExpiration()).refreshToken(clientToken.getRefreshToken()).refreshTokenExpiration(clientToken.getRefreshTokenExpiration());
        }

        public Builder updateFrom(ClientTokenMetadata metadata) {
            return this.status(metadata.getStatus()).lastStatusUpdated(metadata.getLastStatusUpdated()).lastRefreshed(metadata.getLastRefreshed()).refreshCount(metadata.getRefreshCount());
        }

        public Builder updateFrom(ClientTokenEntity entity) {
            return this.updateFrom((ClientToken)entity).updateFrom((ClientTokenMetadata)entity).id(entity.getId()).configId(entity.getConfigId());
        }

        public ClientTokenMetadata.ClientTokenStatus getStatus() {
            return this.status;
        }

        public Instant getLastStatusUpdated() {
            return this.lastStatusUpdated;
        }

        public ClientTokenEntity build() {
            return new ClientTokenEntity(this.id, this.configId, this.accessToken, this.accessTokenExpiration, this.refreshToken, this.refreshTokenExpiration, this.status, this.lastRefreshed, this.refreshCount, this.lastStatusUpdated);
        }
    }
}

