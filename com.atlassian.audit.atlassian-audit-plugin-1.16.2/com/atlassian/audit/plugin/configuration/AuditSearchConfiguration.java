/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.web.context.HttpContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.ao.service.AnalyticsTrackedAuditSearchService;
import com.atlassian.audit.ao.service.AuditedSearchService;
import com.atlassian.audit.ao.service.DatabaseAuditSearchService;
import com.atlassian.audit.ao.service.RateLimitedSearchService;
import com.atlassian.audit.ao.service.RestrictiveSearchService;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.web.context.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditSearchConfiguration {
    @Bean
    public AuditSearchService auditSearchService(AuditEntityDao auditEntityDao, PermissionChecker permissionChecker, EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, AuditService auditService, HttpContext httpContext, PropertiesProvider propertiesProvider) {
        int maxConcurrentTextSearchRequests = propertiesProvider.getInteger("plugin.audit.search.max.concurrent.text.requests", 5);
        int maxConcurrentNonTextSearchRequests = propertiesProvider.getInteger("plugin.audit.search.max.concurrent.nontext.requests", 10);
        int queryTimeoutSeconds = propertiesProvider.getInteger("plugin.audit.search.query.timeout", 30);
        return new RestrictiveSearchService(permissionChecker, new AuditedSearchService(new AnalyticsTrackedAuditSearchService(new RateLimitedSearchService(maxConcurrentTextSearchRequests, maxConcurrentNonTextSearchRequests, queryTimeoutSeconds, new DatabaseAuditSearchService(auditEntityDao)), eventPublisher, auditPluginInfo, httpContext), auditService));
    }
}

