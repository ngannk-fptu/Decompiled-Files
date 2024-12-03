/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="loggedInUser")
@Produces(value={"application/json"})
public class LoggedInUserResource {
    private final UserManager userManager;

    public LoggedInUserResource(UserManager userManager) {
        this.userManager = userManager;
    }

    @GET
    public Response getUsername(@Context HttpServletRequest request) {
        String username = this.userManager.getRemoteUsername(request);
        if (username == null) {
            username = "";
        }
        return Response.ok((Object)ImmutableMap.of((Object)"username", (Object)username)).build();
    }
}

