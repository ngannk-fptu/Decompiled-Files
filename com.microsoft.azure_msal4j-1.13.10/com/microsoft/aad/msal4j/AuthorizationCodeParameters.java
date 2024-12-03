/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ClaimsRequest;
import com.microsoft.aad.msal4j.IAcquireTokenParameters;
import com.microsoft.aad.msal4j.ParameterValidationUtils;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

public class AuthorizationCodeParameters
implements IAcquireTokenParameters {
    @NonNull
    private String authorizationCode;
    @NonNull
    private URI redirectUri;
    private Set<String> scopes;
    private ClaimsRequest claims;
    private String codeVerifier;
    private Map<String, String> extraHttpHeaders;
    private Map<String, String> extraQueryParameters;
    private String tenant;

    private static AuthorizationCodeParametersBuilder builder() {
        return new AuthorizationCodeParametersBuilder();
    }

    public static AuthorizationCodeParametersBuilder builder(String authorizationCode, URI redirectUri) {
        ParameterValidationUtils.validateNotBlank("authorizationCode", authorizationCode);
        return AuthorizationCodeParameters.builder().authorizationCode(authorizationCode).redirectUri(redirectUri);
    }

    @NonNull
    public String authorizationCode() {
        return this.authorizationCode;
    }

    @NonNull
    public URI redirectUri() {
        return this.redirectUri;
    }

    @Override
    public Set<String> scopes() {
        return this.scopes;
    }

    @Override
    public ClaimsRequest claims() {
        return this.claims;
    }

    public String codeVerifier() {
        return this.codeVerifier;
    }

    @Override
    public Map<String, String> extraHttpHeaders() {
        return this.extraHttpHeaders;
    }

    @Override
    public Map<String, String> extraQueryParameters() {
        return this.extraQueryParameters;
    }

    @Override
    public String tenant() {
        return this.tenant;
    }

    private AuthorizationCodeParameters(@NonNull String authorizationCode, @NonNull URI redirectUri, Set<String> scopes, ClaimsRequest claims, String codeVerifier, Map<String, String> extraHttpHeaders, Map<String, String> extraQueryParameters, String tenant) {
        if (authorizationCode == null) {
            throw new NullPointerException("authorizationCode is marked @NonNull but is null");
        }
        if (redirectUri == null) {
            throw new NullPointerException("redirectUri is marked @NonNull but is null");
        }
        this.authorizationCode = authorizationCode;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.claims = claims;
        this.codeVerifier = codeVerifier;
        this.extraHttpHeaders = extraHttpHeaders;
        this.extraQueryParameters = extraQueryParameters;
        this.tenant = tenant;
    }

    public static class AuthorizationCodeParametersBuilder {
        private String authorizationCode;
        private URI redirectUri;
        private Set<String> scopes;
        private ClaimsRequest claims;
        private String codeVerifier;
        private Map<String, String> extraHttpHeaders;
        private Map<String, String> extraQueryParameters;
        private String tenant;

        AuthorizationCodeParametersBuilder() {
        }

        public AuthorizationCodeParametersBuilder authorizationCode(@NonNull String authorizationCode) {
            if (authorizationCode == null) {
                throw new NullPointerException("authorizationCode is marked @NonNull but is null");
            }
            this.authorizationCode = authorizationCode;
            return this;
        }

        public AuthorizationCodeParametersBuilder redirectUri(@NonNull URI redirectUri) {
            if (redirectUri == null) {
                throw new NullPointerException("redirectUri is marked @NonNull but is null");
            }
            this.redirectUri = redirectUri;
            return this;
        }

        public AuthorizationCodeParametersBuilder scopes(Set<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public AuthorizationCodeParametersBuilder claims(ClaimsRequest claims) {
            this.claims = claims;
            return this;
        }

        public AuthorizationCodeParametersBuilder codeVerifier(String codeVerifier) {
            this.codeVerifier = codeVerifier;
            return this;
        }

        public AuthorizationCodeParametersBuilder extraHttpHeaders(Map<String, String> extraHttpHeaders) {
            this.extraHttpHeaders = extraHttpHeaders;
            return this;
        }

        public AuthorizationCodeParametersBuilder extraQueryParameters(Map<String, String> extraQueryParameters) {
            this.extraQueryParameters = extraQueryParameters;
            return this;
        }

        public AuthorizationCodeParametersBuilder tenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public AuthorizationCodeParameters build() {
            return new AuthorizationCodeParameters(this.authorizationCode, this.redirectUri, this.scopes, this.claims, this.codeVerifier, this.extraHttpHeaders, this.extraQueryParameters, this.tenant);
        }

        public String toString() {
            return "AuthorizationCodeParameters.AuthorizationCodeParametersBuilder(authorizationCode=" + this.authorizationCode + ", redirectUri=" + this.redirectUri + ", scopes=" + this.scopes + ", claims=" + this.claims + ", codeVerifier=" + this.codeVerifier + ", extraHttpHeaders=" + this.extraHttpHeaders + ", extraQueryParameters=" + this.extraQueryParameters + ", tenant=" + this.tenant + ")";
        }
    }
}

