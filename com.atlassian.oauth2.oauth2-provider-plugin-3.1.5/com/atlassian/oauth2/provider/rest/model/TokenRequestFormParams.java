/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.model;

import java.util.HashMap;
import java.util.Map;

public class TokenRequestFormParams {
    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String CODE_KEY = "code";
    private static final String REDIRECT_URI_KEY = "redirect_uri";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String CODE_VERIFIER = "code_verifier";
    private final String grantType;
    private final String code;
    private final String redirectUri;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String codeVerifier;

    public Map<String, String> requiredAccessTokenParams() {
        HashMap<String, String> requiredParams = new HashMap<String, String>();
        requiredParams.put(GRANT_TYPE_KEY, this.grantType);
        requiredParams.put(CODE_KEY, this.code);
        requiredParams.put(REDIRECT_URI_KEY, this.redirectUri);
        requiredParams.put(CLIENT_ID_KEY, this.clientId);
        return requiredParams;
    }

    public Map<String, String> requiredRefreshTokenParams() {
        HashMap<String, String> requiredParams = new HashMap<String, String>();
        requiredParams.put(GRANT_TYPE_KEY, this.grantType);
        requiredParams.put(REFRESH_TOKEN_KEY, this.refreshToken);
        requiredParams.put(CLIENT_ID_KEY, this.clientId);
        return requiredParams;
    }

    public static TokenRequestFormParamsBuilder builder() {
        return new TokenRequestFormParamsBuilder();
    }

    public String getGrantType() {
        return this.grantType;
    }

    public String getCode() {
        return this.code;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getCodeVerifier() {
        return this.codeVerifier;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenRequestFormParams)) {
            return false;
        }
        TokenRequestFormParams other = (TokenRequestFormParams)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$grantType = this.getGrantType();
        String other$grantType = other.getGrantType();
        if (this$grantType == null ? other$grantType != null : !this$grantType.equals(other$grantType)) {
            return false;
        }
        String this$code = this.getCode();
        String other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) {
            return false;
        }
        String this$redirectUri = this.getRedirectUri();
        String other$redirectUri = other.getRedirectUri();
        if (this$redirectUri == null ? other$redirectUri != null : !this$redirectUri.equals(other$redirectUri)) {
            return false;
        }
        String this$clientId = this.getClientId();
        String other$clientId = other.getClientId();
        if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) {
            return false;
        }
        String this$clientSecret = this.getClientSecret();
        String other$clientSecret = other.getClientSecret();
        if (this$clientSecret == null ? other$clientSecret != null : !this$clientSecret.equals(other$clientSecret)) {
            return false;
        }
        String this$refreshToken = this.getRefreshToken();
        String other$refreshToken = other.getRefreshToken();
        if (this$refreshToken == null ? other$refreshToken != null : !this$refreshToken.equals(other$refreshToken)) {
            return false;
        }
        String this$codeVerifier = this.getCodeVerifier();
        String other$codeVerifier = other.getCodeVerifier();
        return !(this$codeVerifier == null ? other$codeVerifier != null : !this$codeVerifier.equals(other$codeVerifier));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TokenRequestFormParams;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $grantType = this.getGrantType();
        result = result * 59 + ($grantType == null ? 43 : $grantType.hashCode());
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $redirectUri = this.getRedirectUri();
        result = result * 59 + ($redirectUri == null ? 43 : $redirectUri.hashCode());
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $clientSecret = this.getClientSecret();
        result = result * 59 + ($clientSecret == null ? 43 : $clientSecret.hashCode());
        String $refreshToken = this.getRefreshToken();
        result = result * 59 + ($refreshToken == null ? 43 : $refreshToken.hashCode());
        String $codeVerifier = this.getCodeVerifier();
        result = result * 59 + ($codeVerifier == null ? 43 : $codeVerifier.hashCode());
        return result;
    }

    public String toString() {
        return "TokenRequestFormParams(grantType=" + this.getGrantType() + ", code=" + this.getCode() + ", redirectUri=" + this.getRedirectUri() + ", clientId=" + this.getClientId() + ", clientSecret=" + this.getClientSecret() + ", refreshToken=" + this.getRefreshToken() + ", codeVerifier=" + this.getCodeVerifier() + ")";
    }

    public TokenRequestFormParams(String grantType, String code, String redirectUri, String clientId, String clientSecret, String refreshToken, String codeVerifier) {
        this.grantType = grantType;
        this.code = code;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.codeVerifier = codeVerifier;
    }

    public static class TokenRequestFormParamsBuilder {
        private String grantType;
        private String code;
        private String redirectUri;
        private String clientId;
        private String clientSecret;
        private String refreshToken;
        private String codeVerifier;

        TokenRequestFormParamsBuilder() {
        }

        public TokenRequestFormParamsBuilder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public TokenRequestFormParamsBuilder code(String code) {
            this.code = code;
            return this;
        }

        public TokenRequestFormParamsBuilder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public TokenRequestFormParamsBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public TokenRequestFormParamsBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public TokenRequestFormParamsBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenRequestFormParamsBuilder codeVerifier(String codeVerifier) {
            this.codeVerifier = codeVerifier;
            return this;
        }

        public TokenRequestFormParams build() {
            return new TokenRequestFormParams(this.grantType, this.code, this.redirectUri, this.clientId, this.clientSecret, this.refreshToken, this.codeVerifier);
        }

        public String toString() {
            return "TokenRequestFormParams.TokenRequestFormParamsBuilder(grantType=" + this.grantType + ", code=" + this.code + ", redirectUri=" + this.redirectUri + ", clientId=" + this.clientId + ", clientSecret=" + this.clientSecret + ", refreshToken=" + this.refreshToken + ", codeVerifier=" + this.codeVerifier + ")";
        }
    }
}

