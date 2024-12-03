/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.model;

public class RevokeRequestFormParams {
    private final String token;
    private final String tokenTypeHint;
    private final String clientId;
    private final String clientSecret;

    public static RevokeRequestFormParamsBuilder builder() {
        return new RevokeRequestFormParamsBuilder();
    }

    public RevokeRequestFormParams(String token, String tokenTypeHint, String clientId, String clientSecret) {
        this.token = token;
        this.tokenTypeHint = tokenTypeHint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getToken() {
        return this.token;
    }

    public String getTokenTypeHint() {
        return this.tokenTypeHint;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RevokeRequestFormParams)) {
            return false;
        }
        RevokeRequestFormParams other = (RevokeRequestFormParams)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$token = this.getToken();
        String other$token = other.getToken();
        if (this$token == null ? other$token != null : !this$token.equals(other$token)) {
            return false;
        }
        String this$tokenTypeHint = this.getTokenTypeHint();
        String other$tokenTypeHint = other.getTokenTypeHint();
        if (this$tokenTypeHint == null ? other$tokenTypeHint != null : !this$tokenTypeHint.equals(other$tokenTypeHint)) {
            return false;
        }
        String this$clientId = this.getClientId();
        String other$clientId = other.getClientId();
        if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) {
            return false;
        }
        String this$clientSecret = this.getClientSecret();
        String other$clientSecret = other.getClientSecret();
        return !(this$clientSecret == null ? other$clientSecret != null : !this$clientSecret.equals(other$clientSecret));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RevokeRequestFormParams;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $token = this.getToken();
        result = result * 59 + ($token == null ? 43 : $token.hashCode());
        String $tokenTypeHint = this.getTokenTypeHint();
        result = result * 59 + ($tokenTypeHint == null ? 43 : $tokenTypeHint.hashCode());
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $clientSecret = this.getClientSecret();
        result = result * 59 + ($clientSecret == null ? 43 : $clientSecret.hashCode());
        return result;
    }

    public String toString() {
        return "RevokeRequestFormParams(token=" + this.getToken() + ", tokenTypeHint=" + this.getTokenTypeHint() + ", clientId=" + this.getClientId() + ", clientSecret=" + this.getClientSecret() + ")";
    }

    public static class RevokeRequestFormParamsBuilder {
        private String token;
        private String tokenTypeHint;
        private String clientId;
        private String clientSecret;

        RevokeRequestFormParamsBuilder() {
        }

        public RevokeRequestFormParamsBuilder token(String token) {
            this.token = token;
            return this;
        }

        public RevokeRequestFormParamsBuilder tokenTypeHint(String tokenTypeHint) {
            this.tokenTypeHint = tokenTypeHint;
            return this;
        }

        public RevokeRequestFormParamsBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public RevokeRequestFormParamsBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public RevokeRequestFormParams build() {
            return new RevokeRequestFormParams(this.token, this.tokenTypeHint, this.clientId, this.clientSecret);
        }

        public String toString() {
            return "RevokeRequestFormParams.RevokeRequestFormParamsBuilder(token=" + this.token + ", tokenTypeHint=" + this.tokenTypeHint + ", clientId=" + this.clientId + ", clientSecret=" + this.clientSecret + ")";
        }
    }
}

