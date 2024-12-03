/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.recentlyviewed.rest;

import com.atlassian.confluence.plugins.recentlyviewed.RecentSpace;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewed;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/recent")
@Produces(value={"application/json;charset=UTF-8"})
public class RecentlyViewedResource {
    private static final String DEFAULT_MAX_RESULTS_PAGES = "100";
    private static final String DEFAULT_MAX_RESULTS_SPACES = "5";
    private final RecentlyViewedManager recentlyViewedManager;
    private final UserManager userManager;

    @Autowired
    public RecentlyViewedResource(RecentlyViewedManager recentlyViewedManager, @ComponentImport UserManager userManager) {
        this.recentlyViewedManager = recentlyViewedManager;
        this.userManager = userManager;
    }

    @GET
    public Response getRecentlyViewed(@QueryParam(value="includeTrashedContent") boolean includeTrashedContent, @QueryParam(value="limit") @DefaultValue(value="100") int limit) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (userKey == null) {
            return Response.serverError().build();
        }
        List<RecentlyViewed> recentlyViewed = this.recentlyViewedManager.getRecentlyViewed(userKey, includeTrashedContent, limit);
        return Response.ok(recentlyViewed).build();
    }

    @GET
    @Path(value="/pages")
    public Response getRecentlyViewedPages(@QueryParam(value="noTrashedContent") boolean noTrashedContent, @QueryParam(value="limit") @DefaultValue(value="100") int limit) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (userKey == null) {
            return Response.serverError().build();
        }
        List<RecentlyViewed> recentlyViewed = this.recentlyViewedManager.getRecentlyViewedPages(userKey.getStringValue(), noTrashedContent, limit);
        return Response.ok(recentlyViewed).build();
    }

    @GET
    @Path(value="/spaces")
    public Response getRecentlyViewedSpaces(@QueryParam(value="limit") @DefaultValue(value="5") int limit) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (userKey == null) {
            return Response.serverError().build();
        }
        List recentSpaces = this.recentlyViewedManager.findRecentlyViewedSpaces(userKey.getStringValue(), limit).stream().map(RecentSpace::fromSpace).collect(Collectors.toList());
        return Response.ok(recentSpaces).build();
    }
}

