/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.plugins.createcontent.services.PromotedBlueprintService;
import com.atlassian.confluence.plugins.createcontent.services.PromotedTemplateService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path(value="/promotion")
public class PromotedEntityResource
extends AbstractRestResource {
    public static final String PARAM_BLUEPRINT_ID = "blueprintId";
    public static final String PARAM_SPACE_KEY = "spaceKey";
    public static final String PARAM_ID = "id";
    private final PromotedBlueprintService promotedBlueprintService;
    private final PromotedTemplateService promotedTemplateService;

    public PromotedEntityResource(PromotedBlueprintService promotedBlueprintService, PromotedTemplateService promotedTemplateService, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.promotedBlueprintService = promotedBlueprintService;
        this.promotedTemplateService = promotedTemplateService;
    }

    @Path(value="promote-blueprint/{blueprintId}")
    @PUT
    public boolean promoteBlueprint(@PathParam(value="blueprintId") String blueprintId, @QueryParam(value="spaceKey") String spaceKey) throws BlueprintIllegalArgumentException {
        return this.promotedBlueprintService.promoteBlueprint(blueprintId, spaceKey);
    }

    @Path(value="promote-blueprint/{blueprintId}")
    @DELETE
    public boolean demoteBlueprint(@PathParam(value="blueprintId") String blueprintId, @QueryParam(value="spaceKey") String spaceKey) throws BlueprintIllegalArgumentException {
        return this.promotedBlueprintService.demoteBlueprint(blueprintId, spaceKey);
    }

    @Path(value="promote-template/{id}")
    @PUT
    public void promoteTemplate(@PathParam(value="id") long templateId, @QueryParam(value="spaceKey") String spaceKey) throws BlueprintIllegalArgumentException {
        this.promotedTemplateService.promoteTemplate(templateId, spaceKey);
    }

    @Path(value="promote-template/{id}")
    @DELETE
    public void demoteTemplate(@PathParam(value="id") long templateId, @QueryParam(value="spaceKey") String spaceKey) throws BlueprintIllegalArgumentException {
        this.promotedTemplateService.demoteTemplate(templateId, spaceKey);
    }

    @Path(value="promote-template/")
    @GET
    public Collection<Long> getPromotedTemplates(@QueryParam(value="spaceKey") String spaceKey) {
        this.checkEmptyParameter(spaceKey, PARAM_SPACE_KEY);
        Space space = this.getAndCheckSpace(spaceKey);
        return this.promotedTemplateService.getPromotedTemplates(space);
    }
}

