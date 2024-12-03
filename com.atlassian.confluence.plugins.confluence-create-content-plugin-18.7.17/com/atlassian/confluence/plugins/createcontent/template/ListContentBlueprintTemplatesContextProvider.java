/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.createcontent.BlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.plugins.createcontent.services.PromotedBlueprintService;
import com.atlassian.confluence.plugins.createcontent.template.AbstractListBlueprintTemplatesContextProvider;
import com.atlassian.confluence.plugins.createcontent.template.PageTemplateGrouper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ListContentBlueprintTemplatesContextProvider
extends AbstractListBlueprintTemplatesContextProvider {
    private final ContextPathHolder contextPathHolder;
    private final I18nResolver i18nResolver;
    private final PageTemplateGrouper pageTemplateGrouper;
    private final BlueprintStateController blueprintStateController;
    private final PermissionManager permissionManager;
    private final PromotedBlueprintService promotedBlueprintService;

    public ListContentBlueprintTemplatesContextProvider(@ComponentImport ContextPathHolder contextPathHolder, @ComponentImport I18nResolver i18nResolver, PageTemplateGrouper pageTemplateGrouper, BlueprintStateController blueprintStateController, @ComponentImport PermissionManager permissionManager, PromotedBlueprintService promotedBlueprintService) {
        this.contextPathHolder = contextPathHolder;
        this.i18nResolver = i18nResolver;
        this.pageTemplateGrouper = pageTemplateGrouper;
        this.blueprintStateController = blueprintStateController;
        this.permissionManager = permissionManager;
        this.promotedBlueprintService = promotedBlueprintService;
    }

    @Override
    public Map<String, Object> getContextMap(Map<String, Object> context) {
        Object spaceObject = context.get("space");
        Space space = spaceObject instanceof Space ? (Space)spaceObject : null;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Map<UUID, BlueprintState> blueprintStateMap = this.blueprintStateController.getAllContentBlueprintState("system.create.dialog/content", currentUser, space);
        boolean isViewingSpaceTemplateAdmin = space != null;
        Collection<ContentBlueprint> contentBlueprints = this.pageTemplateGrouper.getSpaceContentBlueprints(space);
        Collection<ContentBlueprint> displayedBlueprints = this.getDisplayableBlueprints(contentBlueprints, blueprintStateMap, isViewingSpaceTemplateAdmin);
        Collection<ContentBlueprint> enabledBlueprints = this.getEnabledBlueprints(contentBlueprints, blueprintStateMap);
        context.put("enabledBlueprints", enabledBlueprints);
        context.put("contentBlueprints", displayedBlueprints);
        context.put("promotedBlueprints", this.promotedBlueprintService.getPromotedBlueprints(displayedBlueprints, space));
        context.put("contextPath", this.contextPathHolder.getContextPath());
        context.put("space", space);
        context.put("i18nResolver", this.i18nResolver);
        boolean canEnableDisableModules = this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, isViewingSpaceTemplateAdmin ? space : PermissionManager.TARGET_APPLICATION);
        context.put("canEnableDisableModules", canEnableDisableModules);
        return context;
    }
}

