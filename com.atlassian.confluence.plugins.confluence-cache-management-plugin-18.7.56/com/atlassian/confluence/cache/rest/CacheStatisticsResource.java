/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.cache.CacheStatisticsManager
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.cache.rest;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.CacheStatisticsManager;
import com.atlassian.confluence.cache.model.CacheStatisticsEntity;
import com.atlassian.confluence.cache.rest.events.CacheManagementForbiddenEvent;
import com.atlassian.confluence.cache.rest.events.GetCacheStatisticsEvent;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/stats")
@Produces(value={"application/json"})
public class CacheStatisticsResource {
    private final PermissionManager permissionManager;
    private final CacheStatisticsManager cacheStatisticsManager;
    private final CacheManager cacheManager;
    private final ClusterManager clusterManager;
    private final EventPublisher eventPublisher;
    private final FormatSettingsManager formatSettingsManager;
    private final I18NBeanFactory i18NBeanFactory;

    @Autowired
    public CacheStatisticsResource(@ComponentImport PermissionManager permissionManager, @ComponentImport CacheStatisticsManager cacheStatisticsManager, @ComponentImport CacheManager cacheManager, @ComponentImport ClusterManager clusterManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport I18NBeanFactory i18NBeanFactory) {
        this.permissionManager = permissionManager;
        this.cacheStatisticsManager = cacheStatisticsManager;
        this.cacheManager = cacheManager;
        this.clusterManager = clusterManager;
        this.eventPublisher = eventPublisher;
        this.formatSettingsManager = formatSettingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @GET
    @Produces(value={"application/json"})
    public List<CacheStatisticsEntity> getCacheStatistics() {
        if (this.isAdmin()) {
            long startTime = System.currentTimeMillis();
            List<CacheStatisticsEntity> response = this.buildResponseEntity();
            this.eventPublisher.publish((Object)new GetCacheStatisticsEvent(System.currentTimeMillis() - startTime));
            return response;
        }
        this.eventPublisher.publish((Object)new CacheManagementForbiddenEvent());
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    private List<CacheStatisticsEntity> buildResponseEntity() {
        return this.cacheManager.getManagedCaches().stream().map(this::createEntity).sorted(Comparator.comparing(CacheStatisticsEntity::getNiceName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
    }

    private CacheStatisticsEntity createEntity(ManagedCache managedCache) {
        return new CacheStatisticsEntity(managedCache, this.clusterManager.isClustered(), this.formatSettingsManager, this.i18NBeanFactory.getI18NBean(), this.cacheStatisticsManager.getCacheStatisticFilter(managedCache.getName()));
    }

    private boolean isAdmin() {
        return this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get());
    }
}

