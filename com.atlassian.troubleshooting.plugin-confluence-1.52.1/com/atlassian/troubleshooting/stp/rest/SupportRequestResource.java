/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.troubleshooting.stp.request.SupportRequestService;
import com.atlassian.troubleshooting.stp.rest.CacheControlUtils;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="support-request")
@Produces(value={"application/json"})
@Singleton
public class SupportRequestResource {
    private final SupportRequestService supportRequestService;

    @Autowired
    public SupportRequestResource(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    @GET
    @Path(value="{id}")
    public Response getSupportZipDetails(@PathParam(value="id") String id) {
        TaskMonitor monitor = this.supportRequestService.getMonitor(id);
        if (monitor == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(CacheControlUtils.NO_CACHE).build();
        }
        Response.ResponseBuilder builder = Response.ok().entity(monitor.getAttributes());
        if (!monitor.isDone()) {
            builder.cacheControl(CacheControlUtils.NO_CACHE);
        }
        return builder.build();
    }
}

