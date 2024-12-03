/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ClaimsRequest;
import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.IAcquireTokenParameters;
import com.microsoft.aad.msal4j.ParameterValidationUtils;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;

public class DeviceCodeFlowParameters
implements IAcquireTokenParameters {
    @NonNull
    private Set<String> scopes;
    @NonNull
    private Consumer<DeviceCode> deviceCodeConsumer;
    private ClaimsRequest claims;
    private Map<String, String> extraHttpHeaders;
    private Map<String, String> extraQueryParameters;
    private String tenant;

    private static DeviceCodeFlowParametersBuilder builder() {
        return new DeviceCodeFlowParametersBuilder();
    }

    public static DeviceCodeFlowParametersBuilder builder(Set<String> scopes, Consumer<DeviceCode> deviceCodeConsumer) {
        ParameterValidationUtils.validateNotNull("scopes", scopes);
        return DeviceCodeFlowParameters.builder().scopes(scopes).deviceCodeConsumer(deviceCodeConsumer);
    }

    @Override
    @NonNull
    public Set<String> scopes() {
        return this.scopes;
    }

    @NonNull
    public Consumer<DeviceCode> deviceCodeConsumer() {
        return this.deviceCodeConsumer;
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

    private DeviceCodeFlowParameters(@NonNull Set<String> scopes, @NonNull Consumer<DeviceCode> deviceCodeConsumer, ClaimsRequest claims, Map<String, String> extraHttpHeaders, Map<String, String> extraQueryParameters, String tenant) {
        if (scopes == null) {
            throw new NullPointerException("scopes is marked @NonNull but is null");
        }
        if (deviceCodeConsumer == null) {
            throw new NullPointerException("deviceCodeConsumer is marked @NonNull but is null");
        }
        this.scopes = scopes;
        this.deviceCodeConsumer = deviceCodeConsumer;
        this.claims = claims;
        this.extraHttpHeaders = extraHttpHeaders;
        this.extraQueryParameters = extraQueryParameters;
        this.tenant = tenant;
    }

    public static class DeviceCodeFlowParametersBuilder {
        private Set<String> scopes;
        private Consumer<DeviceCode> deviceCodeConsumer;
        private ClaimsRequest claims;
        private Map<String, String> extraHttpHeaders;
        private Map<String, String> extraQueryParameters;
        private String tenant;

        DeviceCodeFlowParametersBuilder() {
        }

        public DeviceCodeFlowParametersBuilder scopes(@NonNull Set<String> scopes) {
            if (scopes == null) {
                throw new NullPointerException("scopes is marked @NonNull but is null");
            }
            this.scopes = scopes;
            return this;
        }

        public DeviceCodeFlowParametersBuilder deviceCodeConsumer(@NonNull Consumer<DeviceCode> deviceCodeConsumer) {
            if (deviceCodeConsumer == null) {
                throw new NullPointerException("deviceCodeConsumer is marked @NonNull but is null");
            }
            this.deviceCodeConsumer = deviceCodeConsumer;
            return this;
        }

        public DeviceCodeFlowParametersBuilder claims(ClaimsRequest claims) {
            this.claims = claims;
            return this;
        }

        public DeviceCodeFlowParametersBuilder extraHttpHeaders(Map<String, String> extraHttpHeaders) {
            this.extraHttpHeaders = extraHttpHeaders;
            return this;
        }

        public DeviceCodeFlowParametersBuilder extraQueryParameters(Map<String, String> extraQueryParameters) {
            this.extraQueryParameters = extraQueryParameters;
            return this;
        }

        public DeviceCodeFlowParametersBuilder tenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public DeviceCodeFlowParameters build() {
            return new DeviceCodeFlowParameters(this.scopes, this.deviceCodeConsumer, this.claims, this.extraHttpHeaders, this.extraQueryParameters, this.tenant);
        }

        public String toString() {
            return "DeviceCodeFlowParameters.DeviceCodeFlowParametersBuilder(scopes=" + this.scopes + ", deviceCodeConsumer=" + this.deviceCodeConsumer + ", claims=" + this.claims + ", extraHttpHeaders=" + this.extraHttpHeaders + ", extraQueryParameters=" + this.extraQueryParameters + ", tenant=" + this.tenant + ")";
        }
    }
}

