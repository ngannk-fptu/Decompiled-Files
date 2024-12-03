/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkService
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.atlassian.fugue.Pair
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
package com.atlassian.plugins.navlink.consumer.projectshortcuts.rest;

import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugins.navlink.consumer.projectshortcuts.rest.ProjectShortCutEnvelope;
import com.atlassian.plugins.navlink.consumer.projectshortcuts.rest.UnauthenticatedRemoteApplication;
import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkService;
import com.atlassian.plugins.navlink.producer.contentlinks.plugin.ContentLinkModuleDescriptor;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntityFactory;
import com.atlassian.plugins.navlink.producer.contentlinks.services.ContentLinksService;
import com.atlassian.plugins.navlink.util.url.UrlFactory;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.Collections;
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
public class ProjectShortcutsResource {
    private ContentLinksService contentLinksService;
    private CustomContentLinkService customContentLinksService;
    private UrlFactory urlFactory;

    public ProjectShortcutsResource(ContentLinksService contentLinksService, CustomContentLinkService customContentLinksService, UrlFactory urlFactory) {
        this.contentLinksService = contentLinksService;
        this.customContentLinksService = customContentLinksService;
        this.urlFactory = urlFactory;
    }

    @GET
    @Path(value="local/{key}")
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response getLocalContentLinksAndProjectDetails(@PathParam(value="key") String key, @Nullable @QueryParam(value="entityType") String entityType, @Context HttpServletRequest request) {
        if (StringUtils.isBlank((CharSequence)key)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Please provide a project key").build();
        }
        HashMap context = Maps.newHashMap();
        context.put("key", key);
        List<ContentLinkModuleDescriptor> localContentLinksDescriptors = this.contentLinksService.getAllLocalContentLinks(context, entityType != null ? new TypeId(entityType) : null);
        ContentLinkEntityFactory factory = new ContentLinkEntityFactory(request, context, this.urlFactory);
        ArrayList<ContentLinkEntity> contentLinkEntities = new ArrayList<ContentLinkEntity>(factory.create(localContentLinksDescriptors));
        contentLinkEntities.addAll(factory.createFromCustomContentLinks(this.customContentLinksService.getPluginCustomContentLinks(key), false));
        return Response.ok((Object)new ProjectShortCutEnvelope(contentLinkEntities, Collections.emptyList())).build();
    }

    @GET
    @Path(value="remote/{key}")
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response getRemoteContentLinks(@PathParam(value="key") String key, @QueryParam(value="entityType") String entityType, @Context HttpServletRequest request) {
        if (StringUtils.isBlank((CharSequence)key)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Please provide a project key").build();
        }
        if (StringUtils.isBlank((CharSequence)entityType)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Please provide an entityType").build();
        }
        HashMap context = Maps.newHashMap();
        context.put("key", key);
        Pair<Iterable<ContentLinkEntity>, Iterable<UnauthenticatedRemoteApplication>> allRemoteContentLinksAndUnauthedApps = this.contentLinksService.getAllRemoteContentLinksAndUnauthedApps(key, new TypeId(entityType));
        ArrayList remoteContentLinks = Lists.newArrayList((Iterable)((Iterable)allRemoteContentLinksAndUnauthedApps.left()));
        remoteContentLinks.addAll(new ContentLinkEntityFactory(request, context, this.urlFactory).createFromCustomContentLinks(this.customContentLinksService.getCustomContentLinks(key), false));
        return Response.ok((Object)new ProjectShortCutEnvelope(remoteContentLinks, Lists.newArrayList((Iterable)((Iterable)allRemoteContentLinksAndUnauthedApps.right())))).build();
    }
}

