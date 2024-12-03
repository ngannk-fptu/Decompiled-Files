/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateType
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
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
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.restapi.experimental.resources;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.template.ContentTemplateService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
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
import org.apache.commons.lang3.StringUtils;

@ExperimentalApi
@AnonymousAllowed
@LimitRequestSize(value=0x500000L)
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/template")
public class TemplateResource {
    private static final String DEFAULT_EXPANSIONS = "body";
    private final ContentTemplateService contentTemplateService;

    public TemplateResource(@ComponentImport ContentTemplateService contentTemplateService) {
        this.contentTemplateService = contentTemplateService;
    }

    @GET
    @Path(value="blueprint")
    public PageResponse<ContentTemplate> getBlueprintTemplates(@QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @QueryParam(value="expand") @DefaultValue(value="") String expand, @Context UriInfo uriInfo) {
        return this.getTemplates(ContentTemplateType.BLUEPRINT, spaceKey, start, limit, expand, uriInfo);
    }

    @GET
    @Path(value="page")
    public PageResponse<ContentTemplate> getContentTemplates(@QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="25") int limit, @QueryParam(value="expand") @DefaultValue(value="") String expand, @Context UriInfo uriInfo) {
        return this.getTemplates(ContentTemplateType.PAGE, spaceKey, start, limit, expand, uriInfo);
    }

    private PageResponse<ContentTemplate> getTemplates(ContentTemplateType templateType, String spaceKey, int start, int limit, String expand, UriInfo uriInfo) {
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        Optional space = StringUtils.isNotEmpty((CharSequence)spaceKey) ? Optional.of(Space.builder().key(spaceKey).build()) : Optional.empty();
        PageResponse result = this.contentTemplateService.getTemplates(templateType, space, (PageRequest)pageRequest, expansions);
        return RestList.createRestList((PageRequest)pageRequest.copyWithLimits(result), (PageResponse)result);
    }

    @GET
    @Path(value="{contentTemplateId}")
    public ContentTemplate getContentTemplate(@PathParam(value="contentTemplateId") ContentTemplateId id, @QueryParam(value="expand") @DefaultValue(value="body") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return this.contentTemplateService.getTemplate(id, expansions);
    }

    @POST
    public ContentTemplate createContentTemplate(ContentTemplate contentTemplate, @QueryParam(value="expand") @DefaultValue(value="body") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return this.contentTemplateService.create(contentTemplate, expansions);
    }

    @PUT
    public ContentTemplate updateContentTemplate(ContentTemplate contentTemplate, @QueryParam(value="expand") @DefaultValue(value="body") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return this.contentTemplateService.update(contentTemplate, expansions);
    }

    @DELETE
    @Path(value="/{contentTemplateId}")
    public Response removeTemplate(@PathParam(value="contentTemplateId") ContentTemplateId contentTemplateId) {
        this.contentTemplateService.delete(contentTemplateId);
        return Response.noContent().build();
    }

    @POST
    @Path(value="/page/{contentTemplateId}/instance")
    public ContentBlueprintInstance createInstance(ContentBlueprintInstance contentBlueprintInstance, @PathParam(value="contentTemplateId") ContentTemplateId contentTemplateId, @QueryParam(value="expand") @DefaultValue(value="body") String expand) {
        this.validateContentTemplateIdMatchesInstance(contentTemplateId, contentBlueprintInstance);
        return this.contentTemplateService.createInstance(contentBlueprintInstance, ExpansionsParser.parse((String)expand));
    }

    private void validateContentTemplateIdMatchesInstance(ContentTemplateId contentTemplateId, ContentBlueprintInstance contentBlueprintInstance) {
        Optional templateId = contentBlueprintInstance.getContentBlueprintSpec().contentTemplateId();
        if (templateId.isPresent() && !((ContentTemplateId)templateId.get()).equals((Object)contentTemplateId)) {
            throw new BadRequestException("Template Id posted in instance doesn't match template id set on path");
        }
    }
}

