/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  com.atlassian.business.insights.api.dataset.DatasetProvider
 *  com.atlassian.business.insights.api.schema.SchemaStatus
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.status.JobDetails
 *  io.swagger.v3.oas.annotations.OpenAPIDefinition
 *  io.swagger.v3.oas.annotations.Operation
 *  io.swagger.v3.oas.annotations.info.Info
 *  io.swagger.v3.oas.annotations.media.ArraySchema
 *  io.swagger.v3.oas.annotations.media.Content
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.parameters.RequestBody
 *  io.swagger.v3.oas.annotations.responses.ApiResponse
 *  io.swagger.v3.oas.annotations.responses.ApiResponses
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.OrderComparator
 *  org.springframework.stereotype.Component
 */
package com.atlassian.business.insights.core.rest;

import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.api.dataset.DatasetProvider;
import com.atlassian.business.insights.api.schema.SchemaStatus;
import com.atlassian.business.insights.core.analytics.path.CustomExportPathDeleteTriggeredAnalyticEvent;
import com.atlassian.business.insights.core.analytics.path.CustomExportPathGetRequestedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.path.CustomExportPathUpdateFailedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.path.CustomExportPathUpdateTriggeredAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleDeleteTriggeredAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleDeletedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleGetRequestedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleUpdateAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schedule.ExportScheduleUpdateTriggeredAnalyticEvent;
import com.atlassian.business.insights.core.analytics.schema.SupportedSchemaVersionsGetRequestedAnalyticEvent;
import com.atlassian.business.insights.core.rest.exception.InvalidRequestBodyException;
import com.atlassian.business.insights.core.rest.model.ConfigExportPathRequest;
import com.atlassian.business.insights.core.rest.model.ConfigExportPathResponse;
import com.atlassian.business.insights.core.rest.model.ConfigExportScheduleRequest;
import com.atlassian.business.insights.core.rest.model.ConfigExportScheduleResponse;
import com.atlassian.business.insights.core.rest.model.ErrorStatusResponse;
import com.atlassian.business.insights.core.rest.model.SchemaResponse;
import com.atlassian.business.insights.core.rest.validation.ValidateLicenseIsDc;
import com.atlassian.business.insights.core.rest.validation.ValidateRequestBody;
import com.atlassian.business.insights.core.rest.validation.ValidateUserIsAuthedAsSysAdmin;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.business.insights.core.rest.validation.Validator;
import com.atlassian.business.insights.core.rest.validation.validators.bodyparam.ExportPathValidator;
import com.atlassian.business.insights.core.rest.validation.validators.bodyparam.ExportScheduleValidator;
import com.atlassian.business.insights.core.rest.validation.validators.util.SchemaVersionValueParser;
import com.atlassian.business.insights.core.service.ExportPathHolder;
import com.atlassian.business.insights.core.service.api.ConfigService;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.business.insights.core.service.api.ExportScheduleService;
import com.atlassian.business.insights.core.service.api.ScheduleConfigService;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.status.JobDetails;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

@OpenAPIDefinition(info=@Info(title="Data Pipeline Config Resource", version="1.0", description="Experimental Data Pipeline API. Configure Data Pipeline settings. The root path is /rest/datapipeline/latest"))
@Path(value="/config")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@Component
@InterceptorChain(value={ValidateUserIsAuthedAsSysAdmin.class, ValidateLicenseIsDc.class, ValidateRequestBody.class})
public class ConfigResource {
    private final ConfigService configService;
    private final EventPublisherService eventPublisherService;
    private final ScheduleConfigService scheduleConfigService;
    private final ExportScheduleService exportScheduleService;
    private final TimeZoneManager timeZoneManager;
    private final DatasetProvider datasetProvider;

    @Autowired
    public ConfigResource(ConfigService configService, ScheduleConfigService scheduleConfigService, EventPublisherService eventPublisherService, ExportScheduleService exportScheduleService, TimeZoneManager timeZoneManager, DatasetProvider datasetProvider) {
        this.configService = configService;
        this.scheduleConfigService = scheduleConfigService;
        this.eventPublisherService = eventPublisherService;
        this.exportScheduleService = exportScheduleService;
        this.timeZoneManager = timeZoneManager;
        this.datasetProvider = datasetProvider;
    }

    @Operation(summary="Creates or updates custom export root path", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns the details of the updated export path.", content={@Content(schema=@Schema(implementation=ConfigExportPathRequest.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @PUT
    @Path(value="/export-path")
    public Response updateExportPath(@RequestBody @Validator(value=ExportPathValidator.class) String exportPath) throws IOException {
        this.publishCustomExportPathUpdateTriggeredAnalyticEvent();
        try {
            ConfigExportPathRequest request = (ConfigExportPathRequest)new ObjectMapper().readValue(exportPath, ConfigExportPathRequest.class);
            this.configService.setCustomExportPath(request.getPath());
        }
        catch (NotDirectoryException e) {
            this.publishCustomExportPathUpdateFailedAnalyticEvent();
            throw new InvalidRequestBodyException(new ValidationResult("data-pipeline.api.rest.config.error.invalid.root.directory.path"));
        }
        return this.getExportPathResponse();
    }

    @Operation(summary="Deletes custom export path and reverts to the default export path", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns the details of the updated export path.", content={@Content(schema=@Schema(implementation=ConfigExportPathRequest.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @DELETE
    @Path(value="/export-path")
    public Response deleteExportPath() throws NotDirectoryException {
        this.publishCustomExportPathDeleteTriggeredAnalyticEvent();
        this.configService.setCustomExportPath(null);
        return this.getExportPathResponse();
    }

    @Operation(summary="Retrieves the current export root path", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns the details of the updated export path.", content={@Content(schema=@Schema(implementation=ConfigExportPathRequest.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @GET
    @Path(value="/export-path")
    public Response getExportPath() {
        this.publishCustomExportPathGetRequestedAnalyticEvent();
        return this.getExportPathResponse();
    }

    @Operation(summary="Get supported schema versions", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns list of supported schema versions in descending order.", content={@Content(array=@ArraySchema(schema=@Schema(implementation=SchemaResponse.class)))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @GET
    @Path(value="/schema")
    public Response getSupportedSchemaVersions() {
        this.publishSupportedSchemaVersionsGetRequestedAnalyticEvent();
        List schemaResponses = this.datasetProvider.getAllDatasets().stream().sorted(new OrderComparator().reversed()).map(SchemaResponse::fromDataset).collect(Collectors.toList());
        return Response.status((Response.Status)Response.Status.OK).entity(schemaResponses).build();
    }

    @Operation(summary="Creates or updates current export schedule setting", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="202", description="Successfully received request to create schedule with provided settings."), @ApiResponse(responseCode="400", description="Bad request", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @PUT
    @Path(value="/schedule")
    public Response updateExportSchedule(@RequestBody @Validator(value=ExportScheduleValidator.class) ConfigExportScheduleRequest configRequest) {
        ZoneId zoneId = this.getProductDefaultTimeZoneId();
        Dataset dataset = Optional.ofNullable(configRequest.getSchemaVersion()).map(this::toDataset).orElseGet(() -> ((DatasetProvider)this.datasetProvider).getDefaultDataset());
        ScheduleConfig.Builder scheduleConfigBuilder = new ScheduleConfig.Builder().from(configRequest).schemaVersion(dataset.getVersion()).zoneId(zoneId);
        this.publishScheduleUpdateTriggeredAnalyticEvent(scheduleConfigBuilder.build());
        JobDetails jobDetails = this.exportScheduleService.scheduleJob(scheduleConfigBuilder.build());
        Date scheduledStartDate = Optional.ofNullable(jobDetails.getNextRunTime()).orElseThrow(() -> new RuntimeException("Could not calculate schedule starting date!"));
        ScheduleConfig scheduleConfigWithStartDate = scheduleConfigBuilder.scheduleStartDate(DateConversionUtil.formatToIso(scheduledStartDate.toInstant(), zoneId)).build();
        this.scheduleConfigService.setExportSchedule(scheduleConfigWithStartDate);
        this.publishScheduleUpdatedAnalyticEvent(scheduleConfigWithStartDate);
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }

    @Operation(summary="Deletes export schedule", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successfully deleted schedule if there is one set."), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @DELETE
    @Path(value="/schedule")
    public Response deleteExportSchedule() {
        this.publishScheduleDeleteTriggeredAnalyticEvent();
        this.scheduleConfigService.setExportSchedule(null);
        this.exportScheduleService.unscheduleJob();
        this.publishScheduleDeletedAnalyticEvent();
        return Response.status((Response.Status)Response.Status.OK).build();
    }

    @Operation(summary="Retrieves the current export schedule setting", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns the settings of the current export schedule if there is one set, otherwise, it will return an empty response", content={@Content(schema=@Schema(implementation=ConfigExportScheduleResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @GET
    @Path(value="/schedule")
    public Response getExportSchedule() {
        this.publishScheduleGetRequestedAnalyticEvent();
        Optional<ScheduleConfig> scheduleConfig = this.scheduleConfigService.getExportSchedule();
        if (scheduleConfig.isPresent()) {
            String nextRunTime = this.scheduleConfigService.getNextRunTime().map(date -> DateConversionUtil.formatToIso(date.toInstant(), ZoneId.of(((ScheduleConfig)scheduleConfig.get()).getZoneId()))).orElse(null);
            SchemaStatus schemaStatus = this.datasetProvider.getSchemaStatus(scheduleConfig.get().getSchemaVersion());
            ConfigExportScheduleResponse configExportScheduleResponse = ConfigExportScheduleResponse.from(scheduleConfig.get(), schemaStatus, nextRunTime);
            return Response.status((Response.Status)Response.Status.OK).entity((Object)configExportScheduleResponse).build();
        }
        return Response.status((Response.Status)Response.Status.OK).entity(Collections.emptyMap()).build();
    }

    private ZoneId getProductDefaultTimeZoneId() {
        return this.timeZoneManager.getDefaultTimeZone().toZoneId();
    }

    private Response getExportPathResponse() {
        ExportPathHolder exportPath = this.configService.getRootExportPathHolder();
        return Response.status((Response.Status)Response.Status.OK).entity((Object)ConfigExportPathResponse.from(exportPath)).build();
    }

    private void publishCustomExportPathUpdateTriggeredAnalyticEvent() {
        this.eventPublisherService.publish(new CustomExportPathUpdateTriggeredAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishCustomExportPathUpdateFailedAnalyticEvent() {
        this.eventPublisherService.publish(new CustomExportPathUpdateFailedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishCustomExportPathDeleteTriggeredAnalyticEvent() {
        this.eventPublisherService.publish(new CustomExportPathDeleteTriggeredAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishCustomExportPathGetRequestedAnalyticEvent() {
        this.eventPublisherService.publish(new CustomExportPathGetRequestedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishSupportedSchemaVersionsGetRequestedAnalyticEvent() {
        this.eventPublisherService.publish(new SupportedSchemaVersionsGetRequestedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishScheduleUpdateTriggeredAnalyticEvent(ScheduleConfig scheduleConfig) {
        this.eventPublisherService.publish(new ExportScheduleUpdateTriggeredAnalyticEvent(this.eventPublisherService.getPluginVersion(), scheduleConfig));
    }

    private void publishScheduleUpdatedAnalyticEvent(ScheduleConfig scheduleConfigWithStartDate) {
        this.eventPublisherService.publish(new ExportScheduleUpdateAnalyticEvent(this.eventPublisherService.getPluginVersion(), scheduleConfigWithStartDate));
    }

    private void publishScheduleDeleteTriggeredAnalyticEvent() {
        this.eventPublisherService.publish(new ExportScheduleDeleteTriggeredAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishScheduleDeletedAnalyticEvent() {
        this.eventPublisherService.publish(new ExportScheduleDeletedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private void publishScheduleGetRequestedAnalyticEvent() {
        this.eventPublisherService.publish(new ExportScheduleGetRequestedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
    }

    private Dataset toDataset(String schemaVersion) {
        return (Dataset)SchemaVersionValueParser.parse(schemaVersion).flatMap(arg_0 -> ((DatasetProvider)this.datasetProvider).getDataset(arg_0)).orElseThrow(() -> new InvalidRequestBodyException(new ValidationResult("data-pipeline.api.rest.request.body.config.schema.version.invalid")));
    }
}

