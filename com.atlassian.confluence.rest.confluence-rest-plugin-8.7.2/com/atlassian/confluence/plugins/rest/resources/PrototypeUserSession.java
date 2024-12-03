/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.plugins.rest.manager.RestUserSessionManager;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.plugins.rest.resources.PrototypeSpaceResource;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/session")
public class PrototypeUserSession
extends AbstractResource {
    private final RestUserSessionManager restUserSessionManager;

    private PrototypeUserSession() {
        this.restUserSessionManager = null;
    }

    public PrototypeUserSession(UserAccessor userAccessor, RestUserSessionManager restUserSessionManager, SpacePermissionManager spacePermissionManager) {
        super(userAccessor, spacePermissionManager);
        this.restUserSessionManager = restUserSessionManager;
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response getSession() {
        this.createRequestContext();
        return Response.ok((Object)this.restUserSessionManager.getUserSession()).build();
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/history")
    public Response getUserHistory(@QueryParam(value="start-index") String startIndexString, @QueryParam(value="max-results") String maxResultsString) {
        this.createRequestContext();
        Integer startIndex = PrototypeSpaceResource.parseInt(startIndexString);
        Integer maxResults = PrototypeSpaceResource.parseInt(maxResultsString);
        return Response.ok((Object)this.restUserSessionManager.getUserHistory(startIndex, maxResults)).build();
    }

    @GET
    @AnonymousAllowed
    @Produces(value={"application/json"})
    @Path(value="/check/{username}")
    public Response check(@PathParam(value="username") String username) {
        String expectedUsername;
        User currentUser = this.getCurrentUser();
        String string = expectedUsername = currentUser != null ? currentUser.getName() : "";
        if (!expectedUsername.equals(username)) {
            String message = String.format("Expected user >%s<", expectedUsername);
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)message).build();
        }
        return Response.ok().build();
    }
}

