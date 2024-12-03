/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.ImmutableMap
 *  io.swagger.v3.oas.annotations.OpenAPIDefinition
 *  io.swagger.v3.oas.annotations.Operation
 *  io.swagger.v3.oas.annotations.info.Info
 *  io.swagger.v3.oas.annotations.media.Content
 *  io.swagger.v3.oas.annotations.media.Schema
 *  io.swagger.v3.oas.annotations.responses.ApiResponse
 *  io.swagger.v3.oas.annotations.responses.ApiResponses
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.business.insights.core.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@OpenAPIDefinition(info=@Info(title="Data Pipeline Export Resource", version="0.0.0", description="Experimental Data Pipeline API. Report the current status of Data Pipeline processes on the host product. The root path is /rest/datapipeline/latest"))
@Path(value="/ping")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class StatusResource {
    @Operation(summary="Check the Data Pipeline REST API is available.", tags={"data pipeline"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful. The Data Pipeline REST API is available", content={@Content(schema=@Schema(implementation=Map.class))})})
    @GET
    @AnonymousAllowed
    public Response ping() {
        return Response.ok((Object)ImmutableMap.of((Object)"ping", (Object)"success")).build();
    }
}

