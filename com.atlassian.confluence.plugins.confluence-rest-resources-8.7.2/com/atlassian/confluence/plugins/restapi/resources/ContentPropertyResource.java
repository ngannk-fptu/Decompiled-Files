/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Strings
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
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Optional;
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
@Path(value="/content/{id}/property")
@PublicApi
public class ContentPropertyResource {
    private static final String DEFAULT_LIMIT = "10";
    private final ContentPropertyService service;

    public ContentPropertyResource(@ComponentImport ContentPropertyService service) {
        this.service = service;
    }

    @POST
    public JsonContentProperty create(@PathParam(value="id") ContentId contentId, JsonContentProperty newProperty) throws ServiceException {
        newProperty = this.validateOrUpdateContentRef(contentId, newProperty);
        return this.service.create(newProperty);
    }

    @POST
    @Path(value="{key}")
    public JsonContentProperty create(@PathParam(value="id") ContentId contentId, @PathParam(value="key") String key, JsonContentProperty newProperty) throws ServiceException {
        newProperty = this.validateOrUpdateContentRef(contentId, newProperty);
        newProperty = this.validateOrUpdatePropertyKey(key, newProperty);
        return this.service.create(newProperty);
    }

    @GET
    public RestList<JsonContentProperty> findAll(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="10") int limit, @Context UriInfo uriInfo) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        PageResponse response = this.service.find(expansions).withContentId(contentId).fetchMany((PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest, (PageResponse)response);
    }

    @GET
    @Path(value="{key}")
    public JsonContentProperty findByKey(@PathParam(value="id") ContentId contentId, @PathParam(value="key") String key, @QueryParam(value="expand") String expand) throws ServiceException {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        Optional contentProperty = this.service.find(expansions).withContentId(contentId).withPropertyKey(key).fetch();
        return (JsonContentProperty)contentProperty.orElseThrow(ServiceExceptionSupplier.notFound((String)("No content property found with key: " + key)));
    }

    @PUT
    @Path(value="{key}")
    public JsonContentProperty update(@PathParam(value="id") ContentId contentId, @PathParam(value="key") String key, JsonContentProperty property) throws IOException, ServiceException {
        property = this.validateOrUpdateContentRef(contentId, property);
        Version version = (property = this.validateOrUpdatePropertyKey(key, property)).getVersion();
        if (version != null && version.getNumber() == 1) {
            return this.service.create(property);
        }
        return this.service.update(property);
    }

    @DELETE
    @Path(value="{key}")
    public Response delete(@PathParam(value="id") ContentId contentId, @PathParam(value="key") String key) throws ServiceException {
        JsonContentProperty property = JsonContentProperty.builder().content(Content.buildReference((ContentSelector)ContentSelector.fromId((ContentId)contentId))).key(key).build();
        this.service.delete(property);
        return Response.noContent().build();
    }

    private JsonContentProperty validateOrUpdatePropertyKey(String key, JsonContentProperty property) {
        if (Strings.isNullOrEmpty((String)property.getKey())) {
            property = JsonContentProperty.builder((JsonContentProperty)property).key(key).build();
        } else if (!key.equals(property.getKey())) {
            throw new BadRequestException("Path/value mismatch on key");
        }
        return property;
    }

    private JsonContentProperty validateOrUpdateContentRef(ContentId contentId, JsonContentProperty property) {
        if (!property.getContentRef().exists()) {
            property = JsonContentProperty.builder((JsonContentProperty)property).content(Content.buildReference((ContentSelector)ContentSelector.fromId((ContentId)contentId))).build();
        } else {
            ContentId foundId = Content.getSelector((Reference)property.getContentRef()).getId();
            if (!contentId.equals((Object)foundId)) {
                throw new BadRequestException(String.format("Path/value mismatch. Expected %s in property but found %s", contentId, foundId));
            }
        }
        return property;
    }
}

