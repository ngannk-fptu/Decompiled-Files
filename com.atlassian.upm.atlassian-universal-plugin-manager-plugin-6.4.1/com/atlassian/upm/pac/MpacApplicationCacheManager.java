/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.CachedReferenceEvent
 *  com.atlassian.cache.CachedReferenceListener
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.pac;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceEvent;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.ApplicationVersionSpecifier;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.upm.ApplicationKeyUtils;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.pac.MarketplaceClientManager;
import com.atlassian.upm.pac.MpacApplication;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class MpacApplicationCacheManager
implements DisposableBean {
    private final ApplicationProperties applicationProperties;
    private final CacheFactory cacheFactory;
    private final CachedReference<Option<MpacApplication>> mpacAppRef;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SysPersisted sysPersisted;
    private final MarketplaceClientManager mpacV2ClientFactory;
    private final UpmHostApplicationInformation hostApplicationInformation;
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    private final ExecutorService executorsService;

    public MpacApplicationCacheManager(ApplicationProperties applicationProperties, CacheFactory cacheFactory, SysPersisted sysPersisted, MarketplaceClientManager mpacV2ClientFactory, UpmHostApplicationInformation hostApplicationInformation, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
        this.cacheFactory = Objects.requireNonNull(cacheFactory);
        this.sysPersisted = Objects.requireNonNull(sysPersisted);
        this.mpacV2ClientFactory = Objects.requireNonNull(mpacV2ClientFactory);
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation);
        this.threadLocalDelegateExecutorFactory = Objects.requireNonNull(threadLocalDelegateExecutorFactory);
        this.mpacAppRef = this.createMpacAppRef();
        this.executorsService = Executors.newCachedThreadPool();
        this.mpacAppRef.addListener((CachedReferenceListener)new CachedReferenceListener<Option<MpacApplication>>(){

            public void onEvict(@Nonnull CachedReferenceEvent<Option<MpacApplication>> event) {
                MpacApplicationCacheManager.this.populateCache();
            }

            public void onSet(@Nonnull CachedReferenceEvent<Option<MpacApplication>> event) {
            }

            public void onReset(@Nonnull CachedReferenceEvent<Option<MpacApplication>> event) {
                MpacApplicationCacheManager.this.populateCache();
            }
        }, false);
    }

    public void populateCache() {
        ExecutorService executorService = this.threadLocalDelegateExecutorFactory.createExecutorService(this.executorsService);
        executorService.execute(() -> this.mpacAppRef.get());
    }

    CachedReference<Option<MpacApplication>> getCachedReference() {
        return this.mpacAppRef;
    }

    public void reset() {
        this.mpacAppRef.reset();
    }

    private CachedReference<Option<MpacApplication>> createMpacAppRef() {
        CacheSettings settings = new CacheSettingsBuilder().expireAfterWrite(4L, TimeUnit.HOURS).local().build();
        return this.cacheFactory.getCachedReference("app", () -> {
            if (this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
                return Option.none(MpacApplication.class);
            }
            return this.fetchMpacAppInfoForLocalBuild().orElse(this.fetchMpacAppInfoForLatest());
        }, settings);
    }

    private <A> Option<A> fetchMpacAppInfo(ApplicationVersionSpecifier q, Function<Option<ApplicationVersion>, Option<A>> found) {
        try {
            return found.apply(UpmFugueConverters.toUpmOption(this.mpacV2ClientFactory.getMarketplaceClient().applications().safeGetVersion(this.getMarketplaceApplicationKey(), q)));
        }
        catch (Exception e) {
            this.logger.warn("Error when querying application info from MPAC: " + e);
            return Option.none();
        }
    }

    private Option<MpacApplication> fetchMpacAppInfoForLatest() {
        if (this.hostApplicationInformation.isDevelopmentProductVersion()) {
            return this.fetchMpacAppInfo(ApplicationVersionSpecifier.latest(), this.toMpacInfo(true));
        }
        return Option.none();
    }

    private Option<MpacApplication> fetchMpacAppInfoForLocalBuild() {
        return this.fetchMpacAppInfo(ApplicationVersionSpecifier.buildNumber(this.hostApplicationInformation.getBuildNumber()), this.toMpacInfo(false));
    }

    private ApplicationKey getMarketplaceApplicationKey() {
        return ApplicationKeyUtils.getMarketplaceApplicationKey(ApplicationKey.valueOf(this.applicationProperties.getDisplayName()));
    }

    private Function<Option<ApplicationVersion>, Option<MpacApplication>> toMpacInfo(boolean unknown) {
        return applicationVersion -> {
            Iterator iterator = applicationVersion.iterator();
            if (iterator.hasNext()) {
                ApplicationVersion av = (ApplicationVersion)iterator.next();
                return Option.some(new MpacApplication(unknown, Option.some(av.getBuildNumber())));
            }
            if (!this.hostApplicationInformation.isDevelopmentProductVersion()) {
                return Option.some(new MpacApplication(true, Option.none(Integer.class)));
            }
            return Option.none();
        };
    }

    public void destroy() {
        this.executorsService.shutdown();
    }
}

