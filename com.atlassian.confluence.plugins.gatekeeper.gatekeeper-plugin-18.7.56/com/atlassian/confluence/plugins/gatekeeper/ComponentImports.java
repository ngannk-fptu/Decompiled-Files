/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.ConfluenceAdministratorCondition
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.SpacePermissionCondition
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.SpaceSidebarCondition
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.UserManager
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package com.atlassian.confluence.plugins.gatekeeper;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.descriptor.web.conditions.ConfluenceAdministratorCondition;
import com.atlassian.confluence.plugin.descriptor.web.conditions.SpacePermissionCondition;
import com.atlassian.confluence.plugin.descriptor.web.conditions.SpaceSidebarCondition;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;
import com.atlassian.webresource.api.assembler.PageBuilderService;

public class ComponentImports {
    @ComponentImport
    LicenseService licenseService;
    @ComponentImport
    PluginAccessor pluginAccessor;
    @ComponentImport
    PluginController pluginController;
    @ComponentImport
    PluginRetrievalService pluginRetrievalService;
    @ComponentImport
    TransactionTemplate transactionTemplate;
    @ComponentImport
    ApplicationProperties applicationProperties;
    @ComponentImport
    TemplateRenderer templateRenderer;
    @ComponentImport
    PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    LoginUriProvider loginUriProvider;
    @ComponentImport
    com.atlassian.sal.api.user.UserManager salUserManager;
    @ComponentImport
    UserManager userManager;
    @ComponentImport
    I18nResolver i18nResolver;
    @ComponentImport
    SpaceManager spaceManager;
    @ComponentImport
    EventPublisher eventPublisher;
    @ComponentImport
    CacheManager cacheManager;
    @ComponentImport
    ClusterManager clusterManager;
    @ComponentImport
    PageBuilderService pageBuilderService;
    @ComponentImport
    PageManager pageManager;
    @ComponentImport
    CrowdService crowdService;
    @ComponentImport
    GroupManager groupManager;
    @ComponentImport
    ContextPathHolder contextPathHolder;
    @ComponentImport
    BandanaManager bandanaManager;
    @ComponentImport
    DarkFeatureManager darkFeatureManager;
    @ComponentImport
    DarkFeaturesManager darkFeaturesManager;
    ConfluenceAdministratorCondition confluenceAdministratorCondition;
    SpacePermissionCondition spacePermissionCondition;
    SpaceSidebarCondition spaceSidebarCondition;
}

