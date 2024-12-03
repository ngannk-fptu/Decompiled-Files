/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.NotFoundException
 *  com.atlassian.confluence.legacyapi.NotPermittedException
 *  com.atlassian.confluence.legacyapi.model.PartialList
 *  com.atlassian.confluence.legacyapi.model.content.ContentBody
 *  com.atlassian.confluence.legacyapi.model.content.ContentRepresentation
 *  com.atlassian.confluence.legacyapi.model.content.ContentType
 *  com.atlassian.confluence.legacyapi.model.content.Label
 *  com.atlassian.confluence.legacyapi.model.content.Label$Prefix
 *  com.atlassian.confluence.legacyapi.service.Expansion
 *  com.atlassian.confluence.legacyapi.service.content.ContentService
 *  com.atlassian.confluence.legacyapi.service.content.InvalidRepresentationException
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.Lists
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
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.ui.rest.content;

import com.atlassian.confluence.legacyapi.NotFoundException;
import com.atlassian.confluence.legacyapi.NotPermittedException;
import com.atlassian.confluence.legacyapi.model.PartialList;
import com.atlassian.confluence.legacyapi.model.content.ContentBody;
import com.atlassian.confluence.legacyapi.model.content.ContentRepresentation;
import com.atlassian.confluence.legacyapi.model.content.ContentType;
import com.atlassian.confluence.legacyapi.model.content.Label;
import com.atlassian.confluence.legacyapi.service.Expansion;
import com.atlassian.confluence.legacyapi.service.content.ContentService;
import com.atlassian.confluence.legacyapi.service.content.InvalidRepresentationException;
import com.atlassian.confluence.ui.rest.LegacyExpansionsParser;
import com.atlassian.confluence.ui.rest.content.LegacyLabelPrefixHelper;
import com.atlassian.confluence.ui.rest.content.LegacyRestHelper;
import com.atlassian.fugue.Option;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/content")
@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class LegacyContentResource {
    private final ContentService contentService;

    public LegacyContentResource(ContentService contentService) {
        this.contentService = contentService;
    }

    @GET
    @Path(value="/{id}")
    public Response getContent(@PathParam(value="id") Long contentId, @QueryParam(value="expand") @DefaultValue(value="") String expand) {
        Expansion[] expansions = LegacyExpansionsParser.parse(expand);
        Option content = this.contentService.findById(contentId.longValue(), expansions);
        if (content.isEmpty()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)content.get()).build();
    }

    @GET
    @Path(value="/{id}/versions/previous")
    public Response getPreviousContent(@PathParam(value="id") Long contentId, @QueryParam(value="expand") @DefaultValue(value="") String expand) {
        Expansion[] expansions = LegacyExpansionsParser.parse(expand);
        Option content = this.contentService.findPreviousVersion(contentId.longValue(), expansions);
        if (content.isEmpty()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)content.get()).build();
    }

    @GET
    @Path(value="/{id}/versions/next")
    public Response getNextContent(@PathParam(value="id") Long contentId, @QueryParam(value="expand") @DefaultValue(value="") String expand) {
        Expansion[] expansions = LegacyExpansionsParser.parse(expand);
        Option content = this.contentService.findNextVersion(contentId.longValue(), expansions);
        if (content.isEmpty()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)content.get()).build();
    }

    @GET
    @Path(value="/{id}/subcontent/{type}")
    public Response getSubContent(@PathParam(value="id") Long contentId, @PathParam(value="type") String subContentType, @QueryParam(value="thread") @DefaultValue(value="false") boolean threaded, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="max") @DefaultValue(value="50") int max, @QueryParam(value="expand") @DefaultValue(value="") String expand) {
        if (!threaded) {
            PartialList subContent = this.contentService.findSubContent(contentId.longValue(), ContentType.forName((String)subContentType), start, max, LegacyExpansionsParser.parse(expand));
            return Response.ok((Object)subContent).build();
        }
        PartialList subContent = this.contentService.findSubContentTree(contentId.longValue(), ContentType.forName((String)subContentType), LegacyExpansionsParser.parse(expand));
        return Response.ok((Object)subContent).build();
    }

    @GET
    @Path(value="/{id}/body/{representation}")
    public Response getBody(@PathParam(value="id") Long contentId, @PathParam(value="representation") String representation) {
        ContentRepresentation contentRepresentation;
        try {
            contentRepresentation = ContentRepresentation.valueOf((String)representation);
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        Option content = this.contentService.findById(contentId.longValue(), new Expansion[0]);
        if (content.isEmpty()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.ok((Object)this.contentService.getContentBody(contentId.longValue(), contentRepresentation)).build();
        }
        catch (InvalidRepresentationException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path(value="/{id}/body/{representation}")
    public Response updateBody(@PathParam(value="id") Long contentId, @PathParam(value="representation") String representation, ContentBody body) {
        ContentRepresentation contentRepresentation;
        try {
            contentRepresentation = ContentRepresentation.valueOf((String)representation);
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        Option content = this.contentService.findById(contentId.longValue(), new Expansion[0]);
        if (content.isEmpty()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.ok((Object)this.contentService.updateContentBody(contentId.longValue(), contentRepresentation, body)).build();
        }
        catch (InvalidRepresentationException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path(value="/{id}/labels")
    public Response getLabels(@PathParam(value="id") Long contentId, @QueryParam(value="prefix") List<String> prefixes) {
        Iterable labels;
        Iterable<Label.Prefix> requestPrefixes;
        try {
            requestPrefixes = LegacyLabelPrefixHelper.convertLabelPrefixStrings(prefixes);
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        try {
            labels = this.contentService.getLabels(contentId.longValue(), (Collection)Lists.newArrayList(requestPrefixes));
        }
        catch (NotFoundException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        Map<String, Object> result = LegacyRestHelper.createSuccessResultMap(labels);
        return Response.ok(result).build();
    }

    @POST
    @Path(value="/{id}/labels")
    public Response addLabels(@PathParam(value="id") Long contentId, List<Label> labels) {
        try {
            Iterable allLabelsOnEntity = this.contentService.addLabels(contentId.longValue(), labels);
            Map<String, Object> result = LegacyRestHelper.createSuccessResultMap(allLabelsOnEntity);
            return Response.ok(result).build();
        }
        catch (IllegalArgumentException e) {
            Map<String, Object> result = LegacyRestHelper.createFailureResultMap(e);
            return Response.ok(result).status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @Path(value="/{id}/label/{labelId}")
    public Response deleteLabel(@PathParam(value="id") Long contentId, @PathParam(value="labelId") Long labelId) {
        try {
            this.contentService.removeLabel(contentId.longValue(), labelId.longValue());
            return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
        }
        catch (IllegalArgumentException e) {
            Map<String, Object> result = LegacyRestHelper.createFailureResultMap(e);
            return Response.ok(result).status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path(value="/labels/validate")
    public Response validateLabels(List<Label> labels) {
        try {
            Iterable allLabelsOnEntity = this.contentService.validateLabels(labels);
            Map<String, Object> result = LegacyRestHelper.createSuccessResultMap(allLabelsOnEntity);
            return Response.ok(result).build();
        }
        catch (IllegalArgumentException e) {
            Map<String, Object> result = LegacyRestHelper.createFailureResultMap(e);
            return Response.ok(result).status(Response.Status.FORBIDDEN).build();
        }
    }
}

