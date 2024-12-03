/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.web.WebItemView
 *  com.atlassian.confluence.api.model.web.WebPanelView
 *  com.atlassian.confluence.api.model.web.WebSectionView
 *  com.atlassian.confluence.api.service.web.WebView
 *  com.atlassian.confluence.api.service.web.WebViewService
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.restapi.experimental.resources;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.api.model.web.WebPanelView;
import com.atlassian.confluence.api.model.web.WebSectionView;
import com.atlassian.confluence.api.service.web.WebView;
import com.atlassian.confluence.api.service.web.WebViewService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.checkerframework.checker.nullness.qual.Nullable;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/webfragment")
@GraphQLProvider
@LimitRequestSize(value=0x500000L)
public class WebFragmentResource {
    private final WebViewService service;

    public WebFragmentResource(@ComponentImport WebViewService service) {
        this.service = service;
    }

    @GET
    @Path(value="/section/{location}")
    @GraphQLName(value="webItemSections")
    public Iterable<WebSectionView> sections(@GraphQLName(value="location") @PathParam(value="location") String location, @GraphQLName(value="locations") List<String> locations, @GraphQLName(value="key") @QueryParam(value="key") String key, @GraphQLName(value="contentId") @QueryParam(value="contentId") ContentId contentId) {
        if (!locations.isEmpty()) {
            return this.getWebView(key, contentId).getSectionsForLocations((Collection)Lists.newArrayList(locations), new HashMap());
        }
        return this.getWebView(key, contentId).getSectionsForLocation(location, new HashMap());
    }

    @GET
    @Path(value="/section")
    public Iterable<WebSectionView> sections(@QueryParam(value="location") SortedSet locations, @QueryParam(value="key") String key, @QueryParam(value="contentId") ContentId contentId) {
        return this.getWebView(key, contentId).getSectionsForLocations((Collection)locations, (Map)Maps.newHashMap());
    }

    @GET
    @Path(value="/section/{location}/{section}/items/")
    @GraphQLName(value="webItems")
    public Iterable<WebItemView> items(@GraphQLName(value="location") @PathParam(value="location") String location, @GraphQLName(value="section") @PathParam(value="section") String section, @GraphQLName(value="key") @QueryParam(value="key") String key, @GraphQLName(value="contentId") @QueryParam(value="contentId") ContentId contentId) {
        return this.getWebView(key, contentId).getItemsForSection(location + "/" + section, new HashMap());
    }

    @GET
    @Path(value="/panels")
    public Iterable<WebPanelView> panels(@QueryParam(value="location") SortedSet<String> locations, @QueryParam(value="contentId") ContentId contentId) {
        return this.getWebView(null, contentId).getPanelsForLocations(locations, (Map)Maps.newHashMap());
    }

    @GET
    @Path(value="/panels/{location: .+}/")
    @GraphQLName(value="webPanels")
    public Iterable<WebPanelView> panels(@GraphQLName(value="location") @PathParam(value="location") String location, @GraphQLName(value="locations") List<String> locations, @GraphQLName(value="contentId") @QueryParam(value="contentId") ContentId contentId, @GraphQLName(value="key") @QueryParam(value="key") String key) {
        if (!locations.isEmpty()) {
            return this.getWebView(key, contentId).getPanelsForLocations(locations, new HashMap());
        }
        return this.getWebView(key, contentId).getPanelsForLocation(location, new HashMap());
    }

    private WebView getWebView(@Nullable String key, @Nullable ContentId contentId) {
        if (key != null) {
            return this.service.forSpace(key);
        }
        if (contentId != null) {
            return this.service.forContent(contentId);
        }
        return this.service.forGeneric();
    }
}

