/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.mywork.host.service.LocalClientService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="client")
@Produces(value={"application/json"})
public class ClientResource {
    private static final Logger log = LoggerFactory.getLogger(ClientResource.class);
    private final LocalClientService clientService;
    private final UserManager userManager;

    public ClientResource(LocalClientService clientService, UserManager userManager) {
        this.clientService = clientService;
        this.userManager = userManager;
    }

    @POST
    @Consumes(value={"text/plain"})
    @AnonymousAllowed
    @XsrfProtectionExcluded
    public Response clientRegistration(String appId) {
        log.debug("Updating registration [{}]", (Object)appId);
        this.clientService.updatePotentialClient(appId);
        log.debug("Successfully updated registration [{}]", (Object)appId);
        return Response.noContent().build();
    }

    @POST
    @Path(value="pong")
    @XsrfProtectionExcluded
    public Response clientPong(@Context HttpServletRequest request, @QueryParam(value="appId") String appId) {
        this.clientService.clientPong(this.userManager.getRemoteUsername(request), appId);
        return Response.noContent().build();
    }
}

