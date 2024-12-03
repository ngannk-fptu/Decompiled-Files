/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.NotFoundException
 *  com.atlassian.confluence.legacyapi.model.content.Label
 *  com.atlassian.confluence.legacyapi.model.content.Label$Prefix
 *  com.atlassian.confluence.legacyapi.service.content.TemplateService
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.Lists
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.ui.rest.content;

import com.atlassian.confluence.legacyapi.NotFoundException;
import com.atlassian.confluence.legacyapi.model.content.Label;
import com.atlassian.confluence.legacyapi.service.content.TemplateService;
import com.atlassian.confluence.ui.rest.content.LegacyLabelPrefixHelper;
import com.atlassian.confluence.ui.rest.content.LegacyRestHelper;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/template")
@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class LegacyTemplateResource {
    private final TemplateService templateService;

    public LegacyTemplateResource(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GET
    @Path(value="/{id}/labels")
    public Response getLabels(@PathParam(value="id") Long pageTemplateId, @QueryParam(value="prefix") List<String> prefixes) {
        Iterable labels;
        Iterable<Label.Prefix> requestPrefixes;
        try {
            requestPrefixes = LegacyLabelPrefixHelper.convertLabelPrefixStrings(prefixes);
        }
        catch (IllegalArgumentException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        try {
            labels = this.templateService.getLabels(pageTemplateId.longValue(), (Collection)Lists.newArrayList(requestPrefixes));
        }
        catch (NotFoundException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        Map<String, Object> result = LegacyRestHelper.createSuccessResultMap(labels);
        return Response.ok(result).build();
    }

    @POST
    @Path(value="/{id}/labels")
    public Response addLabels(@PathParam(value="id") Long pageTemplateId, List<Label> labels) {
        try {
            Iterable allLabelsOnEntity = this.templateService.addLabels(pageTemplateId.longValue(), labels);
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
    public Response deleteLabel(@PathParam(value="id") Long pageTemplateId, @PathParam(value="labelId") Long labelId) {
        try {
            this.templateService.removeLabel(pageTemplateId.longValue(), labelId.longValue());
            return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
        }
        catch (IllegalArgumentException e) {
            Map<String, Object> result = LegacyRestHelper.createFailureResultMap(e);
            return Response.ok(result).status(Response.Status.FORBIDDEN).build();
        }
    }
}

