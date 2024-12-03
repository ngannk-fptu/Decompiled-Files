/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.CacheRefreshService
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.consumer.admin.rest;

import com.atlassian.failurecache.CacheRefreshService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/admin")
@WebSudoRequired
public class ConsumerAdminResource {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerAdminResource.class);
    private static final long CACHE_REBUILD_TIMEOUT_IN_SECONDS = 30L;
    private final UserManager userManager;
    private final CacheRefreshService cacheRefreshService;

    public ConsumerAdminResource(UserManager userManager, CacheRefreshService cacheRefreshService) {
        this.userManager = userManager;
        this.cacheRefreshService = cacheRefreshService;
    }

    @POST
    @Path(value="/refreshcache")
    @Produces(value={"text/plain"})
    @Consumes(value={"application/json"})
    public Response clearCachesPost(@Context HttpServletRequest request) {
        return this.rebuildCaches(request);
    }

    @PUT
    @Path(value="/refreshcache")
    @Produces(value={"text/plain"})
    @Consumes(value={"application/json"})
    public Response clearCachesPut(@Context HttpServletRequest request) {
        return this.rebuildCaches(request);
    }

    private Response rebuildCaches(HttpServletRequest request) {
        UserProfile user = this.userManager.getRemoteUser(request);
        if (user == null) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"Anonymous user does not have admin permission.").build();
        }
        if (!this.userManager.isAdmin(user.getUserKey())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)String.format("User %s does not have admin permission.", user.getUsername())).build();
        }
        long before = System.currentTimeMillis();
        Future rebuildCachesFuture = this.cacheRefreshService.refreshAll(false);
        try {
            rebuildCachesFuture.get(30L, TimeUnit.SECONDS);
            logger.debug("Caches have been rebuild in {} ms.", (Object)(System.currentTimeMillis() - before));
            return Response.ok().entity((Object)String.format("Caches have been rebuilt in %d ms.", System.currentTimeMillis() - before)).build();
        }
        catch (InterruptedException e) {
            logger.debug("Interrupted while invalidating the caches", (Throwable)e);
            return Response.serverError().entity((Object)"Interrupted while rebuilding the caches.").build();
        }
        catch (CancellationException e) {
            logger.debug("Cache rebuild has been cancelled", (Throwable)e);
            return Response.ok().entity((Object)"Cache rebuild has been cancelled.").build();
        }
        catch (TimeoutException e) {
            logger.debug("Timeout exceeded while clearing the caches", (Throwable)e);
            return Response.ok().entity((Object)"Timeout exceeded while waiting for cache rebuild. Not all caches may have been rebuild yet but it is still ongoing.").build();
        }
        catch (Exception e) {
            logger.debug("Exception occurred while rebuilding the caches", (Throwable)e);
            return Response.serverError().entity((Object)("Exception occurred while rebuilding the caches: " + e.getMessage())).build();
        }
    }
}

