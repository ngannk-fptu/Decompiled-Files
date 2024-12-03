/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintId
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.content.ContentBlueprintService
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.plugins.createcontent.experimental;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintId;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.content.ContentBlueprintService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@AnonymousSiteAccess
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/blueprint")
@ExperimentalApi
@PublicApi
public class ExperimentalContentBlueprintResource
extends AbstractRestResource {
    private final ContentBlueprintService contentBlueprintService;

    public ExperimentalContentBlueprintResource(PermissionManager permissionManager, SpaceManager spaceManager, ContentBlueprintService contentBlueprintService, AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.contentBlueprintService = contentBlueprintService;
    }

    @POST
    @Path(value="{blueprintId}/instance")
    public ContentBlueprintInstance createInstance(ContentBlueprintInstance instance, @PathParam(value="blueprintId") ContentBlueprintId blueprintId, @QueryParam(value="expand") @DefaultValue(value="body.storage,history,space,version,ancestors") String expand) throws ServiceException {
        this.checkNullEntity(instance);
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return this.contentBlueprintService.createInstance(instance, expansions);
    }
}

