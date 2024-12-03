/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkService
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.navlink.producer.contentlinks.rest;

import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkService;
import com.atlassian.plugins.navlink.producer.contentlinks.plugin.ContentLinkModuleDescriptor;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntityFactory;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinksEnvelope;
import com.atlassian.plugins.navlink.producer.contentlinks.services.ContentLinksService;
import com.atlassian.plugins.navlink.util.url.UrlFactory;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/")
public class ContentLinkResource {
    private ContentLinksService contentLinksService;
    private CustomContentLinkService customContentLinksService;
    private UrlFactory urlFactory;

    public ContentLinkResource(ContentLinksService contentLinksService, CustomContentLinkService customContentLinksService, UrlFactory urlFactory) {
        this.contentLinksService = contentLinksService;
        this.customContentLinksService = customContentLinksService;
        this.urlFactory = urlFactory;
    }

    @GET
    @Path(value="local/{key}")
    @Produces(value={"application/json"})
    public Response getLocalContentLinksAndProjectDetails(@PathParam(value="key") String key, @Nullable @QueryParam(value="entityType") String entityType, @Context HttpServletRequest request) {
        if (StringUtils.isBlank((CharSequence)key)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Please provide a project key").build();
        }
        List<ContentLinkEntity> contentLinkEntities = this.fetchContentLinks(key, request);
        return Response.ok(contentLinkEntities).build();
    }

    private List<ContentLinkEntity> fetchContentLinks(String key, HttpServletRequest request) {
        HashMap context = Maps.newHashMap();
        context.put("key", key);
        List<ContentLinkModuleDescriptor> localContentLinksDescriptors = this.contentLinksService.getAllLocalContentLinks(context, null);
        ContentLinkEntityFactory factory = new ContentLinkEntityFactory(request, context, this.urlFactory);
        ArrayList<ContentLinkEntity> contentLinkEntities = new ArrayList<ContentLinkEntity>(factory.create(localContentLinksDescriptors));
        contentLinkEntities.addAll(factory.createFromCustomContentLinks(this.customContentLinksService.getCustomContentLinks(key), true));
        contentLinkEntities.addAll(factory.createFromCustomContentLinks(this.customContentLinksService.getPluginCustomContentLinks(key), true));
        return contentLinkEntities;
    }

    @GET
    @Path(value="/{key}")
    @Produces(value={"application/json"})
    public Response getContentLinks(@PathParam(value="key") String key, @Context HttpServletRequest request) {
        if (StringUtils.isBlank((CharSequence)key)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Please provide a project key").build();
        }
        List<ContentLinkEntity> contentLinkEntities = this.fetchContentLinks(key, request);
        return Response.ok((Object)new ContentLinksEnvelope(contentLinkEntities)).build();
    }
}

