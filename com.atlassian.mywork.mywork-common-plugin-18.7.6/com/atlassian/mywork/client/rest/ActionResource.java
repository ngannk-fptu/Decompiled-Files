/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.JsonNode
 */
package com.atlassian.mywork.client.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.mywork.service.ActionServiceSelector;
import com.atlassian.sal.api.user.UserManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.JsonNode;

@Path(value="action")
@Produces(value={"application/json"})
public class ActionResource {
    private final UserManager userManager;
    private final ActionServiceSelector actionService;

    public ActionResource(UserManager userManager, ActionServiceSelector actionService) {
        this.userManager = userManager;
        this.actionService = actionService;
    }

    @POST
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response execute(@Context HttpServletRequest request, JsonNode action) {
        String username = this.userManager.getRemoteUsername(request);
        return Response.ok((Object)this.actionService.get(action.get("application").getTextValue()).execute(username, action)).build();
    }
}

