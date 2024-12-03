/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginInstaller
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.PluginRegistry$ReadWrite
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 *  com.atlassian.plugin.manager.SafeModeManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.plugin.ConfluencePluginManager;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginInstaller;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.PluginRegistry;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.manager.SafeModeManager;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupPluginManager
extends ConfluencePluginManager {
    private static final Logger log = LoggerFactory.getLogger(SetupPluginManager.class);

    public SetupPluginManager(PluginRegistry.ReadWrite pluginRegistry, PluginAccessor pluginAccessor, PluginPersistentStateStore pluginStateStore, List<Object> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, EventPublisher eventPublisher) {
        super(pluginRegistry, pluginAccessor, pluginStateStore, pluginLoaders, moduleDescriptorFactory, pluginEventManager, Collections.EMPTY_LIST, eventPublisher, new SetupPluginInstaller(), SafeModeManager.START_ALL_PLUGINS);
    }

    @Deprecated(forRemoval=true)
    public SetupPluginManager(PluginRegistry.ReadWrite pluginRegistry, PluginAccessor pluginAccessor, PluginPersistentStateStore pluginStateStore, List<Object> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, EventPublisher eventPublisher, TenantRegistry tenantRegistry) {
        super(pluginRegistry, pluginAccessor, pluginStateStore, pluginLoaders, moduleDescriptorFactory, pluginEventManager, Collections.EMPTY_LIST, eventPublisher, new SetupPluginInstaller(), SafeModeManager.START_ALL_PLUGINS);
    }

    @Override
    protected boolean isSetupPluginManager() {
        return true;
    }

    @Override
    public void init() throws PluginParseException {
        log.info("Initialising setup plugin system");
        super.init();
    }

    public void shutdown() {
        log.info("Shutting down setup plugin system");
        super.shutdown();
    }

    private static class SetupPluginInstaller
    implements PluginInstaller {
        private SetupPluginInstaller() {
        }

        public void installPlugin(String pluginKey, PluginArtifact pluginArtifact) {
            throw new IllegalStateException("Installation of a custom plugin attempted during setup: " + pluginKey + ". Plugins during setup can only be bundled or core.");
        }
    }
}

