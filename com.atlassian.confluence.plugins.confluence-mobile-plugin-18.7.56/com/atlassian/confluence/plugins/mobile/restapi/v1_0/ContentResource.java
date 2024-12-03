/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.restapi.v1_0;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.FavouriteDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import com.atlassian.confluence.plugins.mobile.restapi.docs.CommentsResponse;
import com.atlassian.confluence.plugins.mobile.restapi.docs.RelationResponse;
import com.atlassian.confluence.plugins.mobile.restapi.docs.SavedContentResponse;
import com.atlassian.confluence.plugins.mobile.service.MobileChildContentService;
import com.atlassian.confluence.plugins.mobile.service.MobileContentService;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Tag(name="Content API", description="Contains all operations related to content")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content")
@Component
public class ContentResource {
    private final MobileContentService mobileContentService;
    private final MobileChildContentService mobileChildContentService;

    @Autowired
    public ContentResource(MobileContentService mobileContentService, MobileChildContentService mobileChildContentService) {
        this.mobileContentService = mobileContentService;
        this.mobileChildContentService = mobileChildContentService;
    }

    @Operation(summary="Get content", description="Gets the content of a page or blog post by Id", responses={@ApiResponse(responseCode="200", description="Page or blog post content", content={@Content(schema=@Schema(implementation=ContentDto.class))})})
    @GET
    @Path(value="/{contentId}")
    public ContentDto getContentById(@PathParam(value="contentId") long id) throws ServiceException {
        return this.mobileContentService.getContent(id);
    }

    @Operation(summary="Get comments", description="Gets a pageable list of comments by the parent content Id", responses={@ApiResponse(responseCode="200", description="Pageable list of comments", content={@Content(schema=@Schema(implementation=CommentsResponse.class))}), @ApiResponse(responseCode="404", description="No comments exist")})
    @GET
    @Path(value="/{contentId}/comment")
    public PageResponse<CommentDto> getCommentsByContainerId(@PathParam(value="contentId") ContentId id, @Parameter(description="footer,inline") @DefaultValue(value="author,body,parent,container,metadata") @QueryParam(value="expand") String expand, @Parameter(description="page,attachment") @DefaultValue(value="page") @QueryParam(value="include") String include) throws ServiceException {
        return this.mobileChildContentService.getComments(id, new Expansions(ExpansionsParser.parse((String)expand)), new Inclusions(include));
    }

    @Operation(summary="Get content metadata", description="Gets content metadata by content Id", responses={@ApiResponse(responseCode="200", description="Content metadata", content={@Content(schema=@Schema(implementation=ContentMetadataDto.class))}), @ApiResponse(responseCode="404", description="Content does not exist")})
    @GET
    @Path(value="/{contentId}/metadata")
    public ContentMetadataDto getContentMetadata(@PathParam(value="contentId") ContentId id) {
        return this.mobileContentService.getContentMetadata(id);
    }

    @Operation(summary="Get metadata for content creation", description="Gets the metadata used for creating content", responses={@ApiResponse(responseCode="200", description="Content metadata", content={@Content(schema=@Schema(implementation=ContentMetadataDto.class))})})
    @GET
    @Path(value="/creation/metadata")
    public ContentMetadataDto getCreationContentMetadata(@Parameter(description="global, space, page or blogpost") @QueryParam(value="context") @DefaultValue(value="global") String context, @Parameter(description="required for space or blogpost contexts") @QueryParam(value="spaceKey") String spaceKey, @Parameter(description="required for page context") @QueryParam(value="contentId") Long contentId) {
        return this.mobileContentService.getCreationContentMetadata(new Context(Context.Type.forValue(context), spaceKey, contentId));
    }

    @Operation(summary="Add to saved content", description="Favourites a content item by Id for the current user", responses={@ApiResponse(responseCode="200", description="Successfully favourited")})
    @POST
    @Path(value="/{contentId}/favourite")
    public Boolean favourite(@PathParam(value="contentId") Long id) {
        return this.mobileContentService.favourite(id);
    }

    @Operation(summary="Remove from saved content", description="Undo favourite of a content item by Id for the current user", responses={@ApiResponse(responseCode="200", description="Successfully undid favourite")})
    @DELETE
    @Path(value="/{contentId}/favourite")
    public Boolean removeFavouritePage(@PathParam(value="contentId") Long id) {
        return this.mobileContentService.removeFavourite(id);
    }

    @Operation(summary="Get saved pages", description="Gets a list of the user's favourited pages. Replaced by /content/save", deprecated=true, responses={@ApiResponse(responseCode="200", description="Favourited pages", content={@Content(array=@ArraySchema(schema=@Schema(implementation=FavouriteDto.class)))})})
    @GET
    @Deprecated
    @Path(value="/favourites")
    public List<FavouriteDto> getFavourites(@QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="50") int limit) {
        return this.mobileContentService.getFavourites((PageRequest)new SimplePageRequest(start, limit));
    }

    @Operation(summary="Get saved pages", description="Gets a list of the user's saved pages", responses={@ApiResponse(responseCode="200", description="Saved pages", content={@Content(schema=@Schema(implementation=SavedContentResponse.class))})})
    @GET
    @Path(value="/save")
    public PageResponse<ContentDto> getSavedList(@QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="50") int limit) {
        return this.mobileContentService.getSavedList((PageRequest)new SimplePageRequest(start, limit));
    }

    @Operation(summary="Get related content", description="Gets the related content hierarchy (parent/child/sibling)", responses={@ApiResponse(responseCode="200", description="Related content", content={@Content(schema=@Schema(implementation=RelationResponse.class))}), @ApiResponse(responseCode="501", description="The requested content type is not a page")})
    @GET
    @Path(value="/{contentId}/relation")
    public Map<String, PageResponse> getRelation(@PathParam(value="contentId") long contentId, @QueryParam(value="include") @DefaultValue(value="parent,child,sibling") String include, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="5") int limit) {
        return this.mobileContentService.getRelationContent(contentId, new Expansions(ExpansionsParser.parse((String)expand)), new Inclusions(include), (PageRequest)new SimplePageRequest(start, limit));
    }
}

