/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.token.refresh.dao;

import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.scopes.api.Scope;
import lombok.NonNull;

public class RefreshTokenEntity
implements RefreshToken {
    @NonNull
    private final String id;
    @NonNull
    private final String accessTokenId;
    @NonNull
    private final String clientId;
    @NonNull
    private final String userKey;
    @NonNull
    private final Scope scope;
    @NonNull
    private final String authorizationCode;
    @NonNull
    private final Long authorizationDate;
    @NonNull
    private final Long createdAt;
    private final Integer refreshCount;

    public static RefreshTokenEntityBuilder builder() {
        return new RefreshTokenEntityBuilder();
    }

    public RefreshTokenEntityBuilder toBuilder() {
        return new RefreshTokenEntityBuilder().id(this.id).accessTokenId(this.accessTokenId).clientId(this.clientId).userKey(this.userKey).scope(this.scope).authorizationCode(this.authorizationCode).authorizationDate(this.authorizationDate).createdAt(this.createdAt).refreshCount(this.refreshCount);
    }

    public String toString() {
        return "RefreshTokenEntity(id=" + this.getId() + ", accessTokenId=" + this.getAccessTokenId() + ", clientId=" + this.getClientId() + ", userKey=" + this.getUserKey() + ", scope=" + this.getScope() + ", authorizationCode=" + this.getAuthorizationCode() + ", authorizationDate=" + this.getAuthorizationDate() + ", createdAt=" + this.getCreatedAt() + ", refreshCount=" + this.getRefreshCount() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RefreshTokenEntity)) {
            return false;
        }
        RefreshTokenEntity other = (RefreshTokenEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$authorizationDate = this.getAuthorizationDate();
        Long other$authorizationDate = other.getAuthorizationDate();
        if (this$authorizationDate == null ? other$authorizationDate != null : !((Object)this$authorizationDate).equals(other$authorizationDate)) {
            return false;
        }
        Long this$createdAt = this.getCreatedAt();
        Long other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        Integer this$refreshCount = this.getRefreshCount();
        Integer other$refreshCount = other.getRefreshCount();
        if (this$refreshCount == null ? other$refreshCount != null : !((Object)this$refreshCount).equals(other$refreshCount)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$accessTokenId = this.getAccessTokenId();
        String other$accessTokenId = other.getAccessTokenId();
        if (this$accessTokenId == null ? other$accessTokenId != null : !this$accessTokenId.equals(other$accessTokenId)) {
            return false;
        }
        String this$clientId = this.getClientId();
        String other$clientId = other.getClientId();
        if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        if (this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey)) {
            return false;
        }
        Scope this$scope = this.getScope();
        Scope other$scope = other.getScope();
        if (this$scope == null ? other$scope != null : !this$scope.equals(other$scope)) {
            return false;
        }
        String this$authorizationCode = this.getAuthorizationCode();
        String other$authorizationCode = other.getAuthorizationCode();
        return !(this$authorizationCode == null ? other$authorizationCode != null : !this$authorizationCode.equals(other$authorizationCode));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RefreshTokenEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $authorizationDate = this.getAuthorizationDate();
        result = result * 59 + ($authorizationDate == null ? 43 : ((Object)$authorizationDate).hashCode());
        Long $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        Integer $refreshCount = this.getRefreshCount();
        result = result * 59 + ($refreshCount == null ? 43 : ((Object)$refreshCount).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $accessTokenId = this.getAccessTokenId();
        result = result * 59 + ($accessTokenId == null ? 43 : $accessTokenId.hashCode());
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        Scope $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        String $authorizationCode = this.getAuthorizationCode();
        result = result * 59 + ($authorizationCode == null ? 43 : $authorizationCode.hashCode());
        return result;
    }

    @Override
    @NonNull
    public String getId() {
        return this.id;
    }

    @Override
    @NonNull
    public String getAccessTokenId() {
        return this.accessTokenId;
    }

    @Override
    @NonNull
    public String getClientId() {
        return this.clientId;
    }

    @Override
    @NonNull
    public String getUserKey() {
        return this.userKey;
    }

    @Override
    @NonNull
    public Scope getScope() {
        return this.scope;
    }

    @Override
    @NonNull
    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    @Override
    @NonNull
    public Long getAuthorizationDate() {
        return this.authorizationDate;
    }

    @Override
    @NonNull
    public Long getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public Integer getRefreshCount() {
        return this.refreshCount;
    }

    public RefreshTokenEntity(@NonNull String id, @NonNull String accessTokenId, @NonNull String clientId, @NonNull String userKey, @NonNull Scope scope, @NonNull String authorizationCode, @NonNull Long authorizationDate, @NonNull Long createdAt, Integer refreshCount) {
        if (id == null) {
            throw new NullPointerException("id is marked non-null but is null");
        }
        if (accessTokenId == null) {
            throw new NullPointerException("accessTokenId is marked non-null but is null");
        }
        if (clientId == null) {
            throw new NullPointerException("clientId is marked non-null but is null");
        }
        if (userKey == null) {
            throw new NullPointerException("userKey is marked non-null but is null");
        }
        if (scope == null) {
            throw new NullPointerException("scope is marked non-null but is null");
        }
        if (authorizationCode == null) {
            throw new NullPointerException("authorizationCode is marked non-null but is null");
        }
        if (authorizationDate == null) {
            throw new NullPointerException("authorizationDate is marked non-null but is null");
        }
        if (createdAt == null) {
            throw new NullPointerException("createdAt is marked non-null but is null");
        }
        this.id = id;
        this.accessTokenId = accessTokenId;
        this.clientId = clientId;
        this.userKey = userKey;
        this.scope = scope;
        this.authorizationCode = authorizationCode;
        this.authorizationDate = authorizationDate;
        this.createdAt = createdAt;
        this.refreshCount = refreshCount;
    }

    public static class RefreshTokenEntityBuilder {
        private String id;
        private String accessTokenId;
        private String clientId;
        private String userKey;
        private Scope scope;
        private String authorizationCode;
        private Long authorizationDate;
        private Long createdAt;
        private Integer refreshCount;

        RefreshTokenEntityBuilder() {
        }

        public RefreshTokenEntityBuilder id(@NonNull String id) {
            if (id == null) {
                throw new NullPointerException("id is marked non-null but is null");
            }
            this.id = id;
            return this;
        }

        public RefreshTokenEntityBuilder accessTokenId(@NonNull String accessTokenId) {
            if (accessTokenId == null) {
                throw new NullPointerException("accessTokenId is marked non-null but is null");
            }
            this.accessTokenId = accessTokenId;
            return this;
        }

        public RefreshTokenEntityBuilder clientId(@NonNull String clientId) {
            if (clientId == null) {
                throw new NullPointerException("clientId is marked non-null but is null");
            }
            this.clientId = clientId;
            return this;
        }

        public RefreshTokenEntityBuilder userKey(@NonNull String userKey) {
            if (userKey == null) {
                throw new NullPointerException("userKey is marked non-null but is null");
            }
            this.userKey = userKey;
            return this;
        }

        public RefreshTokenEntityBuilder scope(@NonNull Scope scope) {
            if (scope == null) {
                throw new NullPointerException("scope is marked non-null but is null");
            }
            this.scope = scope;
            return this;
        }

        public RefreshTokenEntityBuilder authorizationCode(@NonNull String authorizationCode) {
            if (authorizationCode == null) {
                throw new NullPointerException("authorizationCode is marked non-null but is null");
            }
            this.authorizationCode = authorizationCode;
            return this;
        }

        public RefreshTokenEntityBuilder authorizationDate(@NonNull Long authorizationDate) {
            if (authorizationDate == null) {
                throw new NullPointerException("authorizationDate is marked non-null but is null");
            }
            this.authorizationDate = authorizationDate;
            return this;
        }

        public RefreshTokenEntityBuilder createdAt(@NonNull Long createdAt) {
            if (createdAt == null) {
                throw new NullPointerException("createdAt is marked non-null but is null");
            }
            this.createdAt = createdAt;
            return this;
        }

        public RefreshTokenEntityBuilder refreshCount(Integer refreshCount) {
            this.refreshCount = refreshCount;
            return this;
        }

        public RefreshTokenEntity build() {
            return new RefreshTokenEntity(this.id, this.accessTokenId, this.clientId, this.userKey, this.scope, this.authorizationCode, this.authorizationDate, this.createdAt, this.refreshCount);
        }

        public String toString() {
            return "RefreshTokenEntity.RefreshTokenEntityBuilder(id=" + this.id + ", accessTokenId=" + this.accessTokenId + ", clientId=" + this.clientId + ", userKey=" + this.userKey + ", scope=" + this.scope + ", authorizationCode=" + this.authorizationCode + ", authorizationDate=" + this.authorizationDate + ", createdAt=" + this.createdAt + ", refreshCount=" + this.refreshCount + ")";
        }
    }
}

