/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.filter.OptOutEntity
 *  com.atlassian.business.insights.api.filter.OptOutEntityType
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.message.I18nResolver
 *  io.swagger.v3.oas.annotations.OpenAPIDefinition
 *  io.swagger.v3.oas.annotations.Operation
 *  io.swagger.v3.oas.annotations.Parameter
 *  io.swagger.v3.oas.annotations.info.Info
 *  io.swagger.v3.oas.annotations.media.ArraySchema
 *  io.swagger.v3.oas.annotations.media.Content
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.parameters.RequestBody
 *  io.swagger.v3.oas.annotations.responses.ApiResponse
 *  io.swagger.v3.oas.annotations.responses.ApiResponses
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.stereotype.Component
 */
package com.atlassian.business.insights.core.rest;

import com.atlassian.business.insights.api.filter.OptOutEntity;
import com.atlassian.business.insights.api.filter.OptOutEntityType;
import com.atlassian.business.insights.core.rest.model.ErrorStatusResponse;
import com.atlassian.business.insights.core.rest.model.OptOutConfigurationRequest;
import com.atlassian.business.insights.core.rest.model.OptOutConfigurationResponse;
import com.atlassian.business.insights.core.rest.model.OptOutEntityResponse;
import com.atlassian.business.insights.core.rest.model.OptOutSummaryResponse;
import com.atlassian.business.insights.core.rest.validation.ValidateLicenseIsDc;
import com.atlassian.business.insights.core.rest.validation.ValidateQueryParams;
import com.atlassian.business.insights.core.rest.validation.ValidateRequestBody;
import com.atlassian.business.insights.core.rest.validation.ValidateUserIsAuthedAsSysAdmin;
import com.atlassian.business.insights.core.rest.validation.Validator;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.ContinueIfNotFoundValidator;
import com.atlassian.business.insights.core.service.api.EntityOptOutService;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.message.I18nResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Component;

@OpenAPIDefinition(info=@Info(title="Data Pipeline Opt-Out Resource", version="1.0", description="Experimental Data Pipeline API. Configure Data Pipeline opt-out settings. The root path is /rest/datapipeline/latest"))
@Path(value="/config/optout")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@Component
@InterceptorChain(value={ValidateUserIsAuthedAsSysAdmin.class, ValidateLicenseIsDc.class, ValidateRequestBody.class, ValidateQueryParams.class})
public class OptOutRestResource {
    private static final String INVALID_RES_TYPE_I18N_KEY = "data-pipeline.api.rest.config.optout.request.body.resource.type.invalid";
    private static final String RES_NOT_FOUND_I18N_KEY = "data-pipeline.api.rest.config.optout.request.body.resource.not.found";
    private final EntityOptOutService entityOptOutService;
    private final I18nResolver i18nResolver;

    public OptOutRestResource(EntityOptOutService entityOptOutService, I18nResolver i18nResolver) {
        this.entityOptOutService = entityOptOutService;
        this.i18nResolver = i18nResolver;
    }

    @Operation(summary="List all opt-out entities", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns all the opt-out entities that have been added.", content={@Content(schema=@Schema(implementation=OptOutSummaryResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @GET
    public Response getOptOutEntities() {
        return Response.ok().entity((Object)new OptOutSummaryResponse(this.mapOptOutEntityResponses(this.entityOptOutService.getOptOutEntities()))).build();
    }

    @Operation(summary="Add opt-out entities.", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns the opt-out entities that have been added.", content={@Content(schema=@Schema(implementation=OptOutConfigurationResponse.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @POST
    public Response addOptOutEntities(@Parameter(description="Continue if one or more are resources not found.") @DefaultValue(value="false") @Schema(type="boolean") @QueryParam(value="continueIfNotFound") @Validator(value=ContinueIfNotFoundValidator.class) boolean continueIfNotFound, @RequestBody @Nonnull OptOutConfigurationRequest optOutConfigurationRequest) {
        this.validateSupportedEntityType(optOutConfigurationRequest);
        ArrayList<OptOutEntity> notFoundEntityHolder = new ArrayList<OptOutEntity>();
        List<OptOutEntity> optOutEntities = this.mapOptOutEntities(continueIfNotFound, optOutConfigurationRequest, notFoundEntityHolder);
        this.entityOptOutService.addEntityOptOuts(optOutEntities.stream().map(OptOutEntity::getIdentifier).collect(Collectors.toList()));
        return Response.ok().entity((Object)new OptOutConfigurationResponse(this.mapOptOutEntityResponses(optOutEntities), notFoundEntityHolder.isEmpty() ? null : this.mapOptOutEntityResponses(notFoundEntityHolder))).build();
    }

    @Operation(summary="Remove opt-out entities.", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns the opt-out entities that have been deleted.", content={@Content(schema=@Schema(implementation=OptOutConfigurationResponse.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @DELETE
    public Response removeOptOutEntities(@Parameter(description="Continue if one or more entities are not found.") @DefaultValue(value="true") @Schema(type="boolean") @QueryParam(value="continueIfNotFound") @Validator(value=ContinueIfNotFoundValidator.class) boolean continueIfNotFound, @RequestBody @Nonnull OptOutConfigurationRequest optOutConfigurationRequest) {
        this.validateSupportedEntityType(optOutConfigurationRequest);
        ArrayList<OptOutEntity> notFoundEntityHolder = new ArrayList<OptOutEntity>();
        List<OptOutEntity> optOutEntities = this.mapOptOutEntities(continueIfNotFound, optOutConfigurationRequest, notFoundEntityHolder);
        optOutEntities.addAll(notFoundEntityHolder);
        this.entityOptOutService.removeEntityOptOuts(optOutEntities.stream().map(OptOutEntity::getIdentifier).collect(Collectors.toList()));
        return Response.ok().entity((Object)new OptOutConfigurationResponse(this.mapOptOutEntityResponses(optOutEntities), null)).build();
    }

    private void validateSupportedEntityType(OptOutConfigurationRequest optOutConfigurationRequest) throws WebApplicationException {
        Optional<OptOutEntityType> optOutEntityType = this.mapOptOutEntityType(optOutConfigurationRequest.getType());
        if (!optOutEntityType.isPresent() || !this.entityOptOutService.getSupportedEntityTypes().contains(optOutEntityType.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorStatusResponse(Response.Status.BAD_REQUEST.getStatusCode(), this.i18nResolver.getText(INVALID_RES_TYPE_I18N_KEY, new Serializable[]{optOutConfigurationRequest.getType(), this.getSupportedEntityTypeAsString()}), null, Instant.now().toString())).build());
        }
    }

    private List<OptOutEntity> mapOptOutEntities(boolean continueIfNotFound, OptOutConfigurationRequest optOutConfigurationRequest, List<OptOutEntity> notFoundEntityHolder) {
        return optOutConfigurationRequest.getKeys().stream().map(key -> this.mapOptOutEntity(optOutConfigurationRequest.getType(), (String)key, continueIfNotFound, notFoundEntityHolder)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private Optional<OptOutEntity> mapOptOutEntity(String entityType, String entityKey, boolean continueIfNotFound, List<OptOutEntity> notFoundEntityHolder) {
        return this.mapOptOutEntityType(entityType).map(optOutEntityType -> {
            Optional<OptOutEntity> optOutEntity = this.entityOptOutService.enrichOptOutEntity((OptOutEntityType)optOutEntityType, entityKey);
            if (!optOutEntity.isPresent()) {
                if (continueIfNotFound) {
                    notFoundEntityHolder.add(OptOutEntity.builder((OptOutEntityType)optOutEntityType, (String)entityKey).build());
                } else {
                    throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorStatusResponse(Response.Status.BAD_REQUEST.getStatusCode(), this.i18nResolver.getText(RES_NOT_FOUND_I18N_KEY, new Serializable[]{entityType, entityKey}), null, Instant.now().toString())).build());
                }
            }
            return optOutEntity;
        }).orElseThrow(() -> new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorStatusResponse(Response.Status.BAD_REQUEST.getStatusCode(), this.i18nResolver.getText(INVALID_RES_TYPE_I18N_KEY, new Serializable[]{entityType, this.getSupportedEntityTypeAsString()}), null, Instant.now().toString())).build()));
    }

    private String getSupportedEntityTypeAsString() {
        return this.entityOptOutService.getSupportedEntityTypes().stream().map(Enum::toString).collect(Collectors.joining(",", "[", "]"));
    }

    private Optional<OptOutEntityType> mapOptOutEntityType(String type) {
        return Arrays.stream(OptOutEntityType.values()).filter(optOutEntityType -> optOutEntityType.toString().equalsIgnoreCase(type)).findFirst();
    }

    private List<OptOutEntityResponse> mapOptOutEntityResponses(List<OptOutEntity> optOutEntities) {
        return optOutEntities.stream().map(this::mapOptOutEntityResponse).collect(Collectors.toList());
    }

    private OptOutEntityResponse mapOptOutEntityResponse(OptOutEntity optOutEntity) {
        return new OptOutEntityResponse(optOutEntity.getIdentifier().getType().toString(), optOutEntity.getIdentifier().getIdentifier(), optOutEntity.getKey(), optOutEntity.getDisplayName(), optOutEntity.getUri());
    }
}

