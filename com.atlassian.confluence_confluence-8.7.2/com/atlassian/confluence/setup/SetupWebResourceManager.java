/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.plugin.webresource.PluginResourceLocator
 *  com.atlassian.plugin.webresource.ResourceBatchingConfiguration
 *  com.atlassian.plugin.webresource.ResourceDependencyResolver
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.WebResourceManagerImpl
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.ResourceDependencyResolver;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceManagerImpl;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;

public class SetupWebResourceManager
extends WebResourceManagerImpl {
    public SetupWebResourceManager(WebResourceAssemblerFactory webResourceAssemblerFactory, LegacyPageBuilderService pageBuilderService, PluginResourceLocator pluginResourceLocator, WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration, ResourceDependencyResolver resourceDependencyResolver) {
        super(webResourceAssemblerFactory, pageBuilderService, pluginResourceLocator, webResourceIntegration, webResourceUrlProvider, batchingConfiguration, resourceDependencyResolver);
    }

    @HtmlSafe
    public String getResources() {
        return this.getRequiredResources(UrlMode.AUTO);
    }
}

