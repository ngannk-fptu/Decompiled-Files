/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.ServletContextFactory
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.ESModuleStreamStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.PluginStreamStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.TomcatStreamStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.WebResourceStreamStrategy;

public class StreamStrategyFactory {
    public static String WEB_CONTEXT_STATIC = "webContextStatic";
    private final ServletContextFactory servletContextFactory;
    private final WebResourceIntegration webResourceIntegration;

    public StreamStrategyFactory(ServletContextFactory servletContextFactory, WebResourceIntegration webResourceIntegration) {
        this.servletContextFactory = servletContextFactory;
        this.webResourceIntegration = webResourceIntegration;
    }

    public StreamStrategy createStandardModuleStreamStrategy(Bundle parent, ResourceLocation resourceLocation) {
        String sourceParam = resourceLocation.getParameter("source");
        boolean isWebContextStatic = WEB_CONTEXT_STATIC.equalsIgnoreCase(sourceParam);
        if (isWebContextStatic) {
            return new TomcatStreamStrategy(this.servletContextFactory);
        }
        if (this.isKeyWebResourceIdentifier(parent.getKey())) {
            return new WebResourceStreamStrategy(this.webResourceIntegration, parent);
        }
        return new PluginStreamStrategy(this.webResourceIntegration, parent);
    }

    protected boolean isKeyWebResourceIdentifier(String resourceKey) {
        return resourceKey.contains(":");
    }

    public StreamStrategy createESModuleStreamStrategy(String pluginKey) {
        return new ESModuleStreamStrategy(this.webResourceIntegration, pluginKey);
    }
}

