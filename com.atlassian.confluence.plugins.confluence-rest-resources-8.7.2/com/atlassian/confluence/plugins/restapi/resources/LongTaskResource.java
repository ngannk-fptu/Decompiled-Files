/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.model.longtasks.LongTaskStatus
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.longtasks.LongTaskService
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
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.longtasks.LongTaskStatus;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.longtasks.LongTaskService;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(value="/longtask")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class LongTaskResource {
    private static final String DEFAULT_LIMIT = "100";
    private final LongTaskService longTaskService;

    public LongTaskResource(@ComponentImport LongTaskService longTaskService) {
        this.longTaskService = longTaskService;
    }

    @GET
    public RestList<LongTaskStatus> getTasks(@QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Context UriInfo uriInfo) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        PageResponse response = this.longTaskService.getAll((PageRequest)pageRequest, expansions);
        return RestList.createRestList((PageRequest)pageRequest, (PageResponse)response);
    }

    @GET
    @AnonymousAllowed
    @Path(value="/{id}")
    @PublicApi
    public LongTaskStatus getTask(@PathParam(value="id") String idStr, @QueryParam(value="expand") @DefaultValue(value="") String expand) {
        LongTaskId id = LongTaskId.deserialise((String)idStr);
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        Optional task = this.longTaskService.getStatus(id, expansions);
        return (LongTaskStatus)task.orElseThrow(ServiceExceptionSupplier.notFound((String)("No task found with id : " + id)));
    }

    public static Response submissionResponse(LongTaskSubmission taskSubmission) {
        return Response.status((Response.Status)Response.Status.ACCEPTED).entity((Object)taskSubmission).build();
    }
}

