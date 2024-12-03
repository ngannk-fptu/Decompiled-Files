/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.ChildContentService
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/{id}/child")
public class ChildContentResource {
    private final ChildContentService childContentService;

    public ChildContentResource(@ComponentImport ChildContentService childContentService) {
        this.childContentService = childContentService;
    }

    @GET
    public Map<ContentType, PageResponse<Content>> children(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="parentVersion") @DefaultValue(value="0") Integer parentVersion, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @Context UriInfo uriInfo) throws ServiceException {
        Expansions expansions = ExpansionsParser.parseAsExpansions((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        return this.childContentService.findContent(contentId, expansions.toArray()).withParentVersion(parentVersion.intValue()).fetchMappedByType((PageRequest)pageRequest);
    }

    @GET
    @Path(value="/{type}")
    public RestList<Content> childrenOfType(@PathParam(value="id") ContentId contentId, @PathParam(value="type") ContentType type, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="parentVersion") @DefaultValue(value="0") Integer parentVersion, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @Context UriInfo uriInfo) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        PageResponse children = this.childContentService.findContent(contentId, expansions).withParentVersion(parentVersion.intValue()).fetchMany(type, (PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest, (PageResponse)children);
    }

    @GET
    @Path(value="/comment")
    public RestList<Content> commentsOfContent(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="parentVersion") @DefaultValue(value="0") Integer parentVersion, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @QueryParam(value="location") Set<String> location, @QueryParam(value="depth") @DefaultValue(value="") String depth, @Context UriInfo uriInfo) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        Depth fetchDepth = depth.equalsIgnoreCase("all") ? Depth.ALL : Depth.ROOT;
        PageResponse children = this.childContentService.findContent(contentId, expansions).withDepth(fetchDepth).withLocation(location).withParentVersion(parentVersion.intValue()).fetchMany(ContentType.COMMENT, (PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest, (PageResponse)children);
    }
}

