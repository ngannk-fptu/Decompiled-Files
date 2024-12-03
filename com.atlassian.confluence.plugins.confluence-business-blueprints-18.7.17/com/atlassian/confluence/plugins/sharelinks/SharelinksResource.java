/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.whitelist.NotAuthorizedException
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.sharelinks;

import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.sharelinks.LinkMetaData;
import com.atlassian.confluence.plugins.sharelinks.LinkMetaDataExtractor;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.whitelist.NotAuthorizedException;
import com.atlassian.user.User;
import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/")
public class SharelinksResource {
    private final LinkMetaDataExtractor linkMetaDataExtractor;
    private final PermissionManager permissionManager;
    private static final Response.Status BAD_REQUEST = Response.Status.BAD_REQUEST;
    private final SpaceManager spaceManager;

    public SharelinksResource(LinkMetaDataExtractor linkMetaDataExtractor, PermissionManager permissionManager, SpaceManager spaceManager) {
        this.linkMetaDataExtractor = linkMetaDataExtractor;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
    }

    @GET
    @Path(value="link")
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response getLinkMetaData(@QueryParam(value="url") String url) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"You are not authorized to access this resource").build();
        }
        try {
            LinkMetaData link = this.linkMetaDataExtractor.parseMetaData(url, true);
            return Response.ok((Object)link).build();
        }
        catch (URISyntaxException e) {
            return Response.status((Response.Status)BAD_REQUEST).entity((Object)"The provided URL is invalid").build();
        }
        catch (NotAuthorizedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)("Not authorized to access " + url + ". Please contact admin to whitelist it")).build();
        }
    }

    @GET
    @Path(value="can-create-comment")
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response canCreateComment(@QueryParam(value="spaceKey") String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"No space found for spacekey").build();
        }
        boolean canCreate = this.permissionManager.hasCreatePermission(this.getUser(), (Object)space, Comment.class);
        return Response.ok((Object)canCreate).build();
    }

    @GET
    @Path(value="external-links-enabled")
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response externalLinksEnabled() {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"You are not authorized to access this resource").build();
        }
        return Response.ok((Object)GeneralUtil.getGlobalSettings().getConfluenceHttpParameters().isEnabled()).build();
    }

    private User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

