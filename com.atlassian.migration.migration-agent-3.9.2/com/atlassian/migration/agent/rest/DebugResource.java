/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.sun.jersey.core.header.ContentDisposition
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.dto.UserDomainCountDto;
import com.atlassian.migration.agent.dto.UserDomainRuleDto;
import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import com.atlassian.migration.agent.mma.service.MigrationMetadataAggregatorService;
import com.atlassian.migration.agent.newexport.SpaceCSVExportTaskContext;
import com.atlassian.migration.agent.newexport.SpaceRapidExporter;
import com.atlassian.migration.agent.rest.MessageDto;
import com.atlassian.migration.agent.service.MigrationMappingService;
import com.atlassian.migration.agent.service.check.CheckResultCSVWriter;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.PreflightService;
import com.atlassian.migration.agent.service.check.csv.CheckResultCSVContainer;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.guardrails.InstanceAssessmentService;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationInitialExecutor;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import com.atlassian.migration.agent.service.status.MigrationStatusService;
import com.atlassian.migration.agent.service.status.PlanStatusDto;
import com.atlassian.migration.agent.service.status.StepStatusDto;
import com.atlassian.migration.agent.service.status.TaskStatusDto;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceExportExecutor;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.scheduler.SchedulerServiceException;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.spi.container.ResourceFilters;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
@Path(value="debug")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Produces(value={"application/json"})
public class DebugResource {
    private static final String TEXT_CSV = "text/csv";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String USER_DOMAIN_RULE_SELECTION_URI = "/admin/migration.action#choose-domains";
    private final PreflightService preflightService;
    private final UserGroupExtractFacade userGroupExtractFacade;
    private final MigrationStatusService migrationStatusService;
    private final SpaceRapidExporter spaceRapidExporter;
    private final SpaceManager spaceManager;
    private final BootstrapManager bootstrapManager;
    private final MigrationMappingService migrationMappingService;
    private final UserDomainService userDomainService;
    private final InstanceAssessmentService instanceAssessmentService;
    private final SpaceStatisticCalculationInitialExecutor spaceStatisticCalculationInitialExecutor;
    private final MigrationMetadataAggregatorService migrationMetadataAggregatorService;

    public DebugResource(PreflightService preflightService, UserGroupExtractFacade userGroupExtractFacade, MigrationStatusService migrationStatusService, SpaceRapidExporter spaceRapidExporter, SpaceManager spaceManager, BootstrapManager bootstrapManager, MigrationMappingService migrationMappingService, UserDomainService userDomainService, InstanceAssessmentService instanceAssessmentService, SpaceStatisticCalculationInitialExecutor spaceStatisticCalculationInitialExecutor, MigrationMetadataAggregatorService migrationMetadataAggregatorService) {
        this.preflightService = preflightService;
        this.userGroupExtractFacade = userGroupExtractFacade;
        this.migrationStatusService = migrationStatusService;
        this.spaceRapidExporter = spaceRapidExporter;
        this.spaceManager = spaceManager;
        this.bootstrapManager = bootstrapManager;
        this.migrationMappingService = migrationMappingService;
        this.userDomainService = userDomainService;
        this.instanceAssessmentService = instanceAssessmentService;
        this.spaceStatisticCalculationInitialExecutor = spaceStatisticCalculationInitialExecutor;
        this.migrationMetadataAggregatorService = migrationMetadataAggregatorService;
    }

    @POST
    @Path(value="/schedule/space-statistic-calculation")
    public Response scheduleSpaceStatisticCalculation() throws SchedulerServiceException {
        return this.spaceStatisticCalculationInitialExecutor.scheduleWithForceUpdate() ? Response.status((Response.Status)Response.Status.ACCEPTED).build() : Response.status((Response.Status)Response.Status.CONFLICT).build();
    }

    @POST
    @Path(value="/mma/emit-space-data")
    public Response emitSpaceData() {
        this.migrationMetadataAggregatorService.sendSpaceMetadataToMMAForAllCloudSites();
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }

    @GET
    @Path(value="/assess/l1")
    public Response assessInstance() {
        return Response.ok(this.instanceAssessmentService.executeAllQueries()).build();
    }

    @GET
    @Path(value="/assess/{queryId}")
    public Response assessQuery(@PathParam(value="queryId") String queryId) {
        return Response.ok((Object)this.instanceAssessmentService.executeQuery(queryId)).build();
    }

    @GET
    @Path(value="/attachments/{planId}")
    public Response debugAttachments(@PathParam(value="planId") String planId) {
        CheckResultCSVWriter writer = new CheckResultCSVWriter();
        CheckResultCSVContainer checkResultCSVContainer = this.preflightService.createCheckResultCSVContainer(planId, CheckType.MISSING_ATTACHMENTS);
        ContentDisposition contentDisposition = ContentDisposition.type((String)"attachment").fileName(planId + "-" + CheckType.MISSING_ATTACHMENTS.value() + ".csv").creationDate(new Date(Instant.now().toEpochMilli())).build();
        return Response.ok(output -> writer.writeResultsInStream(output, checkResultCSVContainer)).type(TEXT_CSV).header(CONTENT_DISPOSITION, (Object)contentDisposition).build();
    }

    @POST
    @Path(value="/export/space")
    public Response exportSpace(ExportSpacesRequest request) throws Exception {
        String cloudId = String.format("Debug space export dummy PlanId %s", UUID.randomUUID());
        String planId = String.format("Debug space export dummy PlanId %s", UUID.randomUUID());
        String taskId = String.format("Debug space export dummy TaskId %s", UUID.randomUUID());
        String spaceKey = request.getSpaceKey();
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"The specified space does not exist.").build();
        }
        SpaceCSVExportTaskContext config = new SpaceCSVExportTaskContext(space.getId(), space.getKey(), cloudId, planId, taskId, SpaceExportExecutor.getFilePathProperty(this.bootstrapManager), false);
        String exportPath = this.spaceRapidExporter.export(config);
        return Response.ok((Object)new ExportSpacesResponse(exportPath, spaceKey)).build();
    }

    @GET
    @Path(value="/extraction/cache/clear/{spaceKey}")
    public Response clearCacheBySpaceKey(@PathParam(value="spaceKey") String spaceKey) {
        this.userGroupExtractFacade.clearCache(spaceKey);
        return Response.ok().build();
    }

    @GET
    @Path(value="/status/plan")
    public Response getAllPlans() {
        List<PlanStatusDto> planStatusDtoList = this.migrationStatusService.getAllPlans();
        return Response.ok(planStatusDtoList).build();
    }

    @GET
    @Path(value="/status/plan/{planId}")
    public Response getTasksByPlanId(@PathParam(value="planId") String planId) {
        List<TaskStatusDto> tasks = this.migrationStatusService.getTasksByPlan(planId);
        return Response.ok(tasks).build();
    }

    @GET
    @Path(value="/status/plan/{planId}/{spaceKey}")
    public Response getDetailsByPlanAndSpaceKey(@PathParam(value="planId") String planId, @PathParam(value="spaceKey") String spaceKey) {
        Map<String, Object> migrationStatus = this.migrationStatusService.getDetailsByPlanAndSpaceKey(planId, spaceKey);
        return Response.ok(migrationStatus).build();
    }

    @GET
    @Path(value="/status/task/{taskId}")
    public Response getStepsByTaskId(@PathParam(value="taskId") String taskId) {
        List<StepStatusDto> tasks = this.migrationStatusService.getStepsByTask(taskId);
        return Response.ok(tasks).build();
    }

    @GET
    @Path(value="/mappings/{cloudId}/{migrationScopeId}/{namespace}")
    public Response getMappings(@PathParam(value="cloudId") String cloudId, @PathParam(value="migrationScopeId") String migrationScopeId, @PathParam(value="namespace") String namespace) {
        if (!MigrationMappingService.CONFLUENCE_MAPPINGS.contains((Object)namespace)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new MessageDto(namespace + " is not a valid confluence namespace")).build();
        }
        return Response.ok(this.migrationMappingService.getMappings(cloudId, migrationScopeId, namespace)).build();
    }

    @GET
    @Path(value="/email/trust-all-domains")
    public Response trustAllDomains() throws URISyntaxException {
        List<UserDomainCountDto> domains = this.userDomainService.getUserDomainCounts();
        domains.forEach(domain -> this.userDomainService.upsertDomainRule(new UserDomainRuleDto(domain.getDomainName(), DomainRuleBehaviour.TRUSTED)));
        return Response.status((Response.Status)Response.Status.TEMPORARY_REDIRECT).location(new URI(USER_DOMAIN_RULE_SELECTION_URI)).build();
    }

    @GET
    @Path(value="/email/remove-all-domain-rules")
    public Response deleteAllUserDomainRules() throws URISyntaxException {
        this.userDomainService.deleteAllDomainRules();
        return Response.status((Response.Status)Response.Status.TEMPORARY_REDIRECT).location(new URI(USER_DOMAIN_RULE_SELECTION_URI)).build();
    }

    public static final class ExportSpacesResponse {
        @JsonProperty
        private final String location;
        @JsonProperty
        private final String spaceKey;

        public ExportSpacesResponse(String location, String spaceKey) {
            this.location = location;
            this.spaceKey = spaceKey;
        }

        @Generated
        public String getLocation() {
            return this.location;
        }

        @Generated
        public String getSpaceKey() {
            return this.spaceKey;
        }
    }

    public static final class ExportSpacesRequest {
        @JsonProperty
        private final String spaceKey;

        @JsonCreator
        public ExportSpacesRequest(@JsonProperty(value="spaceKey") String spaceKey) {
            this.spaceKey = spaceKey;
        }

        @Generated
        public String getSpaceKey() {
            return this.spaceKey;
        }
    }
}

