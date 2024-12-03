/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.Page$Builder
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.api.util.pagination.PageRequest$Builder
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.spi.lookup.AuditingResourcesLookupService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.audit.lookup.rest.v1;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.lookup.rest.v1.AuditAuthorResponseJson;
import com.atlassian.audit.lookup.rest.v1.AuditResourceResponseJson;
import com.atlassian.audit.rest.model.AuditResourceLookupJson;
import com.atlassian.audit.rest.model.ResponseErrorJson;
import com.atlassian.audit.rest.v1.utils.AuditEntitySerializer;
import com.atlassian.audit.spi.lookup.AuditingResourcesLookupService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@OpenAPIDefinition(info=@Info(title="Audit Resource Lookup", version="1.0.0", description="API to lookup data for Advanced Auditing UI, supports author, project, repository and space. The root path is /rest/auditing/1.0"))
@Path(value="/lookup")
@Produces(value={"application/json"})
public class AuditResourceLookupRestResource {
    private static final String DELIMS = ". ";
    private final AuditingResourcesLookupService auditResourceLookupProvider;
    private final ApplicationProperties applicationProperties;

    public AuditResourceLookupRestResource(AuditingResourcesLookupService auditResourceLookupProvider, ApplicationProperties applicationProperties) {
        this.auditResourceLookupProvider = auditResourceLookupProvider;
        this.applicationProperties = applicationProperties;
    }

    private static AuditResourceLookupJson toJson(AuditResource auditResource) {
        return new AuditResourceLookupJson(auditResource.getName(), auditResource.getType(), auditResource.getUri(), auditResource.getId());
    }

    @GET
    @Path(value="/resource/{resourceType}")
    @Operation(summary="Lookup audit resources", tags={"resource", "lookup"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditResourceResponseJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response lookupResources(@Parameter(description="The type of resource") @PathParam(value="resourceType") String resType, @Parameter(description="The number of records to skip") @Schema(minimum="0") @QueryParam(value="offset") @DefaultValue(value="0") int offset, @Parameter(description="Location of last result returned in format of ID or resource. For making a request for page X, the value of this field can be obtained from pagingInfo->nextPageCursor in response for page X-1", example="9") @QueryParam(value="pageCursor") String cursor, @Parameter(description="The maximum number of records returned") @Schema(minimum="1", maximum="100000") @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Parameter(description="Search text") @QueryParam(value="search") String search, @Context UriInfo uriInfo) {
        Page res = this.auditResourceLookupProvider.lookupAuditResource(resType, search, new PageRequest.Builder().offset(offset).limit(limit).cursor((Object)cursor).build());
        AuditResourceResponseJson response = new AuditResourceResponseJson((Page<AuditResource, String>)res, AuditResourceLookupRestResource::toJson, this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), uriInfo);
        return Response.ok((Object)response).build();
    }

    @GET
    @Path(value="/author")
    @Operation(summary="Lookup audit authors", tags={"author", "lookup"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditAuthorResponseJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response lookupAuthors(@Parameter(description="The number of records to skip") @Schema(minimum="0") @QueryParam(value="offset") @DefaultValue(value="0") int offset, @Parameter(description="Location of last result returned in format of ID of author. For making a request for page X, the value of this field can be obtained from pagingInfo->nextPageCursor in response for page X-1", example="9") @QueryParam(value="pageCursor") String cursor, @Parameter(description="The maximum number of records returned") @Schema(minimum="1", maximum="100000") @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Parameter(description="Search text") @QueryParam(value="search") String search, @Context UriInfo uriInfo) {
        List<AuditAuthor> commonAuthors = cursor != null || offset > 0 ? Collections.emptyList() : Stream.of(AuditAuthor.SYSTEM_AUTHOR, AuditAuthor.ANONYMOUS_AUTHOR, AuditAuthor.UNKNOWN_AUTHOR).filter(auditAuthor -> this.authorMatches(search, (AuditAuthor)auditAuthor)).collect(Collectors.toList());
        Page res = this.auditResourceLookupProvider.lookupAuditAuthor(search, new PageRequest.Builder().offset(offset).limit(Math.max(0, limit - commonAuthors.size())).cursor((Object)cursor).build());
        AuditAuthorResponseJson response = new AuditAuthorResponseJson(this.addCommonAuthors((Page<AuditAuthor, String>)res, commonAuthors), AuditEntitySerializer::toJson, this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), uriInfo);
        return Response.ok((Object)response).build();
    }

    private Page<AuditAuthor, String> addCommonAuthors(Page<AuditAuthor, String> page, List<AuditAuthor> commonAuthors) {
        List auditAuthors = Stream.concat(commonAuthors.stream(), page.getValues().stream().filter(auditAuthor -> auditAuthor.getName() != null).sorted(Comparator.comparing(auditAuthor -> auditAuthor.getName().toLowerCase()))).collect(Collectors.toList());
        return new Page.Builder(auditAuthors, page.getIsLastPage()).nextPageRequest((PageRequest)page.getNextPageRequest().orElse(null)).build();
    }

    @VisibleForTesting
    boolean authorMatches(String search, AuditAuthor auditAuthor) {
        if (auditAuthor.getName() == null) {
            return false;
        }
        String searchStr = (search == null ? "" : search).toLowerCase();
        return Collections.list(new StringTokenizer(auditAuthor.getName().toLowerCase(), DELIMS)).stream().map(str -> (String)str).anyMatch(str -> str.startsWith(searchStr));
    }
}

