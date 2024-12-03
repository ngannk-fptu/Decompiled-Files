/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.cache.CacheConfigManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.RequiresXsrfCheck
 *  com.atlassian.user.User
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.rest;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.cache.CacheConfigManager;
import com.atlassian.confluence.cache.rest.events.AllCachesFlushEvent;
import com.atlassian.confluence.cache.rest.events.CacheManagementForbiddenEvent;
import com.atlassian.confluence.cache.rest.events.SingleCacheFlushEvent;
import com.atlassian.confluence.cache.rest.events.UpdateCacheConfigEvent;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import com.atlassian.user.User;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
@Path(value="/")
public class CacheManagementResource {
    private static final Logger log = LoggerFactory.getLogger(CacheManagementResource.class);
    private final PermissionManager permissionManager;
    private final CacheConfigManager cacheConfigManager;
    private final CacheManager cacheManager;
    private final EventPublisher eventPublisher;

    public CacheManagementResource(@ComponentImport PermissionManager permissionManager, @ComponentImport CacheConfigManager cacheConfigManager, @ComponentImport CacheManager cacheManager, @ComponentImport EventPublisher eventPublisher) {
        this.permissionManager = permissionManager;
        this.cacheConfigManager = cacheConfigManager;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    @Path(value="/cacheEntries")
    @DELETE
    public void flushCache(@FormParam(value="cacheName") String cacheName) {
        this.assertIsAdmin();
        if (cacheName == null) {
            log.warn("Flushing all caches");
            this.eventPublisher.publish((Object)new AllCachesFlushEvent());
            this.cacheManager.flushCaches();
        } else {
            log.warn("Flushing cache '{}'", (Object)cacheName);
            this.eventPublisher.publish((Object)new SingleCacheFlushEvent(cacheName));
            this.cacheManager.getCache(cacheName).removeAll();
        }
    }

    @Path(value="/cacheConfig")
    @POST
    @RequiresXsrfCheck
    public void updateCacheConfig(@FormParam(value="cacheName") String cacheName, @FormParam(value="maxElements") int maxElements) {
        log.warn("Requested to change max size of cache [{}] to [{}]", (Object)cacheName, (Object)maxElements);
        this.assertIsAdmin();
        if (maxElements <= 0) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        this.eventPublisher.publish((Object)new UpdateCacheConfigEvent(cacheName));
        log.warn("Changing max size of cache [{}] to [{}]", (Object)cacheName, (Object)maxElements);
        this.cacheConfigManager.changeMaxCacheSize(cacheName, maxElements);
    }

    private void assertIsAdmin() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.isConfluenceAdministrator((User)user)) {
            this.eventPublisher.publish((Object)new CacheManagementForbiddenEvent());
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }
}

