/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.service.WelcomeMessageService
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder
 *  com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder$RecordedResources
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.dashboard;

import com.atlassian.confluence.content.service.WelcomeMessageService;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.confluence.plugins.dashboard.DashboardContext;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.user.User;

public class DefaultDashboardContext
implements DashboardContext {
    private static final ModuleCompleteKey WELCOME_MESSAGE_MODULE_COMPLETE_KEY = new ModuleCompleteKey("com.atlassian.confluence.plugins.system-templates:welcome-message");
    private static final PluginTemplateReference WELCOME_MESSAGE_PLUGIN_TEMPLATE_REFERENCE = PluginTemplateReference.systemTemplateReference((ModuleCompleteKey)WELCOME_MESSAGE_MODULE_COMPLETE_KEY);
    private final ConfluenceWebResourceManager confluenceWebResourceManager;
    private final DarkFeaturesManager darkFeaturesManager;
    private final WelcomeMessageService welcomeMessageService;
    private final PageTemplateManager pageTemplateManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final WebResourceDependenciesRecorder webResourceDependenciesRecorder;

    public DefaultDashboardContext(ConfluenceWebResourceManager confluenceWebResourceManager, DarkFeaturesManager darkFeaturesManager, WelcomeMessageService welcomeMessageService, PageTemplateManager pageTemplateManager, PermissionManager permissionManager, SpaceManager spaceManager, ConfluenceAccessManager confluenceAccessManager, WebResourceDependenciesRecorder webResourceDependenciesRecorder) {
        this.confluenceWebResourceManager = confluenceWebResourceManager;
        this.darkFeaturesManager = darkFeaturesManager;
        this.welcomeMessageService = welcomeMessageService;
        this.pageTemplateManager = pageTemplateManager;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.webResourceDependenciesRecorder = webResourceDependenciesRecorder;
    }

    @Override
    public String getWelcomeMessage() {
        return this.welcomeMessageService.getWelcomeMessage();
    }

    @Override
    public String getEditWelcomePageUrl() {
        PageTemplate template = this.pageTemplateManager.getGlobalPageTemplate("Default Welcome Message");
        String key = "com.atlassian.confluence.plugins.system-templates:welcome-message";
        return template != null ? "pages/templates2/editpagetemplate.action?entityId=" + template.getId() : "plugins/createcontent/edit-template.action?pluginKey=" + key.replace(":", "&moduleKey=");
    }

    @Override
    public boolean customPageTemplateExists() {
        return this.pageTemplateManager.getPageTemplate(WELCOME_MESSAGE_PLUGIN_TEMPLATE_REFERENCE) != null;
    }

    @Override
    public boolean shouldShowWelcomeMessageEditLink() {
        return this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get()) && !this.customPageTemplateExists();
    }

    @Override
    public boolean shouldShowEditButton() {
        return this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get()) && this.customPageTemplateExists();
    }

    @Override
    public boolean showOnboarding() {
        return !AuthenticatedUserThreadLocal.isAnonymousUser() && !this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("dashboard.onboarding.disabled");
    }

    @Override
    public boolean shouldDisplayCreateButton() {
        if (!AuthenticatedUserThreadLocal.isAnonymousUser()) {
            SpacesQuery spacesQuery = SpacesQuery.newQuery().forUser((User)AuthenticatedUserThreadLocal.get()).withSpaceStatus(SpaceStatus.CURRENT).withPermission("EDITSPACE").unsorted().build();
            return !this.spaceManager.getSpaces(spacesQuery).getPage(0, 1).isEmpty();
        }
        return false;
    }

    @Override
    public String getJsResources() {
        return this.confluenceWebResourceManager.getJsResources();
    }

    @Override
    public String getCssResources() {
        return this.confluenceWebResourceManager.getCssResources();
    }

    @Override
    public boolean visibleToAnonymousUsers() {
        return this.confluenceAccessManager.getUserAccessStatus(null).canUseConfluence();
    }

    @Override
    public Pair<String, WebResourceDependenciesRecorder.RecordedResources> getWelcomeMessageWithResources() {
        try {
            return this.webResourceDependenciesRecorder.record(() -> ((WelcomeMessageService)this.welcomeMessageService).getWelcomeMessage());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

