/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import com.atlassian.confluence.plugins.mobile.restapi.docs.SpacesResponse;
import com.atlassian.confluence.plugins.mobile.service.MobileSpaceService;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Space API", description="Contains all operations for spaces")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/space")
@Component
public class MobileSpaceResource {
    private final MobileSpaceService spaceService;

    @Autowired
    public MobileSpaceResource(MobileSpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @Operation(summary="Get spaces", description="Gets the user's spaces", responses={@ApiResponse(responseCode="200", description="The user's spaces", content={@Content(schema=@Schema(implementation=SpacesResponse.class))})})
    @GET
    public PageResponse<SpaceDto> getSpaces(@QueryParam(value="include") @DefaultValue(value="favourite,recent,other") String include, @Parameter(description="logoPath, homePage") @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="20") int limit, @Parameter(description="overrides page size (getAllFavourites: will get all favourites regardless of limit)") @QueryParam(value="priority") @DefaultValue(value="getAllFavourites") String priority) {
        return this.spaceService.getSpaces(priority, new Expansions(ExpansionsParser.parse((String)expand)), new Inclusions(include), (PageRequest)new SimplePageRequest(start, limit));
    }

    @Operation(summary="Get space home page", description="Get the home page of a space", responses={@ApiResponse(responseCode="200", description="Space home page", content={@Content(schema=@Schema(implementation=ContentDto.class))})})
    @GET
    @Path(value="{spaceKey}/homePage")
    public ContentDto getHomePage(@PathParam(value="spaceKey") String spaceKey, @Parameter(description="author, timeToRead, body, space") @QueryParam(value="expand") @DefaultValue(value="author") String expand) {
        return this.spaceService.getHomePage(spaceKey, new Expansions(ExpansionsParser.parse((String)expand)));
    }
}

