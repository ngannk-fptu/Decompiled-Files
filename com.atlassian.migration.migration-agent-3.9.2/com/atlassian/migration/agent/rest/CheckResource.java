/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.ChecksResultDto
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.core.header.ContentDisposition
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.ChecksResultDto;
import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.logging.LoggingContextBuilder;
import com.atlassian.migration.agent.rest.MessageDto;
import com.atlassian.migration.agent.service.check.CheckResultCSVWriter;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.PreflightService;
import com.atlassian.migration.agent.service.check.csv.AppVendorCheckCSVContainer;
import com.atlassian.migration.agent.service.check.csv.CheckResultCSVContainer;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.spi.container.ResourceFilters;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@Path(value="check")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class CheckResource {
    private static final Logger log = ContextLoggerFactory.getLogger(CheckResource.class);
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_HH-mm-ss");
    private static final String TEXT_CSV = "text/csv";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private final PreflightService preflightService;
    private final CheckResultsService checkResultService;

    public CheckResource(PreflightService preflightService, CheckResultsService checkResultService) {
        this.preflightService = preflightService;
        this.checkResultService = checkResultService;
    }

    @GET
    @Path(value="/{executionId}/status")
    public Response getCheckExecutionStatus(@PathParam(value="executionId") String executionId) {
        return LoggingContextBuilder.logCtx().withCheckExecutionId(executionId).execute(() -> {
            List<CheckResultDto> results = this.preflightService.getCheckExecutionStatus(executionId);
            if (results.isEmpty()) {
                log.warn("Check execution not found.");
                return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)new MessageDto(String.format("Check execution with id = %s not found", executionId))).build();
            }
            return Response.ok((Object)new ChecksResultDto(results)).build();
        });
    }

    @GET
    @Path(value="/{executionId}/status/{checkType}")
    public Response getCheckExecutionStatus(@PathParam(value="executionId") String executionId, @PathParam(value="checkType") CheckType checkType) {
        return LoggingContextBuilder.logCtx().withCheckExecutionId(executionId).execute(() -> {
            List<CheckResultDto> results = this.preflightService.getCheckExecutionStatus(executionId, checkType);
            if (results.isEmpty()) {
                log.warn("Check execution not found.");
                return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)new MessageDto(String.format("Check execution with id = %s not found", executionId))).build();
            }
            return Response.ok((Object)new ChecksResultDto(results)).build();
        });
    }

    @POST
    @Path(value="/{executionId}")
    public void executeChecks(@PathParam(value="executionId") String executionId, @QueryParam(value="type") Set<CheckType> types, PlanDto planDto) {
        LoggingContextBuilder.logCtx().withCheckExecutionId(executionId).execute(() -> this.preflightService.executeChecks(executionId, planDto, types));
    }

    @POST
    @Path(value="/{executionId}/{checkType}")
    public void executeChecksForApp(@PathParam(value="executionId") String executionId, @PathParam(value="checkType") CheckType checkType, @QueryParam(value="appKey") @Nonnull String serverAppKey, PlanDto planDto) {
        LoggingContextBuilder.logCtx().withCheckExecutionId(executionId).execute(() -> this.preflightService.executeChecksForApp(executionId, planDto, checkType, serverAppKey));
    }

    @DELETE
    @Path(value="/{executionId}")
    public void deleteCheckExecutionResults(@PathParam(value="executionId") String executionId) {
        LoggingContextBuilder.logCtx().withCheckExecutionId(executionId).execute(() -> {
            this.checkResultService.deleteCheckResultsByExecutionId(executionId);
            log.info("Check execution is deleted.");
        });
    }

    @GET
    @Path(value="/{executionId}/{type}/csv")
    public Response generateCSV(@PathParam(value="executionId") String executionId, @PathParam(value="type") CheckType type, @QueryParam(value="planName") String planName) {
        if (!CheckType.checkTypesForCSV.contains(type)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto(String.format("%s type is only allowed.", type.value()))).build();
        }
        CheckResultCSVWriter writer = new CheckResultCSVWriter();
        CheckResultCSVContainer checkResultCSVContainer = this.preflightService.createCheckResultCSVContainer(executionId, type);
        String name = StringUtils.isNotEmpty((CharSequence)planName) ? planName : executionId;
        ContentDisposition contentDisposition = ContentDisposition.type((String)"attachment").fileName(this.generateFilename(name, type)).creationDate(new Date(Instant.now().toEpochMilli())).build();
        return Response.ok(output -> writer.writeResultsInStream(output, checkResultCSVContainer)).type(TEXT_CSV).header(CONTENT_DISPOSITION, (Object)contentDisposition).build();
    }

    @GET
    @Path(value="/{executionId}/AppVendorCheck/{appKey}/{checkId}/csv")
    public Response generateAppVendorCheckCSV(@PathParam(value="executionId") String executionId, @PathParam(value="appKey") String appKey, @PathParam(value="checkId") String checkId) {
        CheckResultCSVWriter writer = new CheckResultCSVWriter();
        Optional<AppVendorCheckCSVContainer> checkResultCSVContainer = this.preflightService.createAppVendorCheckResultCSVContainer(executionId, appKey, checkId);
        if (checkResultCSVContainer.isPresent()) {
            ContentDisposition contentDisposition = ContentDisposition.type((String)"attachment").fileName(this.generateAppVendorCSVFilename(executionId, appKey, checkId)).creationDate(new Date(Instant.now().toEpochMilli())).build();
            return Response.ok(output -> writer.writeListResultsInStream(output, ((AppVendorCheckCSVContainer)checkResultCSVContainer.get()).headers(), ((AppVendorCheckCSVContainer)checkResultCSVContainer.get()).beans())).type(TEXT_CSV).header(CONTENT_DISPOSITION, (Object)contentDisposition).build();
        }
        log.warn("No Csv content for executionId: {} appKey: {} checkId: {} is empty", new Object[]{executionId, appKey, checkId});
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)new MessageDto(String.format("Csv content not found for executionId=%s appKey=%s checkId=%s", executionId, appKey, checkId))).build();
    }

    private String generateFilename(String name, CheckType checkType) {
        String fileNameSubstring = this.getFileNameSubstring(checkType);
        return name + fileNameSubstring + checkType.value() + "_Confluence_" + this.dateFormat.format(new Date(Instant.now().toEpochMilli())) + ".csv";
    }

    @NotNull
    private String getFileNameSubstring(CheckType checkType) {
        String fileNameSubstring = "_Pre-migration checks_Spaces_";
        if (checkType.equals(CheckType.GLOBAL_DATA_TEMPLATE)) {
            fileNameSubstring = "_Pre-migration checks_Global Templates_";
        }
        return fileNameSubstring;
    }

    private String generateAppVendorCSVFilename(String executionId, String appKey, String checkId) {
        return executionId + "-AppVendorCheck-" + appKey + "-" + checkId + ".csv";
    }
}

