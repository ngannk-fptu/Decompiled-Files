/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.plugins.rest.common.security.AuthenticationRequiredException
 *  com.atlassian.user.User
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriBuilder
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.plugins.rest.manager.RequestContext;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.user.User;
import java.security.Principal;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public abstract class AbstractResource {
    protected final UserAccessor userAccessor;
    @Context
    protected AuthenticationContext authContext;
    @Context
    protected UriInfo uriInfo;
    private SpacePermissionManager spacePermissionManager;

    AbstractResource() {
        this.userAccessor = null;
    }

    public AbstractResource(UserAccessor userAccessor, SpacePermissionManager spm) {
        this.userAccessor = userAccessor;
        this.spacePermissionManager = spm;
    }

    protected UriBuilder getContentUriBuilder() {
        return this.uriInfo.getBaseUriBuilder().path("content").path("{id}");
    }

    protected UriBuilder getSpaceUriBuilder() {
        return this.uriInfo.getBaseUriBuilder().path("space").path("{key}");
    }

    protected UriBuilder getAttachmentUriBuilder() {
        return this.uriInfo.getBaseUriBuilder().path("attachment").path("{id}");
    }

    protected UriBuilder getUserUriBuilder() {
        return this.uriInfo.getBaseUriBuilder().path("user").path("non-system").path("{username}");
    }

    protected UriBuilder getAnonymousUserUriBuilder() {
        return this.uriInfo.getBaseUriBuilder().path("user").path("system").path("anonymous");
    }

    protected UriBuilder getMissingUserUriBuilder() {
        return this.uriInfo.getBaseUriBuilder().path("user").path("missing").queryParam("username", new Object[0]);
    }

    protected User getCurrentUser() {
        return this.convertPrincipalToUser(this.authContext.getPrincipal());
    }

    protected RequestContext createRequestContext() {
        if (!this.authContext.isAuthenticated() && !this.spacePermissionManager.hasPermission("USECONFLUENCE", null, null)) {
            throw new AuthenticationRequiredException();
        }
        RequestContext requestContext = new RequestContext(this.getCurrentUser(), this.uriInfo.getBaseUriBuilder());
        requestContext.setUriBuilder("content", this.getContentUriBuilder());
        requestContext.setUriBuilder("space", this.getSpaceUriBuilder());
        requestContext.setUriBuilder("attachment", this.getAttachmentUriBuilder());
        requestContext.setUriBuilder("user/non-system", this.getUserUriBuilder());
        requestContext.setUriBuilder("user/system/anonymous", this.getAnonymousUserUriBuilder());
        requestContext.setUriBuilder("user/missing", this.getMissingUserUriBuilder());
        RequestContextThreadLocal.set(requestContext);
        return requestContext;
    }

    private User convertPrincipalToUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        if (principal instanceof User) {
            return (User)principal;
        }
        return this.userAccessor.getUserByName(principal.getName());
    }

    static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

