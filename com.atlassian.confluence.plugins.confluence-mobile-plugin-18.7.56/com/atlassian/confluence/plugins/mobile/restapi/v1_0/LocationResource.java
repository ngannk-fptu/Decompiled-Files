/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Location API", description="Contains all operations related to resource locations")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/location")
@Component
public class LocationResource {
    private final LocationService LocationService;

    @Autowired
    public LocationResource(LocationService LocationService2) {
        this.LocationService = LocationService2;
    }

    @Operation(summary="Get create page location", description="Get the suggested location for creating a page", responses={@ApiResponse(responseCode="200", description="Page Location", content={@Content(schema=@Schema(implementation=LocationDto.class))}), @ApiResponse(responseCode="400", description="Bad Request"), @ApiResponse(responseCode="403", description="User unauthorized")})
    @GET
    @Path(value="/page/creation")
    public LocationDto getPageCreateLocation(@Parameter(description="global, space, page or blogpost") @QueryParam(value="context") String context, @Parameter(description="required for space or blogpost contexts") @QueryParam(value="spaceKey") String spaceKey, @Parameter(description="required for page context") @QueryParam(value="contentId") Long contentId) {
        return this.LocationService.getPageCreateLocation(new Context(Context.Type.forValue(context), spaceKey, contentId));
    }
}

