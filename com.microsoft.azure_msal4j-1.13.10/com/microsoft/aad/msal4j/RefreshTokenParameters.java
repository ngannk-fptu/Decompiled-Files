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
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

public class RefreshTokenParameters
implements IAcquireTokenParameters {
    @NonNull
    private Set<String> scopes;
    @NonNull
    private String refreshToken;
    private ClaimsRequest claims;
    private Map<String, String> extraHttpHeaders;
    private Map<String, String> extraQueryParameters;
    private String tenant;

    private static RefreshTokenParametersBuilder builder() {
        return new RefreshTokenParametersBuilder();
    }

    public static RefreshTokenParametersBuilder builder(Set<String> scopes, String refreshToken) {
        ParameterValidationUtils.validateNotBlank("refreshToken", refreshToken);
        return RefreshTokenParameters.builder().scopes(scopes).refreshToken(refreshToken);
    }

    @Override
    @NonNull
    public Set<String> scopes() {
        return this.scopes;
    }

    @NonNull
    public String refreshToken() {
        return this.refreshToken;
    }

    @Override
    public ClaimsRequest claims() {
        return this.claims;
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

    private RefreshTokenParameters(@NonNull Set<String> scopes, @NonNull String refreshToken, ClaimsRequest claims, Map<String, String> extraHttpHeaders, Map<String, String> extraQueryParameters, String tenant) {
        if (scopes == null) {
            throw new NullPointerException("scopes is marked @NonNull but is null");
        }
        if (refreshToken == null) {
            throw new NullPointerException("refreshToken is marked @NonNull but is null");
        }
        this.scopes = scopes;
        this.refreshToken = refreshToken;
        this.claims = claims;
        this.extraHttpHeaders = extraHttpHeaders;
        this.extraQueryParameters = extraQueryParameters;
        this.tenant = tenant;
    }

    public static class RefreshTokenParametersBuilder {
        private Set<String> scopes;
        private String refreshToken;
        private ClaimsRequest claims;
        private Map<String, String> extraHttpHeaders;
        private Map<String, String> extraQueryParameters;
        private String tenant;

        RefreshTokenParametersBuilder() {
        }

        public RefreshTokenParametersBuilder scopes(@NonNull Set<String> scopes) {
            if (scopes == null) {
                throw new NullPointerException("scopes is marked @NonNull but is null");
            }
            this.scopes = scopes;
            return this;
        }

        public RefreshTokenParametersBuilder refreshToken(@NonNull String refreshToken) {
            if (refreshToken == null) {
                throw new NullPointerException("refreshToken is marked @NonNull but is null");
            }
            this.refreshToken = refreshToken;
            return this;
        }

        public RefreshTokenParametersBuilder claims(ClaimsRequest claims) {
            this.claims = claims;
            return this;
        }

        public RefreshTokenParametersBuilder extraHttpHeaders(Map<String, String> extraHttpHeaders) {
            this.extraHttpHeaders = extraHttpHeaders;
            return this;
        }

        public RefreshTokenParametersBuilder extraQueryParameters(Map<String, String> extraQueryParameters) {
            this.extraQueryParameters = extraQueryParameters;
            return this;
        }

        public RefreshTokenParametersBuilder tenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public RefreshTokenParameters build() {
            return new RefreshTokenParameters(this.scopes, this.refreshToken, this.claims, this.extraHttpHeaders, this.extraQueryParameters, this.tenant);
        }

        public String toString() {
            return "RefreshTokenParameters.RefreshTokenParametersBuilder(scopes=" + this.scopes + ", refreshToken=" + this.refreshToken + ", claims=" + this.claims + ", extraHttpHeaders=" + this.extraHttpHeaders + ", extraQueryParameters=" + this.extraQueryParameters + ", tenant=" + this.tenant + ")";
        }
    }
}

