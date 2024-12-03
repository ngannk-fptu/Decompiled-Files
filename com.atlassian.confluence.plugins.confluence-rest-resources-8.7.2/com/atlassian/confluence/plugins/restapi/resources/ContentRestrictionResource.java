/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.service.permissions.ContentRestrictionService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.service.permissions.ContentRestrictionService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@AnonymousAllowed
@LimitRequestSize
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/{id}/restriction")
public class ContentRestrictionResource {
    private final ContentRestrictionService service;

    public ContentRestrictionResource(@ComponentImport ContentRestrictionService service) {
        this.service = service;
    }

    @GET
    @Path(value="byOperation")
    @PublicApi
    public Map<OperationKey, ContentRestriction> byOperation(@PathParam(value="id") ContentId id, @QueryParam(value="expand") @DefaultValue(value="update.restrictions.user,read.restrictions.group,read.restrictions.user,update.restrictions.group") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return this.service.getRestrictionsGroupByOperation(id, expansions);
    }

    @GET
    @Path(value="byOperation/{operationKey}")
    @PublicApi
    public ContentRestriction forOperation(@PathParam(value="id") ContentId id, @PathParam(value="operationKey") OperationKey opKey, @QueryParam(value="expand") @DefaultValue(value="restrictions.user,restrictions.group") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        SimplePageRequest pageRequest = new SimplePageRequest(start, limit);
        return this.service.getRestrictionsForOperation(id, opKey, (PageRequest)pageRequest, expansions);
    }
}

