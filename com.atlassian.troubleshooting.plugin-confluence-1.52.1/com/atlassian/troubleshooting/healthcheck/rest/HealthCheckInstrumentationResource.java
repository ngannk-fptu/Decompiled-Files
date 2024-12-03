/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.troubleshooting.healthcheck.rest;

import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/instrument")
@Deprecated
public class HealthCheckInstrumentationResource {
    @VisibleForTesting
    public static final String NAME = "Test healthcheck";
    @VisibleForTesting
    public static final String DESCRIPTION = "This is a test healthcheck for testing";
    private final HealthStatusPersistenceService statusPersistenceService;

    public HealthCheckInstrumentationResource(HealthStatusPersistenceService statusPersistenceService) {
        this.statusPersistenceService = statusPersistenceService;
    }

    @Path(value="/createHealthStatus")
    @POST
    public Response createHealthStatus(@QueryParam(value="isHealthy") boolean isHealthy, @QueryParam(value="severity") SupportHealthStatus.Severity severity) {
        if (Boolean.parseBoolean(System.getProperty("troubleshooting.dev.mode"))) {
            HealthCheckStatus status = HealthCheckStatus.builder().name(NAME).completeKey("com.atlassian.jira.plugins.jira-healthcheck-plugin:testHealthCheck").description(DESCRIPTION).isHealthy(isHealthy).failureReason("Some texts here...").application("JIRA").time(System.currentTimeMillis()).severity(severity).documentation("http://www.atlassian.com").tag("Test").build();
            ArrayList statuses = Lists.newArrayList();
            statuses.add(status);
            this.statusPersistenceService.storeFailedStatuses(statuses);
            return Response.ok().build();
        }
        return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
    }
}

