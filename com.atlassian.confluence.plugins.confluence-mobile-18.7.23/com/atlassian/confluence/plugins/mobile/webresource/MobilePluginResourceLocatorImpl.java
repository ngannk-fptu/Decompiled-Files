/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  com.atlassian.plugin.webresource.PluginResourceLocatorImpl
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.plugins.mobile.webresource;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.PluginResourceLocatorImpl;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.Map;

public class MobilePluginResourceLocatorImpl
extends PluginResourceLocatorImpl {
    static final String MATCHES_SUBSTRING = "/plugins/servlet/mobile/download";

    public MobilePluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, PluginEventManager pluginEventManager) {
        super(webResourceIntegration, servletContextFactory, webResourceUrlProvider, pluginEventManager);
    }

    public boolean matches(String url) {
        return url.contains(MATCHES_SUBSTRING) && super.matches(this.modifyUrl(url));
    }

    public DownloadableResource getDownloadableResource(String url, Map<String, String> queryParams) {
        url = this.modifyUrl(url);
        return super.getDownloadableResource(url, queryParams);
    }

    private String modifyUrl(String url) {
        return url.replace(MATCHES_SUBSTRING, "/download");
    }
}

