/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserProfile
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.ratelimiting.rest.resource;

import com.atlassian.ratelimiting.rest.api.RestApplicationUser;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserProfile;
import com.sun.jersey.spi.resource.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="admin/rate-limit/users")
@Consumes(value={"application/json"})
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class UserResource {
    private final UserService userService;
    private final PermissionEnforcer permissionEnforcer;

    public UserResource(UserService userService, PermissionEnforcer permissionEnforcer) {
        this.userService = userService;
        this.permissionEnforcer = permissionEnforcer;
    }

    @GET
    @Path(value="/picker")
    public Response getUsersForUserPicker(@QueryParam(value="filter") String filter, @DefaultValue(value="0") @QueryParam(value="offset") int offset, @DefaultValue(value="10") @QueryParam(value="maxResults") int maxResults) {
        this.permissionEnforcer.enforceAdmin();
        List<UserProfile> searchResults = this.userService.searchUsersForUserPicker(filter, offset, maxResults);
        List mappedSearchResults = searchResults.stream().map(RestApplicationUser::new).collect(Collectors.toList());
        return Response.ok(mappedSearchResults).build();
    }
}

