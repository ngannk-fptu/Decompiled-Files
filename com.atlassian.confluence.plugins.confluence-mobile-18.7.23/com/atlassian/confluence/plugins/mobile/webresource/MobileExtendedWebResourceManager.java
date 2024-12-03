/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.PluginResourceLocator
 *  com.atlassian.plugin.webresource.ResourceBatchingConfiguration
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceFilter
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.WebResourceManagerImpl
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.plugins.mobile.webresource;

import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceFilter;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceManagerImpl;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.List;
import java.util.Set;

public class MobileExtendedWebResourceManager
extends WebResourceManagerImpl {
    public MobileExtendedWebResourceManager(PluginResourceLocator pluginResourceLocator, WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration) {
        super(pluginResourceLocator, webResourceIntegration, webResourceUrlProvider, batchingConfiguration);
    }

    public String getRequiredResources(UrlMode urlMode, WebResourceFilter webResourceFilter, Set<String> excludedResourceKeys, List<String> excludedContexts) {
        return super.getRequiredResources(urlMode, webResourceFilter, excludedResourceKeys, excludedContexts);
    }
}

