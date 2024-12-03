/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.token.access.dao;

import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.scopes.api.Scope;
import lombok.NonNull;

public class AccessTokenEntity
implements AccessToken {
    @NonNull
    private final String id;
    @NonNull
    private final String clientId;
    @NonNull
    private final String userKey;
    @NonNull
    private final String authorizationCode;
    @NonNull
    private final Scope scope;
    @NonNull
    private final Long authorizationDate;
    @NonNull
    private final Long createdAt;
    private final Long lastAccessed;

    public static AccessTokenEntityBuilder builder() {
        return new AccessTokenEntityBuilder();
    }

    public AccessTokenEntityBuilder toBuilder() {
        return new AccessTokenEntityBuilder().id(this.id).clientId(this.clientId).userKey(this.userKey).authorizationCode(this.authorizationCode).scope(this.scope).authorizationDate(this.authorizationDate).createdAt(this.createdAt).lastAccessed(this.lastAccessed);
    }

    public String toString() {
        return "AccessTokenEntity(id=" + this.getId() + ", clientId=" + this.getClientId() + ", userKey=" + this.getUserKey() + ", authorizationCode=" + this.getAuthorizationCode() + ", scope=" + this.getScope() + ", authorizationDate=" + this.getAuthorizationDate() + ", createdAt=" + this.getCreatedAt() + ", lastAccessed=" + this.getLastAccessed() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AccessTokenEntity)) {
            return false;
        }
        AccessTokenEntity other = (AccessTokenEntity)o;
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
        Long this$lastAccessed = this.getLastAccessed();
        Long other$lastAccessed = other.getLastAccessed();
        if (this$lastAccessed == null ? other$lastAccessed != null : !((Object)this$lastAccessed).equals(other$lastAccessed)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
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
        String this$authorizationCode = this.getAuthorizationCode();
        String other$authorizationCode = other.getAuthorizationCode();
        if (this$authorizationCode == null ? other$authorizationCode != null : !this$authorizationCode.equals(other$authorizationCode)) {
            return false;
        }
        Scope this$scope = this.getScope();
        Scope other$scope = other.getScope();
        return !(this$scope == null ? other$scope != null : !this$scope.equals(other$scope));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AccessTokenEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $authorizationDate = this.getAuthorizationDate();
        result = result * 59 + ($authorizationDate == null ? 43 : ((Object)$authorizationDate).hashCode());
        Long $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        Long $lastAccessed = this.getLastAccessed();
        result = result * 59 + ($lastAccessed == null ? 43 : ((Object)$lastAccessed).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        String $authorizationCode = this.getAuthorizationCode();
        result = result * 59 + ($authorizationCode == null ? 43 : $authorizationCode.hashCode());
        Scope $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        return result;
    }

    @Override
    @NonNull
    public String getId() {
        return this.id;
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
    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    @Override
    @NonNull
    public Scope getScope() {
        return this.scope;
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
    public Long getLastAccessed() {
        return this.lastAccessed;
    }

    public AccessTokenEntity(@NonNull String id, @NonNull String clientId, @NonNull String userKey, @NonNull String authorizationCode, @NonNull Scope scope, @NonNull Long authorizationDate, @NonNull Long createdAt, Long lastAccessed) {
        if (id == null) {
            throw new NullPointerException("id is marked non-null but is null");
        }
        if (clientId == null) {
            throw new NullPointerException("clientId is marked non-null but is null");
        }
        if (userKey == null) {
            throw new NullPointerException("userKey is marked non-null but is null");
        }
        if (authorizationCode == null) {
            throw new NullPointerException("authorizationCode is marked non-null but is null");
        }
        if (scope == null) {
            throw new NullPointerException("scope is marked non-null but is null");
        }
        if (authorizationDate == null) {
            throw new NullPointerException("authorizationDate is marked non-null but is null");
        }
        if (createdAt == null) {
            throw new NullPointerException("createdAt is marked non-null but is null");
        }
        this.id = id;
        this.clientId = clientId;
        this.userKey = userKey;
        this.authorizationCode = authorizationCode;
        this.scope = scope;
        this.authorizationDate = authorizationDate;
        this.createdAt = createdAt;
        this.lastAccessed = lastAccessed;
    }

    public static class AccessTokenEntityBuilder {
        private String id;
        private String clientId;
        private String userKey;
        private String authorizationCode;
        private Scope scope;
        private Long authorizationDate;
        private Long createdAt;
        private Long lastAccessed;

        AccessTokenEntityBuilder() {
        }

        public AccessTokenEntityBuilder id(@NonNull String id) {
            if (id == null) {
                throw new NullPointerException("id is marked non-null but is null");
            }
            this.id = id;
            return this;
        }

        public AccessTokenEntityBuilder clientId(@NonNull String clientId) {
            if (clientId == null) {
                throw new NullPointerException("clientId is marked non-null but is null");
            }
            this.clientId = clientId;
            return this;
        }

        public AccessTokenEntityBuilder userKey(@NonNull String userKey) {
            if (userKey == null) {
                throw new NullPointerException("userKey is marked non-null but is null");
            }
            this.userKey = userKey;
            return this;
        }

        public AccessTokenEntityBuilder authorizationCode(@NonNull String authorizationCode) {
            if (authorizationCode == null) {
                throw new NullPointerException("authorizationCode is marked non-null but is null");
            }
            this.authorizationCode = authorizationCode;
            return this;
        }

        public AccessTokenEntityBuilder scope(@NonNull Scope scope) {
            if (scope == null) {
                throw new NullPointerException("scope is marked non-null but is null");
            }
            this.scope = scope;
            return this;
        }

        public AccessTokenEntityBuilder authorizationDate(@NonNull Long authorizationDate) {
            if (authorizationDate == null) {
                throw new NullPointerException("authorizationDate is marked non-null but is null");
            }
            this.authorizationDate = authorizationDate;
            return this;
        }

        public AccessTokenEntityBuilder createdAt(@NonNull Long createdAt) {
            if (createdAt == null) {
                throw new NullPointerException("createdAt is marked non-null but is null");
            }
            this.createdAt = createdAt;
            return this;
        }

        public AccessTokenEntityBuilder lastAccessed(Long lastAccessed) {
            this.lastAccessed = lastAccessed;
            return this;
        }

        public AccessTokenEntity build() {
            return new AccessTokenEntity(this.id, this.clientId, this.userKey, this.authorizationCode, this.scope, this.authorizationDate, this.createdAt, this.lastAccessed);
        }

        public String toString() {
            return "AccessTokenEntity.AccessTokenEntityBuilder(id=" + this.id + ", clientId=" + this.clientId + ", userKey=" + this.userKey + ", authorizationCode=" + this.authorizationCode + ", scope=" + this.scope + ", authorizationDate=" + this.authorizationDate + ", createdAt=" + this.createdAt + ", lastAccessed=" + this.lastAccessed + ")";
        }
    }
}

