/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package com.atlassian.plugins.impl;

import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;

public class ComponentImports {
    @ComponentImport
    private WebResourceIntegration webResourceIntegration;
    @ComponentImport
    private PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private DarkFeatureManager darkFeatureManager;
    @ComponentImport
    private TransactionTemplate transactionTemplate;
    @ComponentImport
    private CacheManager cacheManager;
    @ComponentImport
    private EventPublisher eventPublisher;
    @ComponentImport
    private PluginAccessor pluginAccessor;
    @ComponentImport
    private WebSudoManager webSudoManager;
    @ComponentImport
    private LoginUriProvider loginUriProvider;
    @ComponentImport
    private SoyTemplateRenderer soyTemplateRenderer;
    @ComponentImport
    private PageBuilderService pageBuilderService;
    @ComponentImport
    private UserManager userManager;
    @ComponentImport
    private LicenseHandler licenseHandler;
}

