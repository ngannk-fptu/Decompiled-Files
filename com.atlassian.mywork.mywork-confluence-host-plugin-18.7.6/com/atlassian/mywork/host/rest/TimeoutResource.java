/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.mywork.service.TimeoutService
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.mywork.service.TimeoutService;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="timeout")
@Produces(value={"application/json"})
public class TimeoutResource {
    private static final String TIMEOUT = "timeout";
    private static final String MAX_TIMEOUT = "max_timeout";
    private final TimeoutService timeoutService;
    private final UserManager userManager;

    public TimeoutResource(TimeoutService timeoutService, UserManager userManager) {
        this.timeoutService = timeoutService;
        this.userManager = userManager;
    }

    @GET
    public Response get() {
        return Response.ok((Object)ImmutableMap.of((Object)TIMEOUT, (Object)this.timeoutService.getTimeout(), (Object)MAX_TIMEOUT, (Object)this.timeoutService.getMaxTimeout())).build();
    }

    @PUT
    @XsrfProtectionExcluded
    public Response update(@Context HttpServletRequest request) {
        String maxTimeout;
        if (!this.userManager.isAdmin(this.userManager.getRemoteUsername(request))) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        String timeout = request.getParameter(TIMEOUT);
        if (timeout != null) {
            this.timeoutService.setTimeout(Integer.parseInt(timeout));
        }
        if ((maxTimeout = request.getParameter(MAX_TIMEOUT)) != null) {
            this.timeoutService.setMaxTimeout(Integer.parseInt(maxTimeout));
        }
        return this.get();
    }
}

