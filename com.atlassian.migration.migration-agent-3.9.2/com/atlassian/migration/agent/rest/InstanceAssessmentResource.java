/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.StreamingOutput
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.guardrails.AssessmentJobProgressService;
import com.atlassian.migration.agent.service.guardrails.GuardrailsCsvOutputStreamResult;
import com.atlassian.migration.agent.service.guardrails.InstanceAssessmentCSVService;
import com.atlassian.migration.agent.service.guardrails.InstanceAssessmentService;
import com.atlassian.migration.agent.store.guardrails.AssessmentStatus;
import com.atlassian.migration.agent.store.guardrails.InstanceAssessmentStatus;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@Path(value="instance/assessment/job")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class InstanceAssessmentResource {
    private static final DateTimeFormatter ZIP_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final InstanceAssessmentService instanceAssessmentService;
    private final AssessmentJobProgressService assessmentJobProgressService;
    private final InstanceAssessmentCSVService instanceAssessmentCSVService;
    private static final Logger log = ContextLoggerFactory.getLogger(InstanceAssessmentResource.class);

    public InstanceAssessmentResource(InstanceAssessmentService instanceAssessmentService, AssessmentJobProgressService assessmentJobProgressService, InstanceAssessmentCSVService instanceAssessmentCSVService) {
        this.instanceAssessmentService = instanceAssessmentService;
        this.assessmentJobProgressService = assessmentJobProgressService;
        this.instanceAssessmentCSVService = instanceAssessmentCSVService;
    }

    @GET
    @Path(value="/schedule")
    public Response scheduleNewAssessment() {
        try {
            AssessmentStatus assessmentStatus = this.assessmentJobProgressService.scheduleInstanceAssessment();
            return Response.ok((Object)assessmentStatus).build();
        }
        catch (Exception e) {
            log.warn("Exception:", (Throwable)e);
            AssessmentStatus assessmentStatus = new AssessmentStatus(null, InstanceAssessmentStatus.FAILED, null);
            return Response.ok((Object)assessmentStatus).build();
        }
    }

    @GET
    @Produces(value={"application/zip"})
    @Path(value="/csv")
    public Response downloadCSV() {
        LocalDate today = LocalDate.now();
        String zipDate = ZIP_DATE_FORMATTER.format(today);
        String fileName = "migration-assessments-" + zipDate;
        StreamingOutput stream = output -> {
            GuardrailsCsvOutputStreamResult result = this.instanceAssessmentCSVService.generate(today, zipDate, output);
            if (result.getError() != null) {
                throw new WebApplicationException(result.getError());
            }
        };
        return Response.ok((Object)stream).type("application/zip").header("Content-disposition", (Object)String.format("attachment; filename=%s.zip", fileName)).build();
    }

    @GET
    @Path(value="")
    public Response getJobProgress() {
        AssessmentStatus assessmentStatus = this.assessmentJobProgressService.processJobProgress();
        return Response.ok((Object)assessmentStatus).build();
    }

    @GET
    @Path(value="/actives")
    public Response getActiveAssessment() {
        AssessmentStatus assessmentStatus = this.instanceAssessmentService.activeAssessment();
        return Response.ok((Object)assessmentStatus).build();
    }
}

