/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.plugins.rest.entities.builders.UserEntityBuilder;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/user")
@AnonymousAllowed
public class PrototypeUserResource
extends AbstractResource {
    private UserAccessor userAccessor;
    private UserEntityBuilder userEntityBuilder;
    private PermissionManager permissionManager;

    private PrototypeUserResource() {
    }

    public PrototypeUserResource(UserAccessor userAccessor, PermissionManager permissionManager, WebResourceUrlProvider webResourceUrlProvider, SettingsManager settingsManager, I18NBeanFactory i18NBeanFactory, SpacePermissionManager spm) {
        super(userAccessor, spm);
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.userEntityBuilder = new UserEntityBuilder(userAccessor, settingsManager, webResourceUrlProvider, i18NBeanFactory);
    }

    @Path(value="system/anonymous")
    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response getAnonymousUser() {
        this.createRequestContext();
        return Response.ok((Object)this.userEntityBuilder.buildAnonymous()).build();
    }

    @Path(value="current")
    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response getLoggedInUser() {
        this.createRequestContext();
        if (this.getCurrentUser() == null) {
            return Response.seeOther((URI)this.getAnonymousUserUriBuilder().build(new Object[0])).build();
        }
        return Response.seeOther((URI)this.getUserUriBuilder().build(new Object[]{this.getCurrentUser().getName()})).build();
    }

    @Path(value="non-system/{username}")
    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response getUser(@PathParam(value="username") String username) {
        this.createRequestContext();
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)user)) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("User " + HtmlUtil.htmlEncode((String)username) + " does not exist, or you do not have permission to view.")).build();
        }
        return Response.ok((Object)this.userEntityBuilder.build((User)user)).build();
    }
}

