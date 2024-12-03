/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.service.content.ContentVersionService
 *  com.atlassian.confluence.api.service.content.VersionRestoreParameters
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.confluence.rest.serialization.RestData
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.experimental.resources;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.service.content.ContentVersionService;
import com.atlassian.confluence.api.service.content.VersionRestoreParameters;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.confluence.rest.serialization.RestData;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@ExperimentalApi
@AnonymousAllowed
@LimitRequestSize(value=0x500000L)
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/{id}/version")
public class ContentVersionResource {
    private final ContentVersionService contentVersionService;

    public ContentVersionResource(@ComponentImport ContentVersionService contentVersionService) {
        this.contentVersionService = contentVersionService;
    }

    @GET
    public PageResponse<Version> getContentHistory(@PathParam(value="id") ContentId contentId, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="200") int limit, @QueryParam(value="expand") @DefaultValue(value="") String expand, @Context UriInfo uriInfo) {
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        PageResponse versions = this.contentVersionService.find(expansions).withId(contentId).fetchMany((PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest.copyWithLimits(versions), (PageResponse)versions);
    }

    @GET
    @Path(value="{versionNumber}")
    public Version getContentVersion(@PathParam(value="id") ContentId contentId, @PathParam(value="versionNumber") int versionNumber, @QueryParam(value="expand") @DefaultValue(value="content") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return (Version)this.contentVersionService.find(expansions).withIdAndVersion(contentId, versionNumber).fetchOrNull();
    }

    @DELETE
    @Path(value="/{versionId}")
    public void deleteContentHistory(@PathParam(value="id") ContentId contentId, @PathParam(value="versionId") int versionNumber) {
        this.contentVersionService.delete(contentId, versionNumber);
    }

    @POST
    public Version restoreContentHistory(@PathParam(value="id") ContentId contentId, RestData restData, @QueryParam(value="expand") @DefaultValue(value="content") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        if (!restData.getOperationKey().equals((Object)OperationKey.RESTORE)) {
            throw new BadRequestException(String.format("'%s' is not a supported operation on a Content Version. Supported operations are: 'RESTORE'", restData.getOperationKey()));
        }
        return this.contentVersionService.restore(contentId, VersionRestoreParameters.fromMap((Map)restData.getParams()), expansions);
    }
}

