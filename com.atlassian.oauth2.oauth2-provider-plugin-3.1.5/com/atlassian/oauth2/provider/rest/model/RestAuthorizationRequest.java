/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.model;

public class RestAuthorizationRequest {
    private final String clientId;
    private final String redirectUri;
    private final String state;
    private final String responseType;
    private String scope;
    private String codeChallengeMethod;
    private final String codeChallenge;

    RestAuthorizationRequest(String clientId, String redirectUri, String state, String responseType, String scope, String codeChallengeMethod, String codeChallenge) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.state = state;
        this.responseType = responseType;
        this.scope = scope;
        this.codeChallengeMethod = codeChallengeMethod;
        this.codeChallenge = codeChallenge;
    }

    public static RestAuthorizationRequestBuilder builder() {
        return new RestAuthorizationRequestBuilder();
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public String getState() {
        return this.state;
    }

    public String getResponseType() {
        return this.responseType;
    }

    public String getScope() {
        return this.scope;
    }

    public String getCodeChallengeMethod() {
        return this.codeChallengeMethod;
    }

    public String getCodeChallenge() {
        return this.codeChallenge;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setCodeChallengeMethod(String codeChallengeMethod) {
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestAuthorizationRequest)) {
            return false;
        }
        RestAuthorizationRequest other = (RestAuthorizationRequest)o;
        if (!other.canEqual(this)) {
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
        String this$state = this.getState();
        String other$state = other.getState();
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
            return false;
        }
        String this$responseType = this.getResponseType();
        String other$responseType = other.getResponseType();
        if (this$responseType == null ? other$responseType != null : !this$responseType.equals(other$responseType)) {
            return false;
        }
        String this$scope = this.getScope();
        String other$scope = other.getScope();
        if (this$scope == null ? other$scope != null : !this$scope.equals(other$scope)) {
            return false;
        }
        String this$codeChallengeMethod = this.getCodeChallengeMethod();
        String other$codeChallengeMethod = other.getCodeChallengeMethod();
        if (this$codeChallengeMethod == null ? other$codeChallengeMethod != null : !this$codeChallengeMethod.equals(other$codeChallengeMethod)) {
            return false;
        }
        String this$codeChallenge = this.getCodeChallenge();
        String other$codeChallenge = other.getCodeChallenge();
        return !(this$codeChallenge == null ? other$codeChallenge != null : !this$codeChallenge.equals(other$codeChallenge));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestAuthorizationRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $redirectUri = this.getRedirectUri();
        result = result * 59 + ($redirectUri == null ? 43 : $redirectUri.hashCode());
        String $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        String $responseType = this.getResponseType();
        result = result * 59 + ($responseType == null ? 43 : $responseType.hashCode());
        String $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        String $codeChallengeMethod = this.getCodeChallengeMethod();
        result = result * 59 + ($codeChallengeMethod == null ? 43 : $codeChallengeMethod.hashCode());
        String $codeChallenge = this.getCodeChallenge();
        result = result * 59 + ($codeChallenge == null ? 43 : $codeChallenge.hashCode());
        return result;
    }

    public String toString() {
        return "RestAuthorizationRequest(clientId=" + this.getClientId() + ", redirectUri=" + this.getRedirectUri() + ", state=" + this.getState() + ", responseType=" + this.getResponseType() + ", scope=" + this.getScope() + ", codeChallengeMethod=" + this.getCodeChallengeMethod() + ", codeChallenge=" + this.getCodeChallenge() + ")";
    }

    public static class RestAuthorizationRequestBuilder {
        private String clientId;
        private String redirectUri;
        private String state;
        private String responseType;
        private String scope;
        private String codeChallengeMethod;
        private String codeChallenge;

        RestAuthorizationRequestBuilder() {
        }

        public RestAuthorizationRequestBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public RestAuthorizationRequestBuilder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public RestAuthorizationRequestBuilder state(String state) {
            this.state = state;
            return this;
        }

        public RestAuthorizationRequestBuilder responseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public RestAuthorizationRequestBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public RestAuthorizationRequestBuilder codeChallengeMethod(String codeChallengeMethod) {
            this.codeChallengeMethod = codeChallengeMethod;
            return this;
        }

        public RestAuthorizationRequestBuilder codeChallenge(String codeChallenge) {
            this.codeChallenge = codeChallenge;
            return this;
        }

        public RestAuthorizationRequest build() {
            return new RestAuthorizationRequest(this.clientId, this.redirectUri, this.state, this.responseType, this.scope, this.codeChallengeMethod, this.codeChallenge);
        }

        public String toString() {
            return "RestAuthorizationRequest.RestAuthorizationRequestBuilder(clientId=" + this.clientId + ", redirectUri=" + this.redirectUri + ", state=" + this.state + ", responseType=" + this.responseType + ", scope=" + this.scope + ", codeChallengeMethod=" + this.codeChallengeMethod + ", codeChallenge=" + this.codeChallenge + ")";
        }
    }
}

