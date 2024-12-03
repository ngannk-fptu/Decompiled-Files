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

public class IntegratedWindowsAuthenticationParameters
implements IAcquireTokenParameters {
    @NonNull
    private Set<String> scopes;
    @NonNull
    private String username;
    private ClaimsRequest claims;
    private Map<String, String> extraHttpHeaders;
    private Map<String, String> extraQueryParameters;
    private String tenant;

    private static IntegratedWindowsAuthenticationParametersBuilder builder() {
        return new IntegratedWindowsAuthenticationParametersBuilder();
    }

    public static IntegratedWindowsAuthenticationParametersBuilder builder(Set<String> scopes, String username) {
        ParameterValidationUtils.validateNotNull("scopes", scopes);
        ParameterValidationUtils.validateNotBlank("username", username);
        return IntegratedWindowsAuthenticationParameters.builder().scopes(scopes).username(username);
    }

    @Override
    @NonNull
    public Set<String> scopes() {
        return this.scopes;
    }

    @NonNull
    public String username() {
        return this.username;
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

    private IntegratedWindowsAuthenticationParameters(@NonNull Set<String> scopes, @NonNull String username, ClaimsRequest claims, Map<String, String> extraHttpHeaders, Map<String, String> extraQueryParameters, String tenant) {
        if (scopes == null) {
            throw new NullPointerException("scopes is marked @NonNull but is null");
        }
        if (username == null) {
            throw new NullPointerException("username is marked @NonNull but is null");
        }
        this.scopes = scopes;
        this.username = username;
        this.claims = claims;
        this.extraHttpHeaders = extraHttpHeaders;
        this.extraQueryParameters = extraQueryParameters;
        this.tenant = tenant;
    }

    public static class IntegratedWindowsAuthenticationParametersBuilder {
        private Set<String> scopes;
        private String username;
        private ClaimsRequest claims;
        private Map<String, String> extraHttpHeaders;
        private Map<String, String> extraQueryParameters;
        private String tenant;

        IntegratedWindowsAuthenticationParametersBuilder() {
        }

        public IntegratedWindowsAuthenticationParametersBuilder scopes(@NonNull Set<String> scopes) {
            if (scopes == null) {
                throw new NullPointerException("scopes is marked @NonNull but is null");
            }
            this.scopes = scopes;
            return this;
        }

        public IntegratedWindowsAuthenticationParametersBuilder username(@NonNull String username) {
            if (username == null) {
                throw new NullPointerException("username is marked @NonNull but is null");
            }
            this.username = username;
            return this;
        }

        public IntegratedWindowsAuthenticationParametersBuilder claims(ClaimsRequest claims) {
            this.claims = claims;
            return this;
        }

        public IntegratedWindowsAuthenticationParametersBuilder extraHttpHeaders(Map<String, String> extraHttpHeaders) {
            this.extraHttpHeaders = extraHttpHeaders;
            return this;
        }

        public IntegratedWindowsAuthenticationParametersBuilder extraQueryParameters(Map<String, String> extraQueryParameters) {
            this.extraQueryParameters = extraQueryParameters;
            return this;
        }

        public IntegratedWindowsAuthenticationParametersBuilder tenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public IntegratedWindowsAuthenticationParameters build() {
            return new IntegratedWindowsAuthenticationParameters(this.scopes, this.username, this.claims, this.extraHttpHeaders, this.extraQueryParameters, this.tenant);
        }

        public String toString() {
            return "IntegratedWindowsAuthenticationParameters.IntegratedWindowsAuthenticationParametersBuilder(scopes=" + this.scopes + ", username=" + this.username + ", claims=" + this.claims + ", extraHttpHeaders=" + this.extraHttpHeaders + ", extraQueryParameters=" + this.extraQueryParameters + ", tenant=" + this.tenant + ")";
        }
    }
}

