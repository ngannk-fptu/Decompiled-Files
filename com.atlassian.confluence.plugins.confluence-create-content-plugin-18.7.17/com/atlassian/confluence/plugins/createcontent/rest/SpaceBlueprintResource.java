/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.UuidBackedAo;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.api.services.SpaceBlueprintService;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.plugins.createcontent.rest.SpaceBlueprintEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.BlueprintSpaceEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintSpaceRestEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreatePersonalSpaceRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintSpace;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/space-blueprint")
public class SpaceBlueprintResource
extends AbstractRestResource {
    public static final String PARAM_KEY = "key";
    public static final String PARAM_ID = "id";
    private final SpaceBlueprintService spaceBlueprintService;
    private final SpaceBlueprintManager spaceBlueprintManager;
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final PluginPageTemplateHelper pageTemplateHelper;
    private final PageTemplateManager pageTemplateManager;
    private final ActiveObjects activeObjects;
    private final SettingsManager settingsManager;

    public SpaceBlueprintResource(SpaceBlueprintService spaceBlueprintService, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @Qualifier(value="spaceBlueprintManager") SpaceBlueprintManager spaceBlueprintManager, ContentTemplateRefManager contentTemplateRefManager, PluginPageTemplateHelper pageTemplateHelper, @ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport ActiveObjects activeObjects, @ComponentImport SettingsManager settingsManager, @ComponentImport AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.spaceBlueprintService = spaceBlueprintService;
        this.spaceBlueprintManager = spaceBlueprintManager;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.pageTemplateHelper = pageTemplateHelper;
        this.pageTemplateManager = pageTemplateManager;
        this.activeObjects = activeObjects;
        this.settingsManager = settingsManager;
    }

    @POST
    @Path(value="create-space")
    @Consumes(value={"application/json", "application/xml"})
    public BlueprintSpaceEntity createSpace(CreateBlueprintSpaceRestEntity entity) throws BlueprintIllegalArgumentException {
        BlueprintSpace space = this.spaceBlueprintService.createSpace(entity, this.getUser());
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        return new BlueprintSpaceEntity(space, baseUrl);
    }

    @POST
    @Path(value="create-personal-space")
    @Consumes(value={"application/json", "application/xml"})
    public BlueprintSpaceEntity createPersonalSpace(CreatePersonalSpaceRestEntity entity) {
        this.checkNullEntity(entity);
        BlueprintSpace space = this.spaceBlueprintService.createPersonalSpace(entity, this.getUser());
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        return new BlueprintSpaceEntity(space, baseUrl);
    }

    @GET
    @Path(value="byKey/{key}")
    public SpaceBlueprint getByModuleCompleteKey(@PathParam(value="key") String moduleCompleteKey) {
        this.checkAdminPermission();
        this.checkEmptyParameter(moduleCompleteKey, PARAM_KEY);
        return (SpaceBlueprint)this.spaceBlueprintManager.getCloneByModuleCompleteKey(new ModuleCompleteKey(moduleCompleteKey));
    }

    @GET
    @Path(value="get/{id}")
    public SpaceBlueprint get(@PathParam(value="id") UUID id) {
        this.checkAdminPermission();
        this.checkNullParameter(id, PARAM_ID);
        return (SpaceBlueprint)this.spaceBlueprintManager.getById(id);
    }

    @GET
    @Path(value="list")
    public List<SpaceBlueprint> getAllSpaceBlueprints() {
        this.checkAdminPermission();
        return this.spaceBlueprintManager.getAll();
    }

    @PUT
    @ReadOnlyAccessAllowed
    @Path(value="save")
    @Consumes(value={"application/json", "application/xml"})
    public SpaceBlueprint save(SpaceBlueprint blueprint) {
        this.checkAdminPermission();
        return this.spaceBlueprintManager.update(blueprint);
    }

    @POST
    @ReadOnlyAccessAllowed
    @Path(value="create")
    @Consumes(value={"application/json", "application/xml"})
    public SpaceBlueprint create(@Nonnull SpaceBlueprintEntity entity) {
        UuidBackedAo newHomePage;
        this.checkAdminPermission();
        UUID homePageId = entity.getHomePageId();
        Long homePageTemplateId = entity.getHomePageTemplateId();
        if (homePageId != null) {
            ContentTemplateRef contentTemplateRef = (ContentTemplateRef)this.contentTemplateRefManager.getById(homePageId);
            if (contentTemplateRef == null) {
                throw new ResourceException("The specified homePageId was not found", Response.Status.NOT_FOUND, ResourceErrorType.NOT_FOUND_CONTENT_TEMPLATE_REF, (Object)homePageId);
            }
            newHomePage = this.createContentTemplateRefAo(contentTemplateRef);
        } else {
            newHomePage = homePageTemplateId != null && homePageTemplateId > 0L ? this.createContentTemplateRefAo(new ContentTemplateRef(null, homePageTemplateId, null, null, false, null)) : null;
        }
        UUID newHomePageId = newHomePage != null ? UUID.fromString(newHomePage.getUuid()) : null;
        return this.spaceBlueprintManager.create(entity.getSpaceBlueprint(), newHomePageId);
    }

    private ContentTemplateRefAo createContentTemplateRefAo(@Nonnull ContentTemplateRef contentTemplateRef) {
        return (ContentTemplateRefAo)this.activeObjects.executeInTransaction(() -> this.doCreateContentTemplateRefAo(contentTemplateRef, null));
    }

    @Nonnull
    private ContentTemplateRefAo doCreateContentTemplateRefAo(@Nonnull ContentTemplateRef contentTemplateRef, @Nullable ContentTemplateRefAo parent) {
        PageTemplate pageTemplate = this.pageTemplateHelper.getPageTemplate(contentTemplateRef);
        pageTemplate.setId(0L);
        this.pageTemplateManager.savePageTemplate(pageTemplate, null);
        ArrayList<ContentTemplateRef> children = new ArrayList<ContentTemplateRef>(contentTemplateRef.getChildren());
        contentTemplateRef.getChildren().clear();
        ContentTemplateRefAo result = (ContentTemplateRefAo)this.contentTemplateRefManager.createAo(contentTemplateRef);
        result.setParent(parent);
        result.setTemplateId(pageTemplate.getId());
        result.save();
        for (ContentTemplateRef child : children) {
            this.doCreateContentTemplateRefAo(child, result);
        }
        return result;
    }

    @DELETE
    @ReadOnlyAccessAllowed
    @Path(value="deleteAll")
    public int deleteAll() {
        this.checkAdminPermission();
        return this.spaceBlueprintManager.deleteAll();
    }
}

