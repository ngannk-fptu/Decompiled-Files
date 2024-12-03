/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.jpos.confluencemacro;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ExportAsService(value={LifecycleAware.class})
@Component
public class JposLifecycle
implements LifecycleAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(JposLifecycle.class);
    private static final String DEPRECATED_PLUGIN_KEY = "com.atlassian.jpos.confluencemacro.portfolio-sever-confluence-macro";
    private static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-portfolio-server-macro";
    private final PluginAccessor pluginAccessor;
    private final PluginController pluginController;
    private final PluginEventManager pluginEventManager;

    public JposLifecycle(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginController pluginController, @ComponentImport PluginEventManager pluginEventManager) {
        this.pluginAccessor = pluginAccessor;
        this.pluginController = pluginController;
        this.pluginEventManager = pluginEventManager;
    }

    public void onStart() {
        LOGGER.debug("JPOS macro lifecycle started");
        Plugin plugin = this.pluginAccessor.getPlugin(PLUGIN_KEY);
        Plugin deprecatedPlugin = this.pluginAccessor.getPlugin(DEPRECATED_PLUGIN_KEY);
        if (plugin != null && deprecatedPlugin != null) {
            LOGGER.info("Deprecated plugin {} replaced by {} will be uninstalled", (Object)DEPRECATED_PLUGIN_KEY, (Object)PLUGIN_KEY);
            CompletableFuture.runAsync(() -> {
                try {
                    this.pluginController.uninstall(deprecatedPlugin);
                    LOGGER.info("Deprecated plugin {} has been uninstalled", (Object)DEPRECATED_PLUGIN_KEY);
                    plugin.getModuleDescriptors().forEach(descriptor -> this.pluginEventManager.broadcast((Object)new PluginModuleEnabledEvent(descriptor)));
                }
                catch (PluginException e) {
                    LOGGER.error("Failed uninstall of deprecated plugin {}", (Object)DEPRECATED_PLUGIN_KEY);
                }
            });
        }
    }

    public void onStop() {
    }
}

