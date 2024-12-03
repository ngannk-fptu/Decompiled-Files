/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintCleaner;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/admin")
public class BlueprintsAdminResource
extends AbstractRestResource {
    private final SpaceBlueprintManager spaceBlueprintManager;
    private final ContentBlueprintManager contentBlueprintManager;
    private final ContentTemplateRefManager contentTemplateManager;
    private final PluginAccessor pluginAccessor;
    private final ContentBlueprintCleaner contentBlueprintCleaner;

    public BlueprintsAdminResource(@Qualifier(value="spaceBlueprintManager") SpaceBlueprintManager spaceBlueprintManager, ContentBlueprintManager contentBlueprintManager, ContentTemplateRefManager contentTemplateManager, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport AccessModeService accessModeService, ContentBlueprintCleaner contentBlueprintCleaner) {
        super(permissionManager, spaceManager, accessModeService);
        this.spaceBlueprintManager = spaceBlueprintManager;
        this.contentBlueprintManager = contentBlueprintManager;
        this.contentTemplateManager = contentTemplateManager;
        this.pluginAccessor = pluginAccessor;
        this.contentBlueprintCleaner = contentBlueprintCleaner;
    }

    @POST
    @ReadOnlyAccessAllowed
    @Path(value="refreshAoTables")
    @Consumes(value={"application/json"})
    public int refreshAoTables() {
        this.checkAdminPermission();
        this.spaceBlueprintManager.deleteAll();
        this.contentBlueprintManager.deleteAll();
        this.contentTemplateManager.deleteAll();
        int refreshed = this.refreshContentBlueprints();
        return refreshed += this.spaceBlueprintManager.getAll().size();
    }

    @DELETE
    @ReadOnlyAccessAllowed
    @Path(value="cleanup")
    public Response cleanUp() {
        this.checkAdminPermission();
        int totalSpaces = this.contentBlueprintCleaner.cleanUp();
        return totalSpaces > -1 ? Response.ok().entity((Object)totalSpaces).build() : Response.serverError().build();
    }

    private int refreshContentBlueprints() {
        List moduleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(BlueprintModuleDescriptor.class);
        for (BlueprintModuleDescriptor moduleDescriptor : moduleDescriptors) {
            this.contentBlueprintManager.getPluginBackedContentBlueprint(moduleDescriptor.getBlueprintKey(), null);
        }
        return moduleDescriptors.size();
    }
}

