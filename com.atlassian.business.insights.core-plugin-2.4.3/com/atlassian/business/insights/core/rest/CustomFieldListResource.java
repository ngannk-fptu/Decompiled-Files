/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.customfields.CustomFieldListService
 *  io.swagger.v3.oas.annotations.Operation
 *  io.swagger.v3.oas.annotations.media.ArraySchema
 *  io.swagger.v3.oas.annotations.media.Content
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.responses.ApiResponse
 *  io.swagger.v3.oas.annotations.responses.ApiResponses
 *  javax.ws.rs.GET
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.business.insights.core.rest;

import com.atlassian.business.insights.api.customfields.CustomFieldListService;
import com.atlassian.business.insights.core.rest.exception.NotImplementedException;
import com.atlassian.business.insights.core.rest.model.ErrorStatusResponse;
import com.atlassian.business.insights.core.rest.model.ProcessStatusResponse;
import com.atlassian.business.insights.core.rest.validation.FeatureFlagRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomFieldListResource {
    private static final String ALL_EXPORTABLE_CF_KEY = "data.pipeline.feature.jira.all.exportable.custom.fields";
    private final Optional<CustomFieldListService> customFieldsService;

    @Autowired
    public CustomFieldListResource(Optional<CustomFieldListService> customFieldsService) {
        this.customFieldsService = customFieldsService;
    }

    @Operation(summary="Retrieve the list of all exportable custom fields.", tags={"data pipeline", "ExportableCustomFieldType"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Returns the lis of all available custom fields implementing ExportableCustomFieldType interface.", content={@Content(schema=@Schema(implementation=ProcessStatusResponse.class))}), @ApiResponse(responseCode="401", description="Unauthorized", content={@Content(array=@ArraySchema(schema=@Schema(implementation=Response.class)))}), @ApiResponse(responseCode="403", description="Forbidden", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))}), @ApiResponse(responseCode="404", description="Not implemented by product or feature not activated", content={@Content(schema=@Schema(implementation=ErrorStatusResponse.class))})})
    @GET
    @FeatureFlagRequired(value="data.pipeline.feature.jira.all.exportable.custom.fields")
    public Response getAllExportable() {
        return this.customFieldsService.map(CustomFieldListService::getExportableCustomFields).map(Response::ok).orElseThrow(NotImplementedException::new).build();
    }
}

