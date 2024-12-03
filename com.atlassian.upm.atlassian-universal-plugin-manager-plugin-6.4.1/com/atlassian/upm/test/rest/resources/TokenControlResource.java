/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.rest.resources.UpmResources;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.token.TokenManager;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/tokens")
public class TokenControlResource {
    private static final Logger log = LoggerFactory.getLogger(TokenControlResource.class);
    private final PermissionEnforcer permissionEnforcer;
    private final TokenManager tokenManager;
    private final UserManager userManager;
    private final UpmRepresentationFactory representationFactory;

    public TokenControlResource(PermissionEnforcer permissionEnforcer, TokenManager tokenManager, UserManager userManager, UpmRepresentationFactory representationFactory) {
        this.permissionEnforcer = permissionEnforcer;
        this.tokenManager = tokenManager;
        this.userManager = userManager;
        this.representationFactory = representationFactory;
    }

    @Path(value="/consume")
    @POST
    @XsrfProtectionExcluded
    public Response consumeToken(@QueryParam(value="token") String token) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        UpmResources.validateToken(token, this.userManager.getRemoteUserKey(), "text/html", this.tokenManager, this.representationFactory);
        return Response.ok().build();
    }

    @Path(value="/override")
    @PUT
    public Response setOverride(@QueryParam(value="disable") boolean disable) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        log.warn("Setting Sys.UPM_XSRF_TOKEN_DISABLE to " + disable);
        System.setProperty("upm.xsrf.token.disable", Boolean.toString(disable));
        return Response.ok().build();
    }
}

