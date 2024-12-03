/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.Cache
 *  com.atlassian.failurecache.CacheFactory
 *  com.atlassian.failurecache.CacheLoader
 *  com.atlassian.failurecache.Cacheable
 *  com.atlassian.failurecache.Refreshable
 *  com.google.common.base.Predicate
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.failurecache.Cache;
import com.atlassian.failurecache.CacheFactory;
import com.atlassian.failurecache.CacheLoader;
import com.atlassian.failurecache.Cacheable;
import com.atlassian.failurecache.Refreshable;
import com.atlassian.plugins.navlink.consumer.menu.services.NavigationLinksCacheLoader;
import com.atlassian.plugins.navlink.consumer.menu.services.RemoteNavigationLinkService;
import com.atlassian.plugins.navlink.producer.navigation.ApplicationNavigationLinks;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.util.executor.DaemonExecutorService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CachingRemoteNavigationLinkServiceImpl
implements Cacheable,
InitializingBean,
Runnable,
Refreshable,
RemoteNavigationLinkService {
    private static final long INITIAL_DELAY_IN_SECONDS = Long.getLong("navlink.navigationlinkscache.initialdelay", 35L);
    private static final long DELAY_IN_SECONDS = Long.getLong("navlink.navigationlinkscache.delay", 10L);
    private static final Logger logger = LoggerFactory.getLogger(CachingRemoteNavigationLinkServiceImpl.class);
    private final NavigationLinksCacheLoader navigationLinksCacheLoader;
    private final Cache<ApplicationNavigationLinks> cache;
    private final DaemonExecutorService executorService;

    public CachingRemoteNavigationLinkServiceImpl(NavigationLinksCacheLoader navigationLinksCacheLoader, DaemonExecutorService executorService, CacheFactory cacheFactory) {
        this.navigationLinksCacheLoader = navigationLinksCacheLoader;
        this.cache = cacheFactory.createExpirationDateBasedCache((CacheLoader)navigationLinksCacheLoader);
        this.executorService = executorService;
    }

    @Override
    @Nonnull
    public Set<NavigationLink> all(@Nonnull Locale locale) {
        return this.matching(locale, (com.google.common.base.Predicate<NavigationLink>)((com.google.common.base.Predicate)o -> true));
    }

    @Override
    @Deprecated
    @Nonnull
    public Set<NavigationLink> matching(@Nonnull Locale locale, @Nonnull com.google.common.base.Predicate<NavigationLink> criteria) {
        return this.matching(locale, (Predicate<NavigationLink>)criteria);
    }

    @Override
    @Nonnull
    public Set<NavigationLink> matching(@Nonnull Locale locale, @Nonnull Predicate<NavigationLink> criteria) {
        return this.filterCacheByLocale(locale).stream().map(this.extractNavigationLinkSets()).flatMap(Collection::stream).filter(criteria).collect(Collectors.toSet());
    }

    @Override
    public void run() {
        this.refreshCache();
    }

    public int getCachePriority() {
        return 700;
    }

    public void clearCache() {
        this.cache.clear();
    }

    public ListenableFuture<?> refreshCache() {
        try {
            return this.cache.refresh();
        }
        catch (Exception e) {
            logger.debug("Failed to refresh remote menu items cache", (Throwable)e);
            return Futures.immediateFailedFuture((Throwable)e);
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.executorService.scheduleWithFixedDelay(this, INITIAL_DELAY_IN_SECONDS, DELAY_IN_SECONDS, TimeUnit.SECONDS);
    }

    private List<ApplicationNavigationLinks> filterCacheByLocale(Locale mostSpecificLocale) {
        Set<ApplicationNavigationLinks> cacheValues = StreamSupport.stream(this.cache.getValues().spliterator(), false).collect(Collectors.toSet());
        List<ApplicationNavigationLinks> expectedCacheHit = cacheValues.stream().filter(this.filterByLocale(mostSpecificLocale)).collect(Collectors.toList());
        if (!expectedCacheHit.isEmpty()) {
            return expectedCacheHit;
        }
        this.navigationLinksCacheLoader.cacheMissFor(mostSpecificLocale);
        Locale sameLanguage = new Locale(mostSpecificLocale.getLanguage());
        return this.filterWithFallBack(cacheValues, this.filterByLanguage(sameLanguage), this.filterByLanguage(Locale.ENGLISH));
    }

    private List<ApplicationNavigationLinks> filterWithFallBack(Set<ApplicationNavigationLinks> cacheValues, Predicate<ApplicationNavigationLinks> ... filterFunctions) {
        for (Predicate<ApplicationNavigationLinks> filterFunction : filterFunctions) {
            List<ApplicationNavigationLinks> applicationNavigationLinksForLocale = cacheValues.stream().filter(filterFunction).collect(Collectors.toList());
            if (applicationNavigationLinksForLocale.isEmpty()) continue;
            return applicationNavigationLinksForLocale;
        }
        return Collections.emptyList();
    }

    private Predicate<ApplicationNavigationLinks> filterByLocale(Locale locale) {
        return input -> input != null && input.getLocale().equals(locale);
    }

    private Predicate<ApplicationNavigationLinks> filterByLanguage(Locale locale) {
        String language = locale.getLanguage();
        return input -> input != null && input.getLocale().getLanguage().equals(language);
    }

    private Function<ApplicationNavigationLinks, Set<NavigationLink>> extractNavigationLinkSets() {
        return from -> from != null ? from.getAllNavigationLinks() : Collections.emptySet();
    }
}

