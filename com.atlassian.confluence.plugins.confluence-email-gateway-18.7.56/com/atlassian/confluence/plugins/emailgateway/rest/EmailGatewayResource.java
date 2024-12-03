/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.rest;

import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.plugins.emailgateway.service.BulkEmailProcessingService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.collect.Maps;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/")
public class EmailGatewayResource {
    private static final Logger log = LoggerFactory.getLogger(EmailGatewayResource.class);
    private final BulkEmailProcessingService emailThreadConverterProcessingService;
    private final PermissionManager permissionManager;
    private final EmailGatewaySettingsManager emailGatewaySettingsManager;

    public EmailGatewayResource(BulkEmailProcessingService emailThreadConverterProcessingService, PermissionManager permissionManager, EmailGatewaySettingsManager emailGatewaySettingsManager) {
        this.emailThreadConverterProcessingService = emailThreadConverterProcessingService;
        this.permissionManager = permissionManager;
        this.emailGatewaySettingsManager = emailGatewaySettingsManager;
    }

    @POST
    @Produces(value={"application/json"})
    @Path(value="runEmailHandlers")
    @Consumes(value={"application/json"})
    public Response runEmailConverter() {
        if (!this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        log.info("Processing inbound emails");
        int numProcessed = this.emailThreadConverterProcessingService.processInboundEmail();
        HashMap result = Maps.newHashMap();
        result.put("numProcessed", numProcessed);
        return Response.ok((Object)result).build();
    }

    @POST
    @Consumes(value={"application/json"})
    @Path(value="enable-feature/{feature:reply|create}")
    public Response enableFeature(@PathParam(value="feature") String feature) {
        if (!this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.setFeature(feature, true);
        return Response.noContent().build();
    }

    @POST
    @Consumes(value={"application/json"})
    @Path(value="disable-feature/{feature:reply|create}")
    public Response disableFeature(@PathParam(value="feature") String feature) {
        if (!this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.setFeature(feature, false);
        return Response.noContent().build();
    }

    private void setFeature(String feature, boolean state) {
        switch (feature) {
            case "reply": {
                this.emailGatewaySettingsManager.setAllowToCreateCommentByEmail(state);
                break;
            }
            default: {
                this.emailGatewaySettingsManager.setAllowToCreatePageByEmail(state);
            }
        }
    }
}

