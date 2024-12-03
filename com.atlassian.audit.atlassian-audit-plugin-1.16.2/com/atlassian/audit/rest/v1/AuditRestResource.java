/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditQuery$AuditResourceIdentifier
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.Page$Builder
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.api.util.pagination.PageRequest$Builder
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.spi.entity.AuditEntityTransformationService
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.sun.jersey.core.header.ContentDisposition
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.HttpHeaders
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.SecurityContext
 *  javax.ws.rs.core.UriInfo
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.rest.v1;

import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.csv.AuditCsvExportService;
import com.atlassian.audit.csv.AuditCsvExporter;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.file.AuditRetentionFileConfig;
import com.atlassian.audit.file.AuditRetentionFileConfigService;
import com.atlassian.audit.plugin.configuration.PermissionsEnforced;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.rest.model.AuditCoverageConfigJson;
import com.atlassian.audit.rest.model.AuditEntitiesResponseJson;
import com.atlassian.audit.rest.model.AuditExcludedActionsJson;
import com.atlassian.audit.rest.model.AuditExcludedActionsModifyRequestJson;
import com.atlassian.audit.rest.model.AuditRetentionConfigJson;
import com.atlassian.audit.rest.model.AuditRetentionFileConfigJson;
import com.atlassian.audit.rest.model.ResponseErrorJson;
import com.atlassian.audit.rest.v1.utils.AuditEntitySerializer;
import com.atlassian.audit.rest.v1.validation.AuditRestValidator;
import com.atlassian.audit.rest.v1.validation.ValidationInterceptor;
import com.atlassian.audit.rest.v1.validation.Validator;
import com.atlassian.audit.spi.entity.AuditEntityTransformationService;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.sun.jersey.core.header.ContentDisposition;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OpenAPIDefinition(info=@Info(title="Audit", version="1.0.0", description="Cross-product audit feature APIs. The root path is /rest/auditing/1.0"))
@Path(value="/")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class AuditRestResource {
    public static final String FORMAT_CSV_FILE = "csv";
    public static final String FORMAT_JSON = "json";
    private static final Logger log = LoggerFactory.getLogger(AuditRestResource.class);
    private static final String TEXT_CSV = "text/csv";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private final AuditSearchService searchService;
    private final InternalAuditCoverageConfigService coverageConfigService;
    private final AuditRetentionConfigService retentionConfigService;
    private final AuditRetentionFileConfigService retentionFileConfigService;
    private final AuditEntityTransformationService transformationService;
    private final AuditCsvExportService auditCsvExportService;
    private final TimeZoneManager timeZoneManager;
    private final ApplicationProperties applicationProperties;
    private final ProductLicenseChecker licenseChecker;
    private final PropertiesProvider propertiesProvider;
    private final ExcludedActionsService excludedActionsService;

    public AuditRestResource(ApplicationProperties applicationProperties, AuditCsvExportService auditCsvExportService, AuditSearchService searchService, @PermissionsEnforced InternalAuditCoverageConfigService coverageConfigService, @PermissionsEnforced AuditRetentionConfigService retentionConfigService, @PermissionsEnforced AuditRetentionFileConfigService retentionFileConfigService, AuditEntityTransformationService transformationService, TimeZoneManager timeZoneManager, ProductLicenseChecker licenseChecker, PropertiesProvider propertiesProvider, ExcludedActionsService excludedActionsService) {
        this.applicationProperties = applicationProperties;
        this.auditCsvExportService = auditCsvExportService;
        this.searchService = searchService;
        this.coverageConfigService = coverageConfigService;
        this.licenseChecker = licenseChecker;
        this.propertiesProvider = propertiesProvider;
        this.retentionConfigService = retentionConfigService;
        this.retentionFileConfigService = retentionFileConfigService;
        this.timeZoneManager = timeZoneManager;
        this.transformationService = transformationService;
        this.excludedActionsService = excludedActionsService;
    }

    @GET
    @Path(value="/events")
    @Produces(value={"application/json", "text/csv"})
    @Operation(summary="Get a paginated list of audit events", tags={"audit"})
    @InterceptorChain(value={ValidationInterceptor.class})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditEntitiesResponseJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response getAuditEvents(@Parameter(description="The start timestamp in ISO8601 format", example="2019-11-01T01:00:00.000Z") @Schema(type="string", format="date-time") @QueryParam(value="from") @Validator(value=AuditRestValidator.FromValidator.class) String from, @Parameter(description="The end timestamp in ISO8601 format", example="2019-12-01T01:00:00.000Z") @Schema(type="string", format="date-time") @QueryParam(value="to") @Validator(value=AuditRestValidator.ToValidator.class) String to, @Parameter(description="The number of records to skip") @Schema(minimum="0", type="integer", format="int32") @QueryParam(value="offset") @DefaultValue(value="0") @Validator(value=AuditRestValidator.OffsetValidator.class) String rawOffset, @Parameter(description="Location of last result returned in format of timestamp,ID. For making a request for page X, the value of this field can be obtained from pagingInfo->nextPageCursor in response for page X-1", example="1577437517322,9") @QueryParam(value="pageCursor") @Validator(value=AuditRestValidator.CursorValidator.class) String cursor, @Parameter(description="The maximum number of records returned") @Schema(minimum="1", maximum="100000", type="integer", format="int32") @QueryParam(value="limit") @DefaultValue(value="200") @Validator(value=AuditRestValidator.LimitValidator.class) String rawLimit, @Parameter(description="Audit event author identifiers separated by comma", example="42,46", style=ParameterStyle.FORM, explode=Explode.FALSE) @QueryParam(value="userIds") @Validator(value=AuditRestValidator.UserIdsValidator.class) String userIds, @Parameter(description="Audit categories separated by comma", example="Global settings changed,Group deleted", style=ParameterStyle.FORM, explode=Explode.FALSE) @QueryParam(value="categories") @Validator(value=AuditRestValidator.CategoriesValidator.class) String categories, @Parameter(description="Comma-separated list of actions which triggered the audit record", example="Permissions,Apps", style=ParameterStyle.FORM, explode=Explode.FALSE) @QueryParam(value="actions") @Validator(value=AuditRestValidator.ActionsValidator.class) String actions, @Parameter(description="A list of affected objects separated by semicolon. Each affected object is a pair of object type and id separated by comma. Administrator permission of all affected objects is required when specified. Global administrator permission is required when no affected object is specified.", example="space,42;space,46") @QueryParam(value="affectedObject") @Validator(value=AuditRestValidator.AffectedObjectsValidator.class) String affectedObject, @Parameter(description="Search expression, this parameter may have negative performance impact. It's recommended to use scanLimit when this parameter is specified.") @QueryParam(value="search") @Validator(value=AuditRestValidator.SearchValidator.class) String search, @Parameter(description="What format output should the server create") @QueryParam(value="outputFormat") @DefaultValue(value="json") @Validator(value=AuditRestValidator.FormatValidator.class) String outputFormat, @Parameter(description="The maximum number of records to be scanned in the inverse insertion order with from and to filters taking precedence, the default value is 2147483647 which means there is no limit") @QueryParam(value="scanLimit") @Schema(minimum="1", maximum="2147483647", type="integer", format="int32") @DefaultValue(value="2147483647") @Validator(value=AuditRestValidator.ScanLimitValidator.class) String rawScanLimit, @Context HttpHeaders headers, @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        int offset = Integer.parseInt(rawOffset);
        int limit = Integer.parseInt(rawLimit);
        int scanLimit = Integer.parseInt(rawScanLimit);
        AuditQuery query = this.generateAuditQuery(from, to, userIds, categories, actions, affectedObject, search);
        PageRequest<AuditEntityCursor> pageRequest = this.generatePageRequest(cursor, offset, limit);
        this.checkDcOnlyFilters(query);
        if (outputFormat.equals(FORMAT_CSV_FILE)) {
            return this.generateCsvResponse(offset, limit, query);
        }
        if (outputFormat.equals(FORMAT_JSON)) {
            return this.generateJsonResponse(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), uriInfo, pageRequest, query, scanLimit);
        }
        throw new IllegalStateException("Unexpected outputFormat was provided and was not caught by validation");
    }

    private void checkDcOnlyFilters(AuditQuery query) {
        if (!(query.getCategories().isEmpty() && query.getActions().isEmpty() || !this.licenseChecker.isNotDcLicense())) {
            throw new IllegalArgumentException("categories and actions are only supported for Datacenter license.");
        }
    }

    @Nonnull
    private PageRequest<AuditEntityCursor> generatePageRequest(String cursor, int offset, int limit) {
        String[] cursorParts;
        AuditEntityCursor entityCursor = null;
        if (cursor != null && !cursor.trim().isEmpty() && (cursorParts = cursor.split(",\\s*")).length == 2) {
            entityCursor = new AuditEntityCursor(Instant.ofEpochMilli(Long.parseLong(cursorParts[0])), Long.parseLong(cursorParts[1]));
        }
        return new PageRequest.Builder().offset(offset).limit(limit).cursor(entityCursor).build();
    }

    private AuditQuery generateAuditQuery(String from, String to, String userIds, String categories, String actions, String affectedObjects, String search) {
        ArrayList resourceIdentifiers = new ArrayList();
        if (affectedObjects != null) {
            Stream.of(affectedObjects.split(";\\s*")).forEach(objectString -> {
                String[] affectedObjectParts = objectString.split(",\\s*");
                resourceIdentifiers.add(new AuditQuery.AuditResourceIdentifier(affectedObjectParts[0], affectedObjectParts[1]));
            });
        }
        return AuditQuery.builder().actions(this.split(actions)).userIds(this.split(userIds)).categories(this.split(categories)).searchText(search).from(this.parseTime(from)).to(this.parseTime(to)).resources(resourceIdentifiers).build();
    }

    private Response generateJsonResponse(String baseUrl, @Context UriInfo uriInfo, PageRequest<AuditEntityCursor> pageRequest, AuditQuery query, int scanLimit) {
        try {
            Page<AuditEntity, AuditEntityCursor> entitiesPage = this.retrieveQuery(pageRequest, query, scanLimit);
            List entities = entitiesPage.getValues();
            log.trace("generateJsonResponse baseUrl={}, query={}, pageRequest={}, scanLimit={}, entities.size={}, entities={}", new Object[]{baseUrl, query, pageRequest, scanLimit, entities.size(), entities});
            AuditEntitiesResponseJson response = new AuditEntitiesResponseJson(entitiesPage, entity -> AuditEntitySerializer.toJson(entity, this.timeZoneManager.getDefaultTimeZone()), baseUrl, uriInfo);
            return Response.ok((Object)response).type("application/json").build();
        }
        catch (TimeoutException e) {
            return Response.serverError().entity((Object)e.getMessage()).build();
        }
    }

    private Response generateCsvResponse(int offset, int limit, AuditQuery query) {
        AuditCsvExporter csvExporter = this.auditCsvExportService.createExporter(query);
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss'Z'").format(LocalDateTime.now(ZoneId.systemDefault()));
        String fileName = String.format("Auditing Export %s.csv", date);
        ContentDisposition contentDisposition = ContentDisposition.type((String)"attachment").fileName(fileName).creationDate(new Date(Instant.now().toEpochMilli())).build();
        return Response.ok(output -> csvExporter.export(output, offset, limit)).type(TEXT_CSV).header(CONTENT_DISPOSITION, (Object)contentDisposition).build();
    }

    private Page<AuditEntity, AuditEntityCursor> retrieveQuery(PageRequest<AuditEntityCursor> pageRequest, AuditQuery query, int scanLimit) throws TimeoutException {
        Page page = this.searchService.findBy(query, pageRequest, scanLimit);
        return new Page.Builder(this.transformationService.transform(page.getValues()), page.getIsLastPage()).nextPageRequest((PageRequest)page.getNextPageRequest().orElse(null)).build();
    }

    @Nullable
    private String[] split(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        return str.split(",\\s*");
    }

    @Nullable
    private Instant parseTime(String str) {
        if (str == null) {
            return null;
        }
        return Instant.parse(str);
    }

    @GET
    @Path(value="/configuration/retention")
    @Operation(summary="Get current audit log retention database configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditRetentionConfigJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response getAuditRetentionConfiguration(@Context SecurityContext securityContext) {
        AuditRetentionConfigJson response = new AuditRetentionConfigJson(this.retentionConfigService.getConfig());
        return Response.ok((Object)response).build();
    }

    @PUT
    @Path(value="/configuration/retention")
    @Operation(summary="Set current audit log retention database configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditRetentionConfigJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response updateAuditRetentionConfiguration(@Parameter(required=true) AuditRetentionConfigJson body, @Context SecurityContext securityContext) {
        this.retentionConfigService.updateConfig(body.toRetentionConfig());
        return this.getAuditRetentionConfiguration(securityContext);
    }

    @GET
    @Path(value="/configuration/retention/file")
    @Operation(summary="Get current audit log retention file configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditRetentionFileConfigJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response getAuditRetentionFileConfiguration(@Context SecurityContext securityContext) {
        AuditRetentionFileConfigJson response = this.retentionFileConfigService.getConfig().toJson();
        return Response.ok((Object)response).build();
    }

    @PUT
    @Path(value="/configuration/retention/file")
    @Operation(summary="Set current audit log retention file configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditRetentionFileConfigJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response updateAuditRetentionFileConfiguration(@Parameter(required=true) AuditRetentionFileConfigJson body, @Context SecurityContext securityContext) {
        this.retentionFileConfigService.updateConfig(AuditRetentionFileConfig.fromJson(body, this.propertiesProvider.getInteger("plugin.audit.file.max.file.size", 100)));
        return this.getAuditRetentionFileConfiguration(securityContext);
    }

    @GET
    @Path(value="/configuration/coverage")
    @Operation(summary="Get current audit log coverage configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditCoverageConfigJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response getAuditCoverageConfiguration(@Context SecurityContext securityContext) {
        AuditCoverageConfigJson response = new AuditCoverageConfigJson(this.coverageConfigService.getConfig());
        return Response.ok((Object)response).build();
    }

    @PUT
    @Path(value="/configuration/coverage")
    @Operation(summary="Set current audit log coverage configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditCoverageConfigJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response updateAuditCoverageConfiguration(@Parameter(required=true) AuditCoverageConfigJson body, @Context SecurityContext securityContext) {
        this.coverageConfigService.updateConfig(body.toCoverageConfig());
        return this.getAuditCoverageConfiguration(securityContext);
    }

    @GET
    @Path(value="/configuration/denylist")
    @Operation(summary="Get current excluded actions configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditExcludedActionsJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response getExcludedActions(@Context SecurityContext securityContext) {
        this.checkDcOnly();
        AuditExcludedActionsJson auditDenyListConfJson = new AuditExcludedActionsJson(this.excludedActionsService.getExcludedActions());
        return Response.ok((Object)auditDenyListConfJson).build();
    }

    @POST
    @Path(value="/configuration/denylist")
    @Operation(summary="Modify existing excluded actions configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditExcludedActionsModifyRequestJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response modifyExcludedActions(@Parameter(required=true) AuditExcludedActionsModifyRequestJson modifyRequest, @Context SecurityContext securityContext) {
        this.checkDcOnly();
        this.excludedActionsService.updateExcludedActions(modifyRequest.getActionsToAdd(), modifyRequest.getActionsToDelete());
        return this.getExcludedActions(securityContext);
    }

    @PUT
    @Path(value="/configuration/denylist")
    @Operation(summary="Replace existing exclude actions configuration", tags={"configuration"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditExcludedActionsJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response replaceExcludedActions(@Parameter(required=true) AuditExcludedActionsJson events, @Context SecurityContext securityContext) {
        this.checkDcOnly();
        this.excludedActionsService.replaceExcludedActions(events.getActions());
        return this.getExcludedActions(securityContext);
    }

    private void checkDcOnly() {
        if (this.licenseChecker.isNotDcLicense()) {
            throw new IllegalArgumentException("Excluded events is only supported for Datacenter license.");
        }
    }
}

