/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentBlueprintService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentBlueprintService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@AnonymousAllowed
@LimitRequestSize(value=0x500000L)
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/blueprint")
@PublicApi
public class ContentBlueprintResource {
    private final ContentBlueprintService contentBlueprintService;

    public ContentBlueprintResource(@ComponentImport ContentBlueprintService contentBlueprintService) {
        this.contentBlueprintService = contentBlueprintService;
    }

    @POST
    @Path(value="/instance/{draftId}")
    public Content publishLegacyDraft(@PathParam(value="draftId") ContentId draftId, Content content, @QueryParam(value="status") @DefaultValue(value="draft") ContentStatus status, @QueryParam(value="expand") @DefaultValue(value="body.storage,history,space,version,ancestors") String expand) throws ServiceException {
        return this.publish(draftId, content, status, expand);
    }

    @PUT
    @Path(value="/instance/{draftId}")
    public Content publishSharedDraft(@PathParam(value="draftId") ContentId draftId, Content content, @QueryParam(value="status") @DefaultValue(value="draft") ContentStatus status, @QueryParam(value="expand") @DefaultValue(value="body.storage,history,space,version,ancestors") String expand) throws ServiceException {
        return this.publish(draftId, content, status, expand);
    }

    private Content publish(ContentId draftId, Content content, ContentStatus status, String expand) {
        if (!status.equals((Object)ContentStatus.DRAFT)) {
            throw new NotImplementedServiceException("Received status '" + status + "' but only 'draft' status is currently supported");
        }
        ContentId idInObject = content.getId();
        if (idInObject == null) {
            content = Content.builder((Content)content).id(draftId).build();
        } else if (!idInObject.equals((Object)draftId)) {
            throw new BadRequestException("Draft ID doesn't match content ID.");
        }
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return this.contentBlueprintService.publishInstance(content, expansions);
    }
}

