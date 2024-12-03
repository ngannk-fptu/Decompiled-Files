/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 *  okhttp3.Interceptor
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.atlassian.migration.agent.service.impl.StargateHelper;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.google.common.annotations.VisibleForTesting;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class MigrationPlatformService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MigrationPlatformService.class);
    private static final String GET_CONTAINER_TOKEN_VALIDITY_PATH = "/token/validation";
    private final HttpService httpService;
    private final MigrationAgentConfiguration configuration;

    public MigrationPlatformService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(new HttpService(() -> MigrationPlatformService.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder)), configuration);
    }

    @VisibleForTesting
    MigrationPlatformService(HttpService httpService, MigrationAgentConfiguration configuration) {
        this.httpService = httpService;
        this.configuration = configuration;
    }

    private static OkHttpClient buildHttpClient(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(60L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(60L, TimeUnit.SECONDS).addInterceptor((Interceptor)userAgentInterceptor).build();
    }

    private UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getMigrationServiceBaseUrl());
    }

    public Date getContainerTokenExpiry(String containerToken) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).url(this.getUriBuilder().path(GET_CONTAINER_TOKEN_VALIDITY_PATH).toUriString()).addHeader("Accept", "application/json").addHeader("credential.username", "x-container-token").get().build();
        return this.callJsonWithRetries(request, new TypeReference<ContainerTokenExpiryResponse>(){}).expiry;
    }

    private <T> T callJsonWithRetries(Request request, TypeReference<T> bodyType) {
        return (T)Failsafe.with(RetryPolicyBuilder.policyForMigrationServices(), (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callJson(request, bodyType));
    }

    static class ContainerTokenExpiryResponse {
        private Date expiry;

        @JsonCreator
        ContainerTokenExpiryResponse(@JsonProperty(value="expiry") Date expiry) {
            this.expiry = expiry;
        }
    }
}

