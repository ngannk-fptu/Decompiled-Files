/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.fugue.Pair
 *  com.atlassian.migration.app.dto.check.DisabledCheck
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 *  okhttp3.Interceptor
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.fugue.Pair;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.okhttp.MediaTypes;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.app.MigrationAppAggregatorClient;
import com.atlassian.migration.app.dto.check.DisabledCheck;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class MigrationAppAggregatorService
implements MigrationAppAggregatorClient {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MigrationAppAggregatorService.class);
    private static final String MAA_CACHE_NAME = "com.atlassian.migration.agent.migrationMarketplaceAppsCache";
    private static final String APP_LIST_CACHE_NAME = "com.atlassian.migration.agent.appAssessmentlistCache";
    private static final String APP_ASSESSMENT_BLACKLIST_KEY = "appAssessmentBlacklistKey";
    private static final String GET_BLACKLIST_URL = "/resources/blacklist";
    private static final String APP_ASSESSMENT_RELIABLE_LIST_KEY = "appAssessmentReliableListKey";
    private static final String GET_RELIABLE_LIST_URL = "/resources/reliablelist";
    private static final String GET_DISABLED_APP_VENDOR_CHECK_SPEC_URL = "aggregator/app/checks/disabled";
    private final HttpService httpService;
    private final MigrationAgentConfiguration configuration;
    private final LicenseHandler licenseHandler;
    private final Cache<Pair<Hosting, String>, MigrationAppAggregatorResponse> maaCache;
    private final Cache<String, Set<String>> appAggregatorListCache;

    public MigrationAppAggregatorService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, CacheManager cacheManager, LicenseHandler licenseHandler, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(new HttpService(() -> MigrationAppAggregatorService.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder)), configuration, cacheManager, licenseHandler);
    }

    @VisibleForTesting
    MigrationAppAggregatorService(HttpService httpService, MigrationAgentConfiguration configuration, CacheManager cacheManager, LicenseHandler licenseHandler) {
        this.maaCache = cacheManager.getCache(MAA_CACHE_NAME, this::getAggregatedAppData, new CacheSettingsBuilder().local().expireAfterWrite(55L, TimeUnit.MINUTES).build());
        this.appAggregatorListCache = cacheManager.getCache(APP_LIST_CACHE_NAME, (CacheLoader)new AppListCacheLoader(), new CacheSettingsBuilder().local().expireAfterWrite(1L, TimeUnit.DAYS).build());
        this.httpService = httpService;
        this.configuration = configuration;
        this.licenseHandler = licenseHandler;
        this.maaCache.removeAll();
        this.appAggregatorListCache.removeAll();
    }

    public MigrationAppAggregatorResponse getCachedServerAppData(String pluginKey) {
        boolean isDataCenter = this.licenseHandler.getAllProductLicenses().stream().anyMatch(BaseLicenseDetails::isDataCenter);
        return this.getCachedOrEmpty((Pair<Hosting, String>)Pair.pair((Object)((Object)(isDataCenter ? Hosting.datacenter : Hosting.server)), (Object)pluginKey));
    }

    public MigrationAppAggregatorResponse getCachedCloudAppData(String pluginKey) {
        return this.getCachedOrEmpty((Pair<Hosting, String>)Pair.pair((Object)((Object)Hosting.cloud), (Object)pluginKey));
    }

    public boolean isBlacklisted(String pluginKey) {
        this.validateAndReloadApplist(APP_ASSESSMENT_BLACKLIST_KEY);
        return ((Set)this.appAggregatorListCache.get((Object)APP_ASSESSMENT_BLACKLIST_KEY)).contains(pluginKey);
    }

    public boolean isAppReliable(String pluginKey) {
        this.validateAndReloadApplist(APP_ASSESSMENT_RELIABLE_LIST_KEY);
        return ((Set)this.appAggregatorListCache.get((Object)APP_ASSESSMENT_RELIABLE_LIST_KEY)).contains(pluginKey);
    }

    @Override
    public Set<DisabledCheck> getDisabledAppVendorChecks(Set<String> serverAppKeys) {
        try {
            Request request = new Request.Builder().addHeader("Accept", "application/json").url(this.getUriBuilder().path("/aggregator/app/checks/disabled").toUriString()).post(RequestBody.create((String)Jsons.valueAsString(serverAppKeys), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
            return this.callJsonWithRetries(request, new TypeReference<Set<DisabledCheck>>(){});
        }
        catch (HttpServiceException e) {
            log.warn("Failed to talk to app-aggregator. Returning default values. Error code: {}. Returning emptySet", (Object)e.getStatusCode());
            return Collections.emptySet();
        }
    }

    private MigrationAppAggregatorResponse getCachedOrEmpty(Pair<Hosting, String> cacheKey) {
        MigrationAppAggregatorResponse cached = (MigrationAppAggregatorResponse)this.maaCache.get(cacheKey);
        if (this.isFailedResponse(cached)) {
            this.maaCache.remove(cacheKey);
        }
        return cached;
    }

    private boolean isFailedResponse(MigrationAppAggregatorResponse cached) {
        return cached.getAggregatorHttpErrorCode() != null && cached.getAggregatorHttpErrorCode() >= 500;
    }

    @VisibleForTesting
    String getURIForList(String key) {
        if (!APP_ASSESSMENT_BLACKLIST_KEY.equals(key) && !APP_ASSESSMENT_RELIABLE_LIST_KEY.equals(key)) {
            throw new IllegalArgumentException("Invalid key used with App list cache : " + key + ". Only appAssessmentReliableListKey or appAssessmentBlacklistKey is allowed.");
        }
        if (APP_ASSESSMENT_BLACKLIST_KEY.equals(key)) {
            return GET_BLACKLIST_URL;
        }
        return GET_RELIABLE_LIST_URL;
    }

    @VisibleForTesting
    void validateAndReloadApplist(String key) {
        Set list = (Set)this.appAggregatorListCache.get((Object)key);
        if (list.isEmpty()) {
            this.appAggregatorListCache.put((Object)key, this.getAppAssessmentListFromAggregator(key));
        }
    }

    @Nonnull
    private MigrationAppAggregatorResponse getAggregatedAppData(Pair<Hosting, String> pluginKey) {
        try {
            Request request = new Request.Builder().addHeader("Accept", "application/json").url(this.getUriBuilder().path("/" + (String)pluginKey.right()).query("application=confluence").query("hosting=" + pluginKey.left()).toUriString()).get().build();
            return this.callJsonWithRetries(request, new TypeReference<MigrationAppAggregatorResponse>(){});
        }
        catch (HttpServiceException e) {
            log.warn("Failed to talk to app-aggregator. Returning default values. Error code: {}", (Object)e.getStatusCode());
            return MigrationAppAggregatorResponse.empty(e.getStatusCode());
        }
    }

    private Set<String> getAppAssessmentListFromAggregator(String key) {
        String url = this.getURIForList(key);
        try {
            Request request = new Request.Builder().addHeader("Accept", "application/json").url(this.getUriBuilder().path(url).toUriString()).get().build();
            return this.callJsonWithRetries(request, new TypeReference<HashSet<String>>(){});
        }
        catch (HttpServiceException e) {
            log.warn("Failed to get app assessment list from app-aggregator. Returning empty blacklist. Error code: {}", (Object)e.getStatusCode());
            return new HashSet<String>();
        }
    }

    private <T> T callJsonWithRetries(Request request, TypeReference<T> bodyType) {
        RetryPolicy retryPolicy = RetryPolicyBuilder.maaClientPolicy().build();
        return (T)Failsafe.with(retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callJson(request, bodyType));
    }

    private UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getMigrationAppAggregatorUrl());
    }

    private static OkHttpClient buildHttpClient(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(5L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(20L, TimeUnit.SECONDS).addInterceptor((Interceptor)userAgentInterceptor).build();
    }

    private class AppListCacheLoader
    implements CacheLoader<String, Set<String>> {
        private AppListCacheLoader() {
        }

        @Nonnull
        public Set<String> load(@Nonnull String key) {
            return MigrationAppAggregatorService.this.getAppAssessmentListFromAggregator(key);
        }
    }

    public static enum Hosting {
        server,
        datacenter,
        cloud;

    }
}

