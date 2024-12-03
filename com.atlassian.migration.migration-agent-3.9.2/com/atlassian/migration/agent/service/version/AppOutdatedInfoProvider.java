/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.license.LicenseService
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  lombok.Generated
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.version;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

public class AppOutdatedInfoProvider {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppOutdatedInfoProvider.class);
    private static final String APP_OUTDATED_PATH = "/plugin/versionInfo/{pluginVersion}";
    private final HttpService httpService;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final LicenseService licenseService;

    public AppOutdatedInfoProvider(MigrationAgentConfiguration configuration, LicenseService licenseService, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(new HttpService(() -> AppOutdatedInfoProvider.buildHttpClient(okHttpProxyBuilder)), configuration, licenseService);
    }

    @VisibleForTesting
    AppOutdatedInfoProvider(HttpService httpService, MigrationAgentConfiguration migrationAgentConfiguration, LicenseService licenseService) {
        this.httpService = httpService;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.licenseService = licenseService;
    }

    private static OkHttpClient buildHttpClient(OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(5L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(20L, TimeUnit.SECONDS).build();
    }

    private UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl((String)this.migrationAgentConfiguration.getMigrationAppAggregatorUrl());
    }

    public Optional<IsOutdatedResponse> getPluginOutdatedVersionInfo(String cloudId, String pluginVersion) {
        Request request = new Request.Builder().addHeader("Accept", "application/json").url(this.getUriBuilder().path(APP_OUTDATED_PATH).queryParam("cloudId", new Object[]{cloudId}).queryParam("application", new Object[]{"confluence"}).queryParam("hosting", new Object[]{this.licenseService.isLicensedForDataCenter() ? "datacenter" : "server"}).buildAndExpand(new Object[]{pluginVersion}).toUriString()).build();
        try {
            return Optional.of(this.callJsonWithRetries(request, new TypeReference<IsOutdatedResponse>(){}));
        }
        catch (HttpException e) {
            log.warn("Failed to do outdated check with migration-app-aggregator", (Throwable)e);
            return Optional.empty();
        }
    }

    private <T> T callJsonWithRetries(Request request, TypeReference<T> bodyType) {
        RetryPolicy retryPolicy = RetryPolicyBuilder.maaClientPolicy().build();
        return (T)Failsafe.with(retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callJson(request, bodyType));
    }

    public static class IsOutdatedResponse {
        private boolean isAppOutdated;
        private NextRelease nextRelease;

        @JsonCreator
        IsOutdatedResponse(@JsonProperty(value="isOutdated") boolean isAppOutdated, @JsonProperty(value="nextRelease") NextRelease nextRelease) {
            this.isAppOutdated = isAppOutdated;
            this.nextRelease = nextRelease;
        }

        public NextRelease getNextRelease() {
            return this.nextRelease;
        }

        public boolean isAppOutdated() {
            return this.isAppOutdated;
        }

        public static class NextRelease {
            private String version;
            private Boolean withinGrace;
            private LocalDate releaseDate;
            private LocalDate upgradeBy;

            @JsonCreator
            public NextRelease(@JsonProperty(value="version") String version, @JsonProperty(value="withinGrace") Boolean withinGrace, @JsonProperty(value="releaseDate") @JsonFormat(pattern="yyyy-MM-dd") LocalDate releaseDate, @JsonProperty(value="upgradeBy") @JsonFormat(pattern="yyyy-MM-dd") LocalDate upgradeBy) {
                this.version = version;
                this.withinGrace = withinGrace;
                this.releaseDate = releaseDate;
                this.upgradeBy = upgradeBy;
            }

            public String getVersion() {
                return this.version;
            }

            public Boolean isWithinGrace() {
                return this.withinGrace;
            }

            public LocalDate getReleaseDate() {
                return this.releaseDate;
            }

            public LocalDate getUpgradeBy() {
                return this.upgradeBy;
            }
        }
    }
}

