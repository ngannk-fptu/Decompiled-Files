/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.actions.AuthenticationHelper
 *  com.atlassian.event.EventManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.user.actions.AuthenticationHelper;
import com.atlassian.event.EventManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.config.SecurityConfigFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Logout API", description="Logout user resource")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/session")
@Component
public class LogoutResource {
    private final Logger logger = LoggerFactory.getLogger(LogoutResource.class);
    private final EventManager eventManager;

    @Autowired
    public LogoutResource(@ComponentImport EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Operation(summary="Logout", description="Logs out current user", responses={@ApiResponse(responseCode="204", description="Successfully logged out"), @ApiResponse(responseCode="401", description="User unauthorized")})
    @DELETE
    @Path(value="/logout")
    public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        Authenticator authenticator = SecurityConfigFactory.getInstance().getAuthenticator();
        Principal user = authenticator.getUser(request);
        boolean isLoggedOut = AuthenticationHelper.logout((Principal)user, (HttpServletRequest)request, (HttpServletResponse)response, (EventManager)this.eventManager, (Object)this);
        if (!isLoggedOut) {
            this.logger.error("Couldn't logged out the user {}", (Object)user);
        } else {
            this.logger.info("The user {} has been logged out.", (Object)user);
        }
        return Response.noContent().build();
    }
}

