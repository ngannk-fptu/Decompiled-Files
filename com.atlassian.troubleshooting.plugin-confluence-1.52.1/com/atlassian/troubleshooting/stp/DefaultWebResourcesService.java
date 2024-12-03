/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.api.WebResourcesService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultWebResourcesService
implements WebResourcesService {
    private final WebResourceManager webResourceManager;
    private final PluginInfo pluginInfo;

    @Autowired
    public DefaultWebResourcesService(WebResourceManager webResourceManager, PluginInfo pluginInfo) {
        this.webResourceManager = webResourceManager;
        this.pluginInfo = pluginInfo;
    }

    @Override
    public void requireResourcesForContext(String context) {
        this.webResourceManager.requireResourcesForContext(context);
    }

    @Override
    public void requireResource(String webResKey) {
        this.webResourceManager.requireResource(this.pluginInfo.getPluginKey() + ":" + webResKey);
    }

    @Override
    public String getStaticPluginResource(String webResKey, String resourceName) {
        return this.webResourceManager.getStaticPluginResource(this.pluginInfo.getPluginKey() + ":" + webResKey, resourceName);
    }
}

