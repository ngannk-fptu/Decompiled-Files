/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.jwt.JwtService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.confluence.plugins.remotepageview;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.jwt.JwtService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

class OsgiImports {
    @ComponentImport
    Renderer viewRenderer;
    @ComponentImport
    ConfluenceWebResourceManager webResourceManager;
    @ComponentImport
    TemplateRenderer templateRenderer;
    @ComponentImport
    PageManager pageManager;
    @ComponentImport
    PermissionManager permissionManager;
    @ComponentImport
    SettingsManager settingsManager;
    @ComponentImport
    I18nResolver i18nResolver;
    @ComponentImport
    JwtService jwtService;
    @ComponentImport
    ApplicationConfiguration applicationConfiguration;
    @ComponentImport
    UserAccessor userAccessor;
    @ComponentImport
    PluginSettingsFactory pluginSettingsFactory;

    private OsgiImports() {
    }
}

