/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.ia.rest;

import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/spacesmenu")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class RecentSpacesResource {
    private RecentlyViewedManager recentlyViewedManager;
    private SpaceLogoManager spaceLogoManager;
    private final TemplateRenderer templateRenderer;

    RecentSpacesResource(SpaceLogoManager spaceLogoManager, RecentlyViewedManager recentlyViewedManager, TemplateRenderer templateRenderer) {
        this.spaceLogoManager = spaceLogoManager;
        this.recentlyViewedManager = recentlyViewedManager;
        this.templateRenderer = templateRenderer;
    }

    @GET
    @Path(value="/recent")
    public Response getRecentSpaces(@DefaultValue(value="10") @QueryParam(value="limit") int limit) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        List<Space> recentSpaces = this.getRecentlyViewedSpaces(user, limit);
        List<Map> spaceMenuItems = this.getSpaceMenuItems(recentSpaces);
        return Response.ok(spaceMenuItems).build();
    }

    @GET
    @AnonymousAllowed
    public Response getMenu(@DefaultValue(value="10") @QueryParam(value="limit") int limit) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        HashMap<String, List<Map>> soyContext = new HashMap<String, List<Map>>();
        if (user != null) {
            List<Space> recentSpaces = this.getRecentlyViewedSpaces(user, limit);
            List<Map> spaceMenuItems = this.getSpaceMenuItems(recentSpaces);
            soyContext.put("recentSpaces", spaceMenuItems);
        }
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, "com.atlassian.confluence.plugins.confluence-space-ia:server-soy-resources", "Confluence.Templates.BrowseSpaces.dropdownMenu.soy", soyContext);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("template", output.toString());
        return Response.ok(result).build();
    }

    private List<Space> getRecentlyViewedSpaces(ConfluenceUser user, int limit) {
        return this.recentlyViewedManager.getRecentlyViewedSpaces(user.getKey().toString(), limit);
    }

    private List<Map> getSpaceMenuItems(List<Space> recentSpaces) {
        return recentSpaces.stream().map(this::getSpaceItem).collect(Collectors.toList());
    }

    private Map<String, String> getSpaceItem(Space space) {
        return ImmutableMap.of((Object)"name", (Object)space.getName(), (Object)"key", (Object)space.getKey(), (Object)"href", (Object)space.getDeepLinkUri().toString(), (Object)"logo", (Object)this.spaceLogoManager.getLogoUriReference(space, (User)AuthenticatedUserThreadLocal.get()));
    }
}

