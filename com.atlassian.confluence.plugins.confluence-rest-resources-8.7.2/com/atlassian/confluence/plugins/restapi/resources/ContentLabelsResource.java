/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Label$Prefix
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.ContentLabelService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
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
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.JavaType
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.ContentLabelService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/{id}/label")
public class ContentLabelsResource {
    private static final String DEFAULT_LIMIT = "200";
    private final ContentLabelService contentLabelService;

    public ContentLabelsResource(@ComponentImport ContentLabelService contentLabelService) {
        this.contentLabelService = contentLabelService;
    }

    @GET
    @PublicApi
    public PageResponse<Label> labels(@PathParam(value="id") ContentId contentId, @QueryParam(value="prefix") List<String> prefixes, @QueryParam(value="start") int offset, @QueryParam(value="limit") @DefaultValue(value="200") int limit, @Context UriInfo uriInfo) throws ServiceException {
        Collection<Label.Prefix> requestPrefixes = this.convertLabelPrefixStrings(prefixes);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, offset, limit);
        PageResponse labels = this.contentLabelService.getLabels(contentId, requestPrefixes, (PageRequest)pageRequest);
        return RestList.createRestList((PageRequest)pageRequest.copyWithLimits(labels), (PageResponse)labels);
    }

    @POST
    @PublicApi
    public PageResponse<Label> addLabels(@PathParam(value="id") ContentId contentId, String labels) throws ServiceException {
        return this.contentLabelService.addLabels(contentId, this.deserializeLabels(labels));
    }

    @DELETE
    @Path(value="/{label}")
    @PublicApi
    public Response deleteLabel(@PathParam(value="id") ContentId contentId, @PathParam(value="label") String label) throws ServiceException {
        this.contentLabelService.removeLabel(contentId, Label.builder((String)label).build());
        return Response.noContent().build();
    }

    @DELETE
    @PublicApi
    public Response deleteLabelWithQueryParam(@PathParam(value="id") ContentId contentId, @QueryParam(value="name") String label) throws ServiceException {
        return this.deleteLabel(contentId, label);
    }

    private List<Label> deserializeLabels(String labelsJson) throws ServiceException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            return (List)mapper.readValue(labelsJson, (JavaType)mapper.getTypeFactory().constructCollectionType(List.class, Label.class));
        }
        catch (IOException ex) {
            throw new BadRequestException("Could not parse Labels from " + labelsJson, (Throwable)ex);
        }
    }

    private Collection<Label.Prefix> convertLabelPrefixStrings(List<String> prefixes) throws ServiceException {
        if (prefixes == null || prefixes.isEmpty()) {
            return ImmutableList.copyOf((Object[])Label.Prefix.values());
        }
        try {
            return ImmutableList.copyOf((Iterable)Iterables.transform(prefixes, (Function)new Function<String, Label.Prefix>(){

                public Label.Prefix apply(String input) {
                    return Label.Prefix.valueOf((String)input);
                }
            }));
        }
        catch (Exception ex) {
            throw new BadRequestException("Could not convert label prefixes :" + prefixes, (Throwable)ex);
        }
    }
}

