/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.assessment.AppListResponse;
import com.atlassian.migration.agent.dto.assessment.AppUsageDto;
import com.atlassian.migration.agent.dto.assessment.AppUsageStatus;
import com.atlassian.migration.agent.service.RecentlyViewedService;
import com.atlassian.migration.agent.service.analytics.AppAssessmentAnalyticsEventService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.recentlyviewed.RecentlyViewedServiceLocator;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.annotations.VisibleForTesting;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppUsageService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppUsageService.class);
    static final String CACHE_NAME = "com.atlassian.migration.agent.AppUsageCache";
    private static final String DOUBLE_QUOTE = "\"";
    private final MacroMetadataManager macroMetadataManager;
    private final CQLSearchService cqlSearchService;
    private final PluginManager pluginManager;
    private final Cache<String, AppUsageDto> appUsageCache;
    private final ExecutorService executorService;
    private final RecentlyViewedService recentlyViewedService;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService;

    public AppUsageService(MacroMetadataManager macroMetadataManager, CQLSearchService cqlSearchService, PluginManager pluginManager, CacheManager cacheManager, RecentlyViewedServiceLocator recentlyViewedServiceLocator, MigrationAgentConfiguration migrationAgentConfiguration, AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this(macroMetadataManager, cqlSearchService, pluginManager, cacheManager, recentlyViewedServiceLocator.getRecentlyViewedService(), migrationAgentConfiguration, appAssessmentAnalyticsEventService, threadLocalDelegateExecutorFactory.createExecutorService(Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)AppUsageService.class.getName(), (ThreadFactories.Type)ThreadFactories.Type.DAEMON))));
    }

    @VisibleForTesting
    AppUsageService(MacroMetadataManager macroMetadataManager, CQLSearchService cqlSearchService, PluginManager pluginManager, CacheManager cacheManager, RecentlyViewedService recentlyViewedService, MigrationAgentConfiguration migrationAgentConfiguration, AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService, ExecutorService executorService) {
        this.macroMetadataManager = macroMetadataManager;
        this.cqlSearchService = cqlSearchService;
        this.pluginManager = pluginManager;
        this.appUsageCache = cacheManager.getCache(CACHE_NAME, (CacheLoader)new AppUsageCacheLoader(), new CacheSettingsBuilder().local().expireAfterAccess(24L, TimeUnit.HOURS).build());
        this.recentlyViewedService = recentlyViewedService;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.appAssessmentAnalyticsEventService = appAssessmentAnalyticsEventService;
        this.executorService = executorService;
        this.appUsageCache.removeAll();
    }

    public AppListResponse<AppUsageDto> getAppUsageStats() {
        return new AppListResponse<AppUsageDto>(this.buildAppUsageResponse());
    }

    public AppUsageDto getAppUsageByPluginKey(String pluginKey) {
        AppUsageDto appUsageDto = (AppUsageDto)this.appUsageCache.get((Object)pluginKey);
        if (appUsageDto.getStatus().equals((Object)AppUsageStatus.SUCCESS)) {
            this.appAssessmentAnalyticsEventService.saveAppUsageAnalytics(AuthenticatedUserThreadLocal.get(), appUsageDto);
        }
        return appUsageDto;
    }

    public String clearAppUsageCache() {
        this.appUsageCache.removeAll();
        return "cleared";
    }

    private List<AppUsageDto> buildAppUsageResponse() {
        return this.pluginManager.getActualUserInstalledPlugins().stream().map(plugin -> this.getAppUsageByPluginKey(plugin.getKey())).collect(Collectors.toList());
    }

    @PreDestroy
    public void destroy() {
        this.executorService.shutdownNow();
    }

    private class AppUsageCacheLoader
    implements CacheLoader<String, AppUsageDto> {
        private AppUsageCacheLoader() {
        }

        @Nonnull
        public AppUsageDto load(@Nonnull String key) {
            log.info("Computing usage statistics of plugin with key: {}", (Object)key);
            List macroList = AppUsageService.this.macroMetadataManager.getAllMacroMetadata().stream().filter(macroMetadata -> key.equals(macroMetadata.getPluginKey())).map(MacroMetadata::getMacroName).collect(Collectors.toList());
            AppUsageDto.Builder initialDtoBuilder = AppUsageDto.builder().key(key).hasMacros(!macroList.isEmpty()).status(AppUsageStatus.RUNNING);
            if (macroList.isEmpty()) {
                initialDtoBuilder.status(AppUsageStatus.SUCCESS);
            } else {
                AppUsageService.this.executorService.submit(() -> this.computeUsageStatsByPlugin(key, macroList));
            }
            return initialDtoBuilder.build();
        }

        private void computeUsageStatsByPlugin(String pluginKey, List<String> macroList) {
            AppUsageDto.Builder builder = AppUsageDto.builder().key(pluginKey).hasMacros(true);
            try {
                long startCalculation = Instant.now().toEpochMilli();
                Set<Long> pages = this.getPagesWithMacro(macroList);
                int users = AppUsageService.this.recentlyViewedService.getUniqueUserViews(pages);
                long totalTimeTaken = Instant.now().toEpochMilli() - startCalculation;
                builder.users(users).pages(pages.size()).timeToCalculate(totalTimeTaken).status(AppUsageStatus.SUCCESS);
            }
            catch (Exception e) {
                log.error("Unable to compute usage stats of plugin with key: {}", (Object)pluginKey, (Object)e);
                builder.status(AppUsageStatus.ERROR);
            }
            AppUsageService.this.appUsageCache.put((Object)pluginKey, (Object)builder.build());
        }

        private Set<Long> getPagesWithMacro(List<String> macroList) {
            PageResponse searchResults;
            HashSet<Long> results = new HashSet<Long>();
            String searchString = this.searchString(macroList);
            int start = 0;
            int limit = this.getLimitPerPage();
            do {
                searchResults = AppUsageService.this.cqlSearchService.searchContent(searchString, (PageRequest)new SimplePageRequest(start, limit), new Expansion[0]);
                Set partialResults = searchResults.getResults().stream().map(result -> result.getId().asLong()).collect(Collectors.toSet());
                if (searchResults.hasMore()) {
                    start += this.getLimitPerPage();
                }
                results.addAll(partialResults);
            } while (searchResults.hasMore());
            return results;
        }

        private String searchString(List<String> macros) {
            String macrosAsString = macros.stream().map(macroName -> AppUsageService.DOUBLE_QUOTE + macroName + AppUsageService.DOUBLE_QUOTE).collect(Collectors.joining(", "));
            return "macro IN (" + macrosAsString + ")";
        }

        private int getLimitPerPage() {
            return AppUsageService.this.migrationAgentConfiguration.getLimitPerPageRequest();
        }
    }
}

