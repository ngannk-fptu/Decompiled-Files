/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.upgrade;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JiraComponent
@ExportAsService
public class RateLimitingFrontendPluginRemovalTask
implements LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFrontendPluginRemovalTask.class);
    private static final String FRONTEND_PLUGIN_KEY = "com.atlassian.ratelimiting.rate-limiting-frontend";
    private final PluginAccessor pluginAccessor;
    private final PluginController pluginController;

    public RateLimitingFrontendPluginRemovalTask(@JiraImport PluginAccessor pluginAccessor, @JiraImport PluginController pluginController) {
        this.pluginAccessor = pluginAccessor;
        this.pluginController = pluginController;
    }

    public void onStart() {
        try {
            Plugin plugin = this.pluginAccessor.getPlugin(FRONTEND_PLUGIN_KEY);
            if (plugin != null) {
                this.pluginController.uninstall(plugin);
            }
        }
        catch (Throwable e) {
            logger.error("Failed to uninstall plugin '{}'", (Object)FRONTEND_PLUGIN_KEY, (Object)e);
        }
    }

    public void onStop() {
    }
}

