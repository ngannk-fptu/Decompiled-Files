/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import java.util.Collection;
import java.util.Set;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;

public interface PluginController {
    public void enablePlugins(String ... var1);

    public void disablePlugin(String var1);

    public void disablePluginWithoutPersisting(String var1);

    public void enablePluginModule(String var1);

    public void disablePluginModule(String var1);

    public Set<String> installPlugins(PluginArtifact ... var1);

    public void uninstall(Plugin var1);

    default public void uninstallPlugins(Collection<Plugin> plugins) {
        LoggerFactory.getLogger(PluginController.class).warn("Naive uninstallPlugins implementation. Please upgrade plugin framework.");
        plugins.forEach(this::uninstall);
    }

    public void revertRestartRequiredChange(String var1);

    public int scanForNewPlugins();

    public ModuleDescriptor<?> addDynamicModule(Plugin var1, Element var2);

    public void removeDynamicModule(Plugin var1, ModuleDescriptor<?> var2);
}

