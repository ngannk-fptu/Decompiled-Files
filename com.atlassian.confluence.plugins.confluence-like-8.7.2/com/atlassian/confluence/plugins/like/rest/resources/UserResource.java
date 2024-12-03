/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.follow.FollowManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.like.rest.resources;

import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/user/{username}")
public class UserResource {
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final FollowManager followManager;

    public UserResource(UserAccessor userAccessor, PermissionManager permissionManager, FollowManager followManager) {
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.followManager = followManager;
    }

    @PUT
    @Path(value="/following")
    @Consumes(value={"application/json"})
    public Response addToFollowing(@QueryParam(value="username") String candidateUsername, @PathParam(value="username") String currentUsername) {
        if (!currentUsername.equals(AuthenticatedUserThreadLocal.getUsername())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"You can only alter your own network").build();
        }
        ConfluenceUser user = this.userAccessor.getUserByName(currentUsername);
        if (user == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("User with name \"" + currentUsername + "\" does not exist")).build();
        }
        ConfluenceUser candidate = this.userAccessor.getUserByName(candidateUsername);
        if (candidate == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("User with name \"" + candidateUsername + "\" does not exist")).build();
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"You do not have sufficient privileges.").build();
        }
        this.followManager.followUser(user, candidate);
        return Response.ok().build();
    }
}

