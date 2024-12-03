/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.DefaultPluginArtifactFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginArtifactFactory;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.loaders.DiscardablePluginLoader;
import com.atlassian.plugin.loaders.DynamicPluginLoader;
import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.Scanner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanningPluginLoader
implements DynamicPluginLoader,
DiscardablePluginLoader {
    private static final Logger log = LoggerFactory.getLogger(ScanningPluginLoader.class);
    protected final Scanner scanner;
    protected final Map<DeploymentUnit, Plugin> plugins;
    protected final List<PluginFactory> pluginFactories;
    protected final PluginArtifactFactory pluginArtifactFactory;

    public ScanningPluginLoader(Scanner scanner, List<PluginFactory> pluginFactories, PluginEventManager pluginEventManager) {
        this(scanner, pluginFactories, new DefaultPluginArtifactFactory(), pluginEventManager);
    }

    public ScanningPluginLoader(Scanner scanner, List<PluginFactory> pluginFactories, PluginArtifactFactory pluginArtifactFactory, PluginEventManager pluginEventManager) {
        Preconditions.checkNotNull(pluginFactories, (Object)"The list of plugin factories must be specified");
        Preconditions.checkNotNull((Object)pluginEventManager, (Object)"The event manager must be specified");
        Preconditions.checkNotNull((Object)scanner, (Object)"The scanner must be specified");
        this.plugins = new TreeMap<DeploymentUnit, Plugin>();
        this.pluginArtifactFactory = pluginArtifactFactory;
        this.scanner = scanner;
        this.pluginFactories = new ArrayList<PluginFactory>(pluginFactories);
        pluginEventManager.register((Object)this);
    }

    @Override
    public Iterable<Plugin> loadAllPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        this.scanner.scan();
        for (DeploymentUnit deploymentUnit : this.scanner.getDeploymentUnits()) {
            Plugin plugin = this.deployPluginFromUnit(deploymentUnit, moduleDescriptorFactory);
            plugin = this.postProcess(plugin);
            this.plugins.put(deploymentUnit, plugin);
        }
        if (this.scanner.getDeploymentUnits().isEmpty()) {
            log.info("No plugins found to be deployed");
        }
        return ImmutableList.copyOf(this.plugins.values());
    }

    @Override
    public Iterable<Plugin> loadFoundPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        Collection<DeploymentUnit> updatedDeploymentUnits = this.scanner.scan();
        ArrayList<Plugin> foundPlugins = new ArrayList<Plugin>();
        for (DeploymentUnit deploymentUnit : updatedDeploymentUnits) {
            if (this.plugins.containsKey(deploymentUnit)) continue;
            Plugin plugin = this.deployPluginFromUnit(deploymentUnit, moduleDescriptorFactory);
            plugin = this.postProcess(plugin);
            this.plugins.put(deploymentUnit, plugin);
            foundPlugins.add(plugin);
        }
        if (foundPlugins.isEmpty()) {
            log.info("No plugins found to be installed");
        }
        return ImmutableList.copyOf(foundPlugins);
    }

    @Override
    public boolean supportsRemoval() {
        return true;
    }

    @Override
    public boolean supportsAddition() {
        return true;
    }

    protected final Plugin deployPluginFromUnit(DeploymentUnit deploymentUnit, ModuleDescriptorFactory moduleDescriptorFactory) {
        UnloadablePlugin plugin = null;
        String errorText = "No plugin factories found for plugin file " + deploymentUnit;
        String pluginKey = null;
        for (PluginFactory factory : this.pluginFactories) {
            try {
                PluginArtifact artifact = this.pluginArtifactFactory.create(deploymentUnit.getPath().toURI());
                pluginKey = factory.canCreate(artifact);
                if (pluginKey == null || (plugin = factory.create(artifact, moduleDescriptorFactory)) == null) continue;
                log.debug("Plugin factory '{}' created plugin '{}'.", (Object)factory.getClass().getName(), (Object)pluginKey);
            }
            catch (Throwable ex) {
                log.error("Unable to deploy plugin '{}' from '{}'.", pluginKey, (Object)deploymentUnit);
                log.error("Because of the following exception:", ex);
                errorText = ex.getMessage();
            }
            break;
        }
        if (plugin == null) {
            plugin = new UnloadablePlugin(errorText);
            if (pluginKey != null) {
                plugin.setKey(pluginKey);
            } else {
                plugin.setKey(deploymentUnit.getPath().getName());
            }
            log.debug("Could not find a suitable factory for plugin '{}' of '{}'", (Object)pluginKey, (Object)deploymentUnit);
        } else {
            log.debug("Plugin '{}' created from '{}'", (Object)plugin.getKey(), (Object)deploymentUnit);
        }
        return plugin;
    }

    @Override
    public void removePlugin(Plugin plugin) {
        if (plugin.getPluginState() == PluginState.ENABLED) {
            throw new PluginException("Cannot remove enabled plugin '" + plugin.getKey() + '\"');
        }
        if (!plugin.isUninstallable()) {
            throw new PluginException("Cannot remove uninstallable plugin '" + plugin.getKey() + '\"');
        }
        DeploymentUnit deploymentUnit = this.findMatchingDeploymentUnit(plugin);
        plugin.uninstall();
        if (plugin.isDeleteable()) {
            this.deleteDeploymentUnit(deploymentUnit);
        }
        this.plugins.remove(deploymentUnit);
        log.info("Removed plugin '{}'", (Object)plugin.getKey());
    }

    private void deleteDeploymentUnit(DeploymentUnit deploymentUnit) {
        try {
            boolean found = false;
            for (DeploymentUnit unit : this.plugins.keySet()) {
                if (!unit.getPath().equals(deploymentUnit.getPath()) || unit.equals(deploymentUnit)) continue;
                found = true;
                break;
            }
            if (!found) {
                this.scanner.remove(deploymentUnit);
            }
        }
        catch (SecurityException e) {
            throw new PluginException((Throwable)e);
        }
    }

    private DeploymentUnit findMatchingDeploymentUnit(Plugin plugin) {
        DeploymentUnit deploymentUnit = null;
        for (Map.Entry<DeploymentUnit, Plugin> entry : this.plugins.entrySet()) {
            if (entry.getValue() != plugin) continue;
            deploymentUnit = entry.getKey();
            break;
        }
        if (deploymentUnit == null) {
            throw new PluginException("This pluginLoader has no memory of deploying the plugin you are trying remove: [" + plugin.getName() + "]");
        }
        return deploymentUnit;
    }

    @PluginEventListener
    public void onShutdown(PluginFrameworkShutdownEvent event) {
        Iterator<Plugin> it = this.plugins.values().iterator();
        while (it.hasNext()) {
            Plugin plugin = it.next();
            if (plugin.isUninstallable()) {
                plugin.uninstall();
            }
            it.remove();
        }
        this.scanner.reset();
    }

    @Override
    public boolean isDynamicPluginLoader() {
        return true;
    }

    @Override
    public String canLoad(PluginArtifact pluginArtifact) {
        PluginFactory factory;
        String pluginKey = null;
        Iterator<PluginFactory> iterator = this.pluginFactories.iterator();
        while (iterator.hasNext() && (pluginKey = (factory = iterator.next()).canCreate(pluginArtifact)) == null) {
        }
        return pluginKey;
    }

    @Override
    public void discardPlugin(Plugin plugin) {
        this.plugins.remove(this.findMatchingDeploymentUnit(plugin));
    }

    protected Plugin postProcess(Plugin plugin) {
        return plugin;
    }

    @Override
    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        for (PluginFactory pluginFactory : this.pluginFactories) {
            ModuleDescriptor moduleDescriptor = pluginFactory.createModule(plugin, module, moduleDescriptorFactory);
            if (moduleDescriptor == null) continue;
            return moduleDescriptor;
        }
        return null;
    }
}

