/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentUpdateGroup;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedContentService;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroRequestParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroTabProvider;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.dto.RecentlyUpdatedContentResourceRequestDto;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.RecentlyUpdatedMacroTab;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Path(value="/")
public class RecentlyUpdatedContentResource {
    private RecentlyUpdatedContentService recentlyUpdatedContentService;
    private final RecentlyUpdatedMacroTabProvider tabProvider;
    private final LabelManager labelManager;
    private final UserAccessor userAccessor;

    public RecentlyUpdatedContentResource(RecentlyUpdatedContentService recentlyUpdatedContentService, RecentlyUpdatedMacroTabProvider tabProvider, @ComponentImport LabelManager labelManager, @ComponentImport UserAccessor userAccessor) {
        this.recentlyUpdatedContentService = recentlyUpdatedContentService;
        this.tabProvider = tabProvider;
        this.labelManager = labelManager;
        this.userAccessor = userAccessor;
    }

    @GET
    @Path(value="updates")
    @Produces(value={"application/json"})
    @XsrfProtectionExcluded
    public Response getUpdates(@QueryParam(value="tab") String tabKey, @QueryParam(value="maxResults") int maxResults, @QueryParam(value="showProfilePic") String showProfilePic, @QueryParam(value="labels") String labelsFilter, @QueryParam(value="spaces") String spacesFilter, @QueryParam(value="users") String usersFilter, @QueryParam(value="types") String typesFilter, @QueryParam(value="category") String category, @QueryParam(value="spaceKey") String spaceKey) throws Exception {
        RecentlyUpdatedMacroTab tab = this.tabProvider.getTabByName(tabKey);
        this.recentlyUpdatedContentService.setPreferredTab(tab.getName());
        this.recentlyUpdatedContentService.setPreferredMaxResults(maxResults);
        Response.ResponseBuilder responseBuilder = Response.status((Response.Status)Response.Status.BAD_REQUEST);
        if (showProfilePic == null) {
            return responseBuilder.entity((Object)"query param \"showProfilePic\" was not specified.").build();
        }
        if (labelsFilter == null) {
            return responseBuilder.entity((Object)"query param \"labels\" was not specified.").build();
        }
        if (spacesFilter == null) {
            return responseBuilder.entity((Object)"query param \"spaces\" was not specified.").build();
        }
        if (usersFilter == null) {
            return responseBuilder.entity((Object)"query param \"users\" was not specified.").build();
        }
        if (typesFilter == null) {
            return responseBuilder.entity((Object)"query param \"types\" was not specified.").build();
        }
        ImmutableMap.Builder macroParams = new ImmutableMap.Builder();
        macroParams.put((Object)"showProfilePic", (Object)showProfilePic);
        macroParams.put((Object)"labels", (Object)labelsFilter);
        macroParams.put((Object)"spaces", (Object)spacesFilter);
        macroParams.put((Object)"users", (Object)usersFilter);
        macroParams.put((Object)"types", (Object)typesFilter);
        RecentlyUpdatedMacroRequestParams macroRequestParams = new RecentlyUpdatedMacroRequestParams(maxResults, category, tabKey);
        PageContext renderContext = new PageContext(spaceKey);
        RecentUpdateQueryParameters query = tab.getQueryParameters(new RecentlyUpdatedMacroParams((Map<String, String>)macroParams.build(), this.labelManager), macroRequestParams, (RenderContext)renderContext);
        HashMap<String, Object> finalResult = new HashMap<String, Object>();
        finalResult.putAll(tab.getRenderContext(macroRequestParams, (RenderContext)renderContext));
        List<RecentUpdateGroup> changeSets = this.recentlyUpdatedContentService.getRecentUpdates(query, maxResults);
        finalResult.put("changeSets", changeSets);
        if (changeSets.isEmpty()) {
            finalResult.put("noContentMessage", tab.getNoContentMessage());
        }
        finalResult.put("tabKey", tab.getName());
        return Response.ok(finalResult).build();
    }

    @POST
    @Path(value="updates")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public Response getUpdatesPost(RecentlyUpdatedContentResourceRequestDto req) throws Exception {
        return this.getUpdates(req.getTabKey(), req.getMaxResults(), req.getShowProfilePic(), req.getLabelsFilter(), req.getSpacesFilter(), req.getUsersFilter(), req.getTypesFilter(), req.getCategory(), req.getSpaceKey());
    }

    @GET
    @Path(value="default")
    @Produces(value={"application/json"})
    @XsrfProtectionExcluded
    public Response getDefault() {
        String defaultTab = this.recentlyUpdatedContentService.getPreferredTab();
        HashMap<String, String> finalResult = new HashMap<String, String>();
        finalResult.put("default", defaultTab);
        return Response.ok(finalResult).build();
    }

    @PUT
    @Path(value="default")
    @Produces(value={"application/json"})
    public Response setDefault(@QueryParam(value="tab") String tabKey) {
        RecentlyUpdatedMacroTab tab = this.tabProvider.getTabByName(tabKey);
        this.recentlyUpdatedContentService.setPreferredTab(tab.getName());
        return Response.ok().build();
    }
}

