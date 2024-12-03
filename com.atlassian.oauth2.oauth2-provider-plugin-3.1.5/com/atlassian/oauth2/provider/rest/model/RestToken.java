/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.oauth2.provider.rest.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class RestToken {
    @JsonProperty(value="access_token")
    private String accessToken;
    @JsonProperty(value="token_type")
    private String tokenType;
    @JsonProperty(value="expires_in")
    private Long expiresIn;
    @JsonProperty(value="refresh_token")
    private String refreshToken;
    @JsonProperty(value="scope")
    private String scope;

    public static RestTokenBuilder builder() {
        return new RestTokenBuilder();
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public Long getExpiresIn() {
        return this.expiresIn;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getScope() {
        return this.scope;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestToken)) {
            return false;
        }
        RestToken other = (RestToken)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$expiresIn = this.getExpiresIn();
        Long other$expiresIn = other.getExpiresIn();
        if (this$expiresIn == null ? other$expiresIn != null : !((Object)this$expiresIn).equals(other$expiresIn)) {
            return false;
        }
        String this$accessToken = this.getAccessToken();
        String other$accessToken = other.getAccessToken();
        if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) {
            return false;
        }
        String this$tokenType = this.getTokenType();
        String other$tokenType = other.getTokenType();
        if (this$tokenType == null ? other$tokenType != null : !this$tokenType.equals(other$tokenType)) {
            return false;
        }
        String this$refreshToken = this.getRefreshToken();
        String other$refreshToken = other.getRefreshToken();
        if (this$refreshToken == null ? other$refreshToken != null : !this$refreshToken.equals(other$refreshToken)) {
            return false;
        }
        String this$scope = this.getScope();
        String other$scope = other.getScope();
        return !(this$scope == null ? other$scope != null : !this$scope.equals(other$scope));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestToken;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $expiresIn = this.getExpiresIn();
        result = result * 59 + ($expiresIn == null ? 43 : ((Object)$expiresIn).hashCode());
        String $accessToken = this.getAccessToken();
        result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
        String $tokenType = this.getTokenType();
        result = result * 59 + ($tokenType == null ? 43 : $tokenType.hashCode());
        String $refreshToken = this.getRefreshToken();
        result = result * 59 + ($refreshToken == null ? 43 : $refreshToken.hashCode());
        String $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        return result;
    }

    public String toString() {
        return "RestToken(accessToken=" + this.getAccessToken() + ", tokenType=" + this.getTokenType() + ", expiresIn=" + this.getExpiresIn() + ", refreshToken=" + this.getRefreshToken() + ", scope=" + this.getScope() + ")";
    }

    public RestToken(String accessToken, String tokenType, Long expiresIn, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public RestToken() {
    }

    public static class RestTokenBuilder {
        private String accessToken;
        private String tokenType;
        private Long expiresIn;
        private String refreshToken;
        private String scope;

        RestTokenBuilder() {
        }

        public RestTokenBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public RestTokenBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public RestTokenBuilder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public RestTokenBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public RestTokenBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public RestToken build() {
            return new RestToken(this.accessToken, this.tokenType, this.expiresIn, this.refreshToken, this.scope);
        }

        public String toString() {
            return "RestToken.RestTokenBuilder(accessToken=" + this.accessToken + ", tokenType=" + this.tokenType + ", expiresIn=" + this.expiresIn + ", refreshToken=" + this.refreshToken + ", scope=" + this.scope + ")";
        }
    }
}

