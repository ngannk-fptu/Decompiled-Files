/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.user.extras.rest;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.extras.builders.GroupEntityBuilder;
import com.atlassian.user.User;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/groups")
@Produces(value={"application/json", "application/xml"})
public class GroupsResource {
    private final GroupEntityBuilder groupEntityBuilder;
    private final PermissionManager permissionManager;
    private static final String DEFAULT_MAX_RESULTS_QUERY = "50";

    public GroupsResource(GroupEntityBuilder groupEntityBuilder, PermissionManager permissionManager) {
        this.groupEntityBuilder = groupEntityBuilder;
        this.permissionManager = permissionManager;
    }

    @GET
    public Response getGroups(@QueryParam(value="startIndex") int startIndex, @DefaultValue(value="50") @QueryParam(value="maxResults") int maxResults) {
        if (this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            return Response.ok((Object)this.groupEntityBuilder.getGroups(startIndex, maxResults)).build();
        }
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
    }
}

