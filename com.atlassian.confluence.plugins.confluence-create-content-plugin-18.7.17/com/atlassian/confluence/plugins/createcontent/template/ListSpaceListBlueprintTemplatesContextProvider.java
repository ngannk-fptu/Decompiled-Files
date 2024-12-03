/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.plugins.createcontent.template.AbstractListBlueprintTemplatesContextProvider;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListSpaceListBlueprintTemplatesContextProvider
extends AbstractListBlueprintTemplatesContextProvider {
    private final ContextPathHolder contextPathHolder;
    private final I18nResolver i18nResolver;
    private final SpaceBlueprintStateController spaceBlueprintStateController;
    private final SpaceBlueprintManager spaceBlueprintManager;
    private final PermissionManager permissionManager;

    public ListSpaceListBlueprintTemplatesContextProvider(@ComponentImport ContextPathHolder contextPathHolder, @ComponentImport I18nResolver i18nResolver, SpaceBlueprintStateController spaceBlueprintStateController, SpaceBlueprintManager spaceBlueprintManager, @ComponentImport PermissionManager permissionManager) {
        this.contextPathHolder = contextPathHolder;
        this.i18nResolver = i18nResolver;
        this.spaceBlueprintStateController = spaceBlueprintStateController;
        this.spaceBlueprintManager = spaceBlueprintManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public Map<String, Object> getContextMap(Map<String, Object> context) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Map<UUID, BlueprintState> blueprintStateMap = this.spaceBlueprintStateController.getAllSpaceBlueprintState("system.create.space.dialog/content", currentUser);
        List spaceBlueprints = this.spaceBlueprintManager.getAll();
        Collection displayableBlueprints = this.getDisplayableBlueprints(spaceBlueprints, blueprintStateMap, false);
        Collection enabledBlueprints = this.getEnabledBlueprints(spaceBlueprints, blueprintStateMap);
        ArrayList<SpaceBlueprint> nonSystemBlueprints = new ArrayList<SpaceBlueprint>();
        for (SpaceBlueprint displayableBlueprint : displayableBlueprints) {
            String moduleCompleteKey = displayableBlueprint.getModuleCompleteKey();
            if (moduleCompleteKey.equals("com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-space-blueprint") || moduleCompleteKey.equals("com.atlassian.confluence.plugins.confluence-create-content-plugin:create-personal-space-blueprint")) continue;
            nonSystemBlueprints.add(displayableBlueprint);
        }
        context.put("enabledBlueprints", enabledBlueprints);
        context.put("spaceBlueprints", nonSystemBlueprints);
        context.put("contextPath", this.contextPathHolder.getContextPath());
        context.put("i18nResolver", this.i18nResolver);
        boolean canEnableDisableModules = this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
        context.put("canEnableDisableModules", canEnableDisableModules);
        return context;
    }
}

