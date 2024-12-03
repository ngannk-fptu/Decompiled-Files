/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.analytics.client.configuration;

import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.configuration.AnalyticsConfigEntity;
import com.atlassian.analytics.client.configuration.entities.AnalyticsEnabledEntity;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/config")
@Produces(value={"application/json;charset=UTF-8"})
public class AnalyticsConfigResource {
    private final AnalyticsConfig analyticsConfig;
    private final UserPermissionsHelper userPermissionsHelper;
    private final PeriodicEventUploaderScheduler periodicEventUploaderScheduler;

    public AnalyticsConfigResource(AnalyticsConfig analyticsConfig, UserPermissionsHelper userPermissionsHelper, PeriodicEventUploaderScheduler periodicEventUploaderScheduler) {
        this.analyticsConfig = analyticsConfig;
        this.userPermissionsHelper = userPermissionsHelper;
        this.periodicEventUploaderScheduler = periodicEventUploaderScheduler;
    }

    @GET
    public Response getAnalyticsDestination() {
        return Response.ok((Object)new AnalyticsConfigEntity(this.analyticsConfig.getDestination())).build();
    }

    @Path(value="/destination")
    @PUT
    public Response setAnalyticsDestination(@Context HttpServletRequest request, @FormParam(value="destination") String destination) {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.analyticsConfig.setDestination(destination);
        return Response.ok((Object)new AnalyticsConfigEntity(this.analyticsConfig.getDestination())).build();
    }

    @Path(value="/enable")
    @PUT
    public Response setAnalyticsEnabled(@Context HttpServletRequest request, AnalyticsEnabledEntity in) {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.analyticsConfig.setAnalyticsEnabled(in.isAnalyticsEnabled(), request.getRemoteUser());
        return Response.ok().build();
    }

    @Path(value="/acknowledge")
    @PUT
    public Response setPolicyAcknowledged(@Context HttpServletRequest request) {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.analyticsConfig.setPolicyUpdateAcknowledged(true);
        this.periodicEventUploaderScheduler.scheduleInitialRemoteRead();
        return Response.ok().build();
    }

    @Path(value="/upload")
    @GET
    public Response triggerUploadScheduledJob(@Context HttpServletRequest request) {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.periodicEventUploaderScheduler.runUploadJobImmediately();
        return Response.ok().build();
    }
}

