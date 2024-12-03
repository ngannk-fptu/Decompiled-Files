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
 *  okhttp3.Request$Builder
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.mo;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.atlassian.migration.agent.service.impl.StargateHelper;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.agent.service.mo.MigrationOrchestratorServiceStatusResponse;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class MigrationOrchestratorClient {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MigrationOrchestratorClient.class);
    public static final String IS_MAINTENANCE_PATH = "/migrations/service-status";
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final HttpService httpService;
    private final boolean bypassStargate;

    public MigrationOrchestratorClient(MigrationAgentConfiguration migrationAgentConfiguration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(migrationAgentConfiguration, new HttpService(() -> MigrationOrchestratorClient.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder)));
    }

    @VisibleForTesting
    public MigrationOrchestratorClient(MigrationAgentConfiguration migrationAgentConfiguration, HttpService httpService) {
        this.bypassStargate = migrationAgentConfiguration.isBypassStargate();
        this.httpService = httpService;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    private static OkHttpClient buildHttpClient(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(60L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(140L, TimeUnit.SECONDS).addInterceptor((Interceptor)userAgentInterceptor).build();
    }

    private Request.Builder getJsonBuilder(String containerToken) {
        return StargateHelper.requestBuilder(containerToken, this.bypassStargate).addHeader("Accept", "application/json");
    }

    public boolean isInMaintenance(String containerToken) {
        Request request = this.getJsonBuilder(containerToken).url(this.getUriBuilder().path(IS_MAINTENANCE_PATH).toUriString()).get().build();
        return this.callJsonWithRetries(request, new TypeReference<MigrationOrchestratorServiceStatusResponse>(){}).getMaintenance();
    }

    private <T> T callJsonWithRetries(Request request, TypeReference<T> bodyType) {
        return (T)Failsafe.with(RetryPolicyBuilder.policyForMigrationOrchestratorService(), (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callJson(request, bodyType));
    }

    private UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl((String)this.migrationAgentConfiguration.getMigrationOrchestratorServiceBaseUrl());
    }
}

