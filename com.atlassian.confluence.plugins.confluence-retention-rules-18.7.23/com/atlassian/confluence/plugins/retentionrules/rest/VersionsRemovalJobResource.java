/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.SoftCleanupStatusService
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.retentionrules.rest;

import com.atlassian.confluence.api.service.retention.SoftCleanupStatusService;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/job")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
public class VersionsRemovalJobResource {
    private final Logger logger = LoggerFactory.getLogger(VersionsRemovalJobResource.class);
    private final SoftCleanupStatusService softCleanupStatusService;

    public VersionsRemovalJobResource(@ComponentImport SoftCleanupStatusService softCleanupStatusService) {
        this.softCleanupStatusService = softCleanupStatusService;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getJobStatus() {
        try {
            return Response.ok((Object)this.softCleanupStatusService.getCurrentStatus()).build();
        }
        catch (NotAuthorizedException ex) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        catch (Exception e) {
            this.logger.error("Failed to retrieve Versions removal job status.", (Throwable)e);
            return Response.serverError().build();
        }
    }
}

