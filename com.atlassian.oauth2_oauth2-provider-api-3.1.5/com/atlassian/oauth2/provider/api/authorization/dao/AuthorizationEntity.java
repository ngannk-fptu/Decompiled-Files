/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.authorization.dao;

import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.scopes.api.Scope;
import lombok.NonNull;

public class AuthorizationEntity
implements Authorization {
    @NonNull
    private final String authorizationCode;
    @NonNull
    private final String clientId;
    @NonNull
    private final String redirectUri;
    @NonNull
    private final String userKey;
    @NonNull
    private final Long createdAt;
    @NonNull
    private final Scope scope;
    private final CodeChallengeMethod codeChallengeMethod;
    private final String codeChallenge;

    AuthorizationEntity(@NonNull String authorizationCode, @NonNull String clientId, @NonNull String redirectUri, @NonNull String userKey, @NonNull Long createdAt, @NonNull Scope scope, CodeChallengeMethod codeChallengeMethod, String codeChallenge) {
        if (authorizationCode == null) {
            throw new NullPointerException("authorizationCode is marked non-null but is null");
        }
        if (clientId == null) {
            throw new NullPointerException("clientId is marked non-null but is null");
        }
        if (redirectUri == null) {
            throw new NullPointerException("redirectUri is marked non-null but is null");
        }
        if (userKey == null) {
            throw new NullPointerException("userKey is marked non-null but is null");
        }
        if (createdAt == null) {
            throw new NullPointerException("createdAt is marked non-null but is null");
        }
        if (scope == null) {
            throw new NullPointerException("scope is marked non-null but is null");
        }
        this.authorizationCode = authorizationCode;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.userKey = userKey;
        this.createdAt = createdAt;
        this.scope = scope;
        this.codeChallengeMethod = codeChallengeMethod;
        this.codeChallenge = codeChallenge;
    }

    public static AuthorizationEntityBuilder builder() {
        return new AuthorizationEntityBuilder();
    }

    public AuthorizationEntityBuilder toBuilder() {
        return new AuthorizationEntityBuilder().authorizationCode(this.authorizationCode).clientId(this.clientId).redirectUri(this.redirectUri).userKey(this.userKey).createdAt(this.createdAt).scope(this.scope).codeChallengeMethod(this.codeChallengeMethod).codeChallenge(this.codeChallenge);
    }

    @Override
    @NonNull
    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    @Override
    @NonNull
    public String getClientId() {
        return this.clientId;
    }

    @Override
    @NonNull
    public String getRedirectUri() {
        return this.redirectUri;
    }

    @Override
    @NonNull
    public String getUserKey() {
        return this.userKey;
    }

    @Override
    @NonNull
    public Long getCreatedAt() {
        return this.createdAt;
    }

    @Override
    @NonNull
    public Scope getScope() {
        return this.scope;
    }

    @Override
    public CodeChallengeMethod getCodeChallengeMethod() {
        return this.codeChallengeMethod;
    }

    @Override
    public String getCodeChallenge() {
        return this.codeChallenge;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthorizationEntity)) {
            return false;
        }
        AuthorizationEntity other = (AuthorizationEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$createdAt = this.getCreatedAt();
        Long other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        String this$authorizationCode = this.getAuthorizationCode();
        String other$authorizationCode = other.getAuthorizationCode();
        if (this$authorizationCode == null ? other$authorizationCode != null : !this$authorizationCode.equals(other$authorizationCode)) {
            return false;
        }
        String this$clientId = this.getClientId();
        String other$clientId = other.getClientId();
        if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) {
            return false;
        }
        String this$redirectUri = this.getRedirectUri();
        String other$redirectUri = other.getRedirectUri();
        if (this$redirectUri == null ? other$redirectUri != null : !this$redirectUri.equals(other$redirectUri)) {
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
        CodeChallengeMethod this$codeChallengeMethod = this.getCodeChallengeMethod();
        CodeChallengeMethod other$codeChallengeMethod = other.getCodeChallengeMethod();
        if (this$codeChallengeMethod == null ? other$codeChallengeMethod != null : !((Object)((Object)this$codeChallengeMethod)).equals((Object)other$codeChallengeMethod)) {
            return false;
        }
        String this$codeChallenge = this.getCodeChallenge();
        String other$codeChallenge = other.getCodeChallenge();
        return !(this$codeChallenge == null ? other$codeChallenge != null : !this$codeChallenge.equals(other$codeChallenge));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AuthorizationEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        String $authorizationCode = this.getAuthorizationCode();
        result = result * 59 + ($authorizationCode == null ? 43 : $authorizationCode.hashCode());
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $redirectUri = this.getRedirectUri();
        result = result * 59 + ($redirectUri == null ? 43 : $redirectUri.hashCode());
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        Scope $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        CodeChallengeMethod $codeChallengeMethod = this.getCodeChallengeMethod();
        result = result * 59 + ($codeChallengeMethod == null ? 43 : ((Object)((Object)$codeChallengeMethod)).hashCode());
        String $codeChallenge = this.getCodeChallenge();
        result = result * 59 + ($codeChallenge == null ? 43 : $codeChallenge.hashCode());
        return result;
    }

    public String toString() {
        return "AuthorizationEntity(authorizationCode=" + this.getAuthorizationCode() + ", clientId=" + this.getClientId() + ", redirectUri=" + this.getRedirectUri() + ", userKey=" + this.getUserKey() + ", createdAt=" + this.getCreatedAt() + ", scope=" + this.getScope() + ", codeChallengeMethod=" + (Object)((Object)this.getCodeChallengeMethod()) + ", codeChallenge=" + this.getCodeChallenge() + ")";
    }

    public static class AuthorizationEntityBuilder {
        private String authorizationCode;
        private String clientId;
        private String redirectUri;
        private String userKey;
        private Long createdAt;
        private Scope scope;
        private CodeChallengeMethod codeChallengeMethod;
        private String codeChallenge;

        AuthorizationEntityBuilder() {
        }

        public AuthorizationEntityBuilder authorizationCode(@NonNull String authorizationCode) {
            if (authorizationCode == null) {
                throw new NullPointerException("authorizationCode is marked non-null but is null");
            }
            this.authorizationCode = authorizationCode;
            return this;
        }

        public AuthorizationEntityBuilder clientId(@NonNull String clientId) {
            if (clientId == null) {
                throw new NullPointerException("clientId is marked non-null but is null");
            }
            this.clientId = clientId;
            return this;
        }

        public AuthorizationEntityBuilder redirectUri(@NonNull String redirectUri) {
            if (redirectUri == null) {
                throw new NullPointerException("redirectUri is marked non-null but is null");
            }
            this.redirectUri = redirectUri;
            return this;
        }

        public AuthorizationEntityBuilder userKey(@NonNull String userKey) {
            if (userKey == null) {
                throw new NullPointerException("userKey is marked non-null but is null");
            }
            this.userKey = userKey;
            return this;
        }

        public AuthorizationEntityBuilder createdAt(@NonNull Long createdAt) {
            if (createdAt == null) {
                throw new NullPointerException("createdAt is marked non-null but is null");
            }
            this.createdAt = createdAt;
            return this;
        }

        public AuthorizationEntityBuilder scope(@NonNull Scope scope) {
            if (scope == null) {
                throw new NullPointerException("scope is marked non-null but is null");
            }
            this.scope = scope;
            return this;
        }

        public AuthorizationEntityBuilder codeChallengeMethod(CodeChallengeMethod codeChallengeMethod) {
            this.codeChallengeMethod = codeChallengeMethod;
            return this;
        }

        public AuthorizationEntityBuilder codeChallenge(String codeChallenge) {
            this.codeChallenge = codeChallenge;
            return this;
        }

        public AuthorizationEntity build() {
            return new AuthorizationEntity(this.authorizationCode, this.clientId, this.redirectUri, this.userKey, this.createdAt, this.scope, this.codeChallengeMethod, this.codeChallenge);
        }

        public String toString() {
            return "AuthorizationEntity.AuthorizationEntityBuilder(authorizationCode=" + this.authorizationCode + ", clientId=" + this.clientId + ", redirectUri=" + this.redirectUri + ", userKey=" + this.userKey + ", createdAt=" + this.createdAt + ", scope=" + this.scope + ", codeChallengeMethod=" + (Object)((Object)this.codeChallengeMethod) + ", codeChallenge=" + this.codeChallenge + ")";
        }
    }
}

