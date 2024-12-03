/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.index.status.ReIndexJob
 *  com.atlassian.confluence.index.status.ReIndexJobManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.rebuildindex.rest;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexJobManager;
import com.atlassian.confluence.plugins.rebuildindex.status.ReIndexJobJson;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/job")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
public class ReIndexJobResource {
    private static final String ERROR_KEY = "error";
    private static final String STATUS_KEY = "status";
    private static final String ERROR_JOB_NOT_FOUND = "no current re-index jobs";
    private static final String ERROR_FAIL_TO_ACK = "failed to acknowledge current job";
    private static final String STATUS_ACKNOWLEDGED = "acknowledged";
    private final ReIndexJobManager reIndexJobManager;

    public ReIndexJobResource(@ComponentImport ReIndexJobManager reIndexJobManager) {
        this.reIndexJobManager = Objects.requireNonNull(reIndexJobManager);
    }

    @GET
    @Produces(value={"application/json"})
    public Response getMostRecentOrRunning() {
        return this.reIndexJobManager.getRunningOrMostRecentReIndex().map(currentJob -> Response.ok((Object)new ReIndexJobJson((ReIndexJob)currentJob)).build()).orElse(Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)ImmutableMap.of((Object)ERROR_KEY, (Object)ERROR_JOB_NOT_FOUND)).build());
    }

    @POST
    @Path(value="/ack")
    @Produces(value={"application/json"})
    @ReadOnlyAccessAllowed
    public Response acknowledgeRunningJob() throws InterruptedException {
        Optional runningJob = this.reIndexJobManager.getRunningOrMostRecentReIndex();
        if (runningJob.isEmpty()) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)ImmutableMap.of((Object)ERROR_KEY, (Object)ERROR_JOB_NOT_FOUND)).build();
        }
        if (this.reIndexJobManager.acknowledgeRunningJob()) {
            return Response.ok((Object)ImmutableMap.of((Object)STATUS_KEY, (Object)STATUS_ACKNOWLEDGED)).build();
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)ImmutableMap.of((Object)ERROR_KEY, (Object)ERROR_FAIL_TO_ACK)).build();
    }
}

