/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty$SpacePropertyBuilder
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.content.SpacePropertyService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.content.SpacePropertyService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@AnonymousAllowed
@LimitRequestSize
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/space/{spaceKey}/property")
@PublicApi
public class SpacePropertyResource {
    private static final String DEFAULT_LIMIT = "10";
    private final SpacePropertyService service;

    public SpacePropertyResource(@ComponentImport SpacePropertyService service) {
        this.service = service;
    }

    @POST
    public JsonSpaceProperty create(@PathParam(value="spaceKey") String spaceKey, JsonSpaceProperty newProperty) throws ServiceException {
        JsonSpaceProperty propertyWithSpace = this.verifyAndUpdateWithSpace(spaceKey, newProperty);
        return this.service.create(propertyWithSpace);
    }

    @POST
    @Path(value="{key}")
    public JsonSpaceProperty create(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="key") String propertyKey, JsonSpaceProperty newProperty) throws ServiceException {
        JsonSpaceProperty propertyWithSpace = this.verifyAndUpdateWithSpace(spaceKey, newProperty);
        JsonSpaceProperty propertyWithKey = this.verifyAndUpdateWithKey(propertyKey, propertyWithSpace);
        return this.service.create(propertyWithKey);
    }

    @GET
    public PageResponse<JsonSpaceProperty> get(@PathParam(value="spaceKey") String spaceKey, @QueryParam(value="expand") @DefaultValue(value="version") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="10") int limit, @Context UriInfo uriInfo) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        PageResponse response = this.service.find(expansions).withSpaceKey(spaceKey).fetchMany((PageRequest)pageRequest);
        return RestList.newRestList().pageRequest((PageRequest)pageRequest).results(response).build();
    }

    @GET
    @Path(value="{key}")
    public JsonSpaceProperty get(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="key") String propertyKey, @QueryParam(value="expand") @DefaultValue(value="version") String expand) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return (JsonSpaceProperty)this.service.find(expansions).withSpaceKey(spaceKey).withPropertyKey(propertyKey).fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("No space property found with key: " + propertyKey)));
    }

    @PUT
    @Path(value="{key}")
    public JsonSpaceProperty update(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="key") String propertyKey, JsonSpaceProperty property) throws ServiceException {
        JsonSpaceProperty propertyWithSpace = this.verifyAndUpdateWithSpace(spaceKey, property);
        JsonSpaceProperty propertyWithKey = this.verifyAndUpdateWithKey(propertyKey, propertyWithSpace);
        return this.service.update(propertyWithKey);
    }

    @DELETE
    @Path(value="{key}")
    public Response delete(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="key") String propertyKey) throws ServiceException {
        JsonSpaceProperty property = ((JsonSpaceProperty.SpacePropertyBuilder)JsonSpaceProperty.builder().space(Space.builder().key(spaceKey).build()).key(propertyKey)).build();
        this.service.delete(property);
        return Response.noContent().build();
    }

    private JsonSpaceProperty verifyAndUpdateWithKey(String pathKey, JsonSpaceProperty newProperty) {
        JsonSpaceProperty propertyWithKey = newProperty.getKey() != null ? newProperty : ((JsonSpaceProperty.SpacePropertyBuilder)JsonSpaceProperty.builder((JsonSpaceProperty)newProperty).key(pathKey)).build();
        if (!propertyWithKey.getKey().equals(pathKey)) {
            throw new BadRequestException("Path/value mismatch on key: " + pathKey + " vs. " + propertyWithKey.getKey());
        }
        return propertyWithKey;
    }

    private JsonSpaceProperty verifyAndUpdateWithSpace(String pathSpaceKey, JsonSpaceProperty newProperty) {
        JsonSpaceProperty propertyWithSpace = newProperty.getSpaceRef().existsAndExpanded() ? newProperty : JsonSpaceProperty.builder((JsonSpaceProperty)newProperty).space(Reference.to((Object)Space.builder().key(pathSpaceKey).build())).build();
        if (!propertyWithSpace.getSpace().getKey().equals(pathSpaceKey)) {
            throw new BadRequestException("Path/value mismatch on spaceKey: " + pathSpaceKey + " vs. " + propertyWithSpace.getSpace().getKey());
        }
        return propertyWithSpace;
    }
}

