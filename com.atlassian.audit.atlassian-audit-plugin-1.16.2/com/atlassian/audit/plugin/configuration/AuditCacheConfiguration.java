/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.ao.dao.AoCachedActionDao;
import com.atlassian.audit.ao.dao.AoCachedCategoryDao;
import com.atlassian.audit.ao.service.CachedActionsService;
import com.atlassian.audit.ao.service.CachedCategoriesService;
import com.atlassian.audit.cache.schedule.BuildCacheJobScheduler;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.service.ActionsService;
import com.atlassian.audit.service.CategoriesService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditCacheConfiguration {
    @Bean
    public ActionsService actionsService(AoCachedActionDao aoCachedActionDao) {
        return new ActionsService(aoCachedActionDao);
    }

    @Bean
    public AoCachedActionDao aoCachedActionDao(ActiveObjects ao, PropertiesProvider propertiesProvider, TransactionTemplate transactionTemplate) {
        return new AoCachedActionDao(ao, propertiesProvider, transactionTemplate);
    }

    @Bean
    public AoCachedCategoryDao aoCachedCategoryDao(ActiveObjects ao, PropertiesProvider propertiesProvider, TransactionTemplate transactionTemplate) {
        return new AoCachedCategoryDao(ao, propertiesProvider, transactionTemplate);
    }

    @Bean
    public BuildCacheJobScheduler buildCacheJobScheduler(ActiveObjects ao, CachedActionsService cachedActionsService, CachedCategoriesService cachedCategoriesService, SchedulerService schedulerService) {
        return new BuildCacheJobScheduler(ao, cachedActionsService, cachedCategoriesService, schedulerService);
    }

    @Bean
    public CachedActionsService cachedActionsService(AoCachedActionDao aoCachedActionDao) {
        return new CachedActionsService(aoCachedActionDao);
    }

    @Bean
    public CachedCategoriesService cachedCategoriesService(AoCachedCategoryDao aoCachedCategoryDao) {
        return new CachedCategoriesService(aoCachedCategoryDao);
    }

    @Bean
    public CategoriesService categoriesService(AoCachedCategoryDao aoCachedCategoryDao) {
        return new CategoriesService(aoCachedCategoryDao);
    }
}

