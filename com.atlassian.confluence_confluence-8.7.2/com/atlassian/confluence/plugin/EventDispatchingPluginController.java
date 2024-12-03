/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginParseException
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.event.events.plugin.AsyncPluginDisableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginEnableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginModuleDisableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginModuleEnableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginUninstallEvent;
import com.atlassian.confluence.event.events.plugin.PluginDisableEvent;
import com.atlassian.confluence.event.events.plugin.PluginEnableEvent;
import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.PluginModuleDisableEvent;
import com.atlassian.confluence.event.events.plugin.PluginModuleEnableEvent;
import com.atlassian.confluence.event.events.plugin.PluginUninstallEvent;
import com.atlassian.confluence.plugin.persistence.PluginDataDao;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginParseException;
import java.util.Set;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDispatchingPluginController
implements PluginController {
    private static final Logger log = LoggerFactory.getLogger(EventDispatchingPluginController.class);
    private EventPublisher eventPublisher;
    private PluginController pluginController;
    private PluginDataDao pluginDataDao;

    public void setPluginDataDao(PluginDataDao pluginDataDao) {
        this.pluginDataDao = pluginDataDao;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setPluginController(PluginController pluginController) {
        this.pluginController = pluginController;
    }

    public void disablePlugin(String key) {
        this.pluginController.disablePlugin(key);
        this.eventPublisher.publish((Object)new PluginDisableEvent(this, key, PluginDisableEvent.Scope.PERSISTENT));
        this.eventPublisher.publish((Object)new AsyncPluginDisableEvent(this, key, PluginDisableEvent.Scope.PERSISTENT));
    }

    public void enablePluginModule(String completeKey) {
        this.pluginController.enablePluginModule(completeKey);
        this.eventPublisher.publish((Object)new PluginModuleEnableEvent(this, completeKey));
        this.eventPublisher.publish((Object)new AsyncPluginModuleEnableEvent(this, completeKey));
    }

    public void disablePluginModule(String completeKey) {
        this.pluginController.disablePluginModule(completeKey);
        this.eventPublisher.publish((Object)new PluginModuleDisableEvent(this, completeKey));
        this.eventPublisher.publish((Object)new AsyncPluginModuleDisableEvent(this, completeKey));
    }

    public Set<String> installPlugins(PluginArtifact ... pluginArtifacts) throws PluginParseException {
        Set installedKeys = this.pluginController.installPlugins(pluginArtifacts);
        for (String key : installedKeys) {
            this.eventPublisher.publish((Object)new PluginInstallEvent(this, key));
            this.eventPublisher.publish((Object)new AsyncPluginInstallEvent(this, key));
        }
        return installedKeys;
    }

    public void uninstall(Plugin plugin) throws PluginException {
        if (this.pluginDataDao.pluginDataExists(plugin.getKey())) {
            this.pluginDataDao.remove(plugin.getKey());
        } else if (!plugin.isBundledPlugin()) {
            log.warn("Uninstalling plugin [{}] without actual data.", (Object)plugin.getKey());
        }
        this.pluginController.uninstall(plugin);
        this.eventPublisher.publish((Object)new PluginUninstallEvent(this, plugin.getKey(), plugin.getName()));
        this.eventPublisher.publish((Object)new AsyncPluginUninstallEvent(this, plugin.getKey(), plugin.getName()));
    }

    public int scanForNewPlugins() throws PluginParseException {
        return this.pluginController.scanForNewPlugins();
    }

    public void disablePluginWithoutPersisting(String key) {
        this.pluginController.disablePluginWithoutPersisting(key);
        this.eventPublisher.publish((Object)new PluginDisableEvent(this, key, PluginDisableEvent.Scope.TEMPORARY));
        this.eventPublisher.publish((Object)new AsyncPluginDisableEvent(this, key, PluginDisableEvent.Scope.TEMPORARY));
    }

    public void enablePlugins(String ... keys) {
        this.pluginController.enablePlugins(keys);
        for (String key : keys) {
            this.eventPublisher.publish((Object)new PluginEnableEvent(this, key));
            this.eventPublisher.publish((Object)new AsyncPluginEnableEvent(this, key));
        }
    }

    public void revertRestartRequiredChange(String pluginKey) throws PluginException {
        this.pluginController.revertRestartRequiredChange(pluginKey);
    }

    public void removeDynamicModule(Plugin plugin, ModuleDescriptor<?> module) {
        this.pluginController.removeDynamicModule(plugin, module);
        this.eventPublisher.publish((Object)new PluginModuleDisableEvent(this, module.getCompleteKey()));
        this.eventPublisher.publish((Object)new AsyncPluginModuleDisableEvent(this, module.getCompleteKey()));
    }

    public ModuleDescriptor<?> addDynamicModule(Plugin plugin, Element module) {
        ModuleDescriptor moduleDescriptor = this.pluginController.addDynamicModule(plugin, module);
        this.eventPublisher.publish((Object)new PluginModuleEnableEvent(this, moduleDescriptor.getCompleteKey()));
        this.eventPublisher.publish((Object)new AsyncPluginModuleEnableEvent(this, moduleDescriptor.getCompleteKey()));
        return moduleDescriptor;
    }
}

