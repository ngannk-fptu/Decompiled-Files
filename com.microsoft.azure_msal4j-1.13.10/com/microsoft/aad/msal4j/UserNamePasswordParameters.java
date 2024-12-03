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
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

public class UserNamePasswordParameters
implements IAcquireTokenParameters {
    @NonNull
    private Set<String> scopes;
    @NonNull
    private String username;
    @NonNull
    private char[] password;
    private ClaimsRequest claims;
    private Map<String, String> extraHttpHeaders;
    private Map<String, String> extraQueryParameters;
    private String tenant;

    public char[] password() {
        return (char[])this.password.clone();
    }

    private static UserNamePasswordParametersBuilder builder() {
        return new UserNamePasswordParametersBuilder();
    }

    public static UserNamePasswordParametersBuilder builder(Set<String> scopes, String username, char[] password) {
        ParameterValidationUtils.validateNotNull("scopes", scopes);
        ParameterValidationUtils.validateNotBlank("username", username);
        ParameterValidationUtils.validateNotEmpty("password", password);
        return UserNamePasswordParameters.builder().scopes(scopes).username(username).password(password);
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

    private UserNamePasswordParameters(@NonNull Set<String> scopes, @NonNull String username, @NonNull char[] password, ClaimsRequest claims, Map<String, String> extraHttpHeaders, Map<String, String> extraQueryParameters, String tenant) {
        if (scopes == null) {
            throw new NullPointerException("scopes is marked @NonNull but is null");
        }
        if (username == null) {
            throw new NullPointerException("username is marked @NonNull but is null");
        }
        if (password == null) {
            throw new NullPointerException("password is marked @NonNull but is null");
        }
        this.scopes = scopes;
        this.username = username;
        this.password = password;
        this.claims = claims;
        this.extraHttpHeaders = extraHttpHeaders;
        this.extraQueryParameters = extraQueryParameters;
        this.tenant = tenant;
    }

    public static class UserNamePasswordParametersBuilder {
        private Set<String> scopes;
        private String username;
        private char[] password;
        private ClaimsRequest claims;
        private Map<String, String> extraHttpHeaders;
        private Map<String, String> extraQueryParameters;
        private String tenant;

        public UserNamePasswordParametersBuilder password(char[] password) {
            this.password = (char[])password.clone();
            return this;
        }

        UserNamePasswordParametersBuilder() {
        }

        public UserNamePasswordParametersBuilder scopes(@NonNull Set<String> scopes) {
            if (scopes == null) {
                throw new NullPointerException("scopes is marked @NonNull but is null");
            }
            this.scopes = scopes;
            return this;
        }

        public UserNamePasswordParametersBuilder username(@NonNull String username) {
            if (username == null) {
                throw new NullPointerException("username is marked @NonNull but is null");
            }
            this.username = username;
            return this;
        }

        public UserNamePasswordParametersBuilder claims(ClaimsRequest claims) {
            this.claims = claims;
            return this;
        }

        public UserNamePasswordParametersBuilder extraHttpHeaders(Map<String, String> extraHttpHeaders) {
            this.extraHttpHeaders = extraHttpHeaders;
            return this;
        }

        public UserNamePasswordParametersBuilder extraQueryParameters(Map<String, String> extraQueryParameters) {
            this.extraQueryParameters = extraQueryParameters;
            return this;
        }

        public UserNamePasswordParametersBuilder tenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public UserNamePasswordParameters build() {
            return new UserNamePasswordParameters(this.scopes, this.username, this.password, this.claims, this.extraHttpHeaders, this.extraQueryParameters, this.tenant);
        }

        public String toString() {
            return "UserNamePasswordParameters.UserNamePasswordParametersBuilder(scopes=" + this.scopes + ", username=" + this.username + ", password=" + Arrays.toString(this.password) + ", claims=" + this.claims + ", extraHttpHeaders=" + this.extraHttpHeaders + ", extraQueryParameters=" + this.extraQueryParameters + ", tenant=" + this.tenant + ")";
        }
    }
}

