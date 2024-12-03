/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.userstatus.FavouriteManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.efi.rest;

import com.atlassian.confluence.efi.rest.beans.SpaceBean;
import com.atlassian.confluence.efi.services.FindRelevantSpacesService;
import com.atlassian.confluence.efi.services.SpaceImportConfig;
import com.atlassian.confluence.efi.services.SpaceService;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="space")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class ConfluenceSpaceResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfluenceSpaceResource.class);
    private final SpaceManager spaceManager;
    private final SpaceService spaceService;
    private final PermissionManager permissionManager;
    private final FindRelevantSpacesService findRelevantSpacesService;
    private final FavouriteManager favouriteManager;
    private final NotificationManager notificationManager;

    public ConfluenceSpaceResource(@ComponentImport SpaceManager spaceManager, SpaceService spaceService, @ComponentImport PermissionManager permissionManager, FindRelevantSpacesService findRelevantSpacesService, @ComponentImport FavouriteManager favouriteManager, @ComponentImport NotificationManager notificationManager) {
        this.spaceManager = spaceManager;
        this.spaceService = spaceService;
        this.permissionManager = permissionManager;
        this.findRelevantSpacesService = findRelevantSpacesService;
        this.favouriteManager = favouriteManager;
        this.notificationManager = notificationManager;
    }

    @POST
    public Response createOnboardingSpace(SpaceBean bean) throws ImportExportException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || !this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, Space.class)) {
            LOGGER.error("User null or unauthorized, rejecting createOnboardingSpace request");
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        if (!Space.isValidGlobalSpaceKey((String)bean.getKey()) || StringUtils.isBlank((CharSequence)bean.getName())) {
            LOGGER.error("Invalid space key {} and name {}, rejecting createOnboardingSpace request", (Object)bean.getKey(), (Object)bean.getName());
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Valid space key and space name must be provided").build();
        }
        URL demoSiteZipUrl = this.spaceService.getOnboardingSpaceZipURL();
        if (demoSiteZipUrl == null) {
            return this.logAndResponse("Could not find demo site on the classpath", null);
        }
        String uniqueSpaceKey = this.spaceService.createUniqueSpaceKey(bean.getKey());
        if (StringUtils.isEmpty((CharSequence)uniqueSpaceKey)) {
            return this.logAndResponse("Failed to create unique space key with prefix " + bean.getKey(), null);
        }
        SpaceImportConfig importConfig = new SpaceImportConfig();
        importConfig.setActor(user);
        importConfig.setSpaceKey(uniqueSpaceKey);
        importConfig.setSpaceTitle(bean.getName());
        importConfig.setHomepageTitle(bean.getName());
        importConfig.setTemporary(bean.isTemporary());
        this.spaceService.importAndReindex(demoSiteZipUrl, importConfig);
        this.favouriteManager.addSpaceToFavourites((User)user, this.spaceManager.getSpace(uniqueSpaceKey));
        LOGGER.debug("Pre-generated space with key {}", (Object)uniqueSpaceKey);
        return Response.ok((Object)ImmutableMap.of((Object)"key", (Object)uniqueSpaceKey)).build();
    }

    @GET
    public Response getRelevantSpaces(@QueryParam(value="query") String query, @Context HttpServletRequest httpServletRequest) {
        return Response.ok(this.findRelevantSpacesService.getRelevantSpaces(query, httpServletRequest)).build();
    }

    @Path(value="relevant")
    @PUT
    public Response followRelevantSpaces(List<String> spaceKeys) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ArrayList successfullyFollowedSpaces = Lists.newArrayList();
        for (String key : spaceKeys) {
            Space space = this.spaceManager.getSpace(key);
            if (space == null) continue;
            this.favouriteManager.addSpaceToFavourites((User)user, space);
            this.notificationManager.addSpaceNotification((User)user, space);
            successfullyFollowedSpaces.add(key);
        }
        return Response.ok((Object)successfullyFollowedSpaces).build();
    }

    @Path(value="relevant")
    @GET
    public Response getRelevantSpaces(@Context HttpServletRequest httpServletRequest) {
        return Response.ok(this.findRelevantSpacesService.getRelevantSpaces(httpServletRequest)).build();
    }

    @GET
    @Path(value="generate")
    public Response generateSpaceKey(@QueryParam(value="key") String key) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        if (StringUtils.isEmpty((CharSequence)key)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        String generatedKey = this.spaceService.createUniqueSpaceKey(key);
        if (generatedKey == null) {
            return this.logAndResponse("Failed to generate unique space key with prefix " + key, null);
        }
        return Response.ok((Object)ImmutableMap.of((Object)"key", (Object)generatedKey)).build();
    }

    private Response logAndResponse(String errorMessage, Exception exception) {
        if (exception != null) {
            LOGGER.error(errorMessage, (Throwable)exception);
        } else {
            LOGGER.error(errorMessage);
        }
        return Response.serverError().entity((Object)ImmutableMap.of((Object)"error", (Object)errorMessage)).build();
    }
}

