/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginException
 *  com.google.common.collect.ImmutableList
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.loaders.SinglePluginLoader;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPathPluginLoader
implements PluginLoader {
    private static Logger log = LoggerFactory.getLogger(ClassPathPluginLoader.class);
    private final String fileNameToLoad;
    private Map<Plugin, SinglePluginLoader> pluginLoaderMap;

    public ClassPathPluginLoader() {
        this("atlassian-plugin.xml");
    }

    public ClassPathPluginLoader(String fileNameToLoad) {
        this.fileNameToLoad = fileNameToLoad;
    }

    private void loadClassPathPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        Enumeration<URL> pluginDescriptorFiles;
        try {
            pluginDescriptorFiles = ClassLoaderUtils.getResources(this.fileNameToLoad, this.getClass());
        }
        catch (IOException e) {
            log.error("Could not load classpath plugins: " + e, (Throwable)e);
            return;
        }
        this.pluginLoaderMap = new LinkedHashMap<Plugin, SinglePluginLoader>();
        while (pluginDescriptorFiles.hasMoreElements()) {
            URL url = pluginDescriptorFiles.nextElement();
            SinglePluginLoader singlePluginLoader = new SinglePluginLoader(url);
            for (Plugin plugin : singlePluginLoader.loadAllPlugins(moduleDescriptorFactory)) {
                this.pluginLoaderMap.put(plugin, singlePluginLoader);
            }
        }
    }

    @Override
    public Iterable<Plugin> loadAllPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        if (this.pluginLoaderMap == null) {
            this.loadClassPathPlugins(moduleDescriptorFactory);
        }
        return ImmutableList.copyOf(this.pluginLoaderMap.keySet());
    }

    @Override
    public boolean supportsRemoval() {
        return false;
    }

    @Override
    public boolean supportsAddition() {
        return false;
    }

    @Override
    public boolean isDynamicPluginLoader() {
        return false;
    }

    @Override
    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        if (this.pluginLoaderMap.containsKey(plugin)) {
            return this.pluginLoaderMap.get(plugin).createModule(plugin, module, moduleDescriptorFactory);
        }
        return null;
    }

    @Override
    public Iterable<Plugin> loadFoundPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        throw new UnsupportedOperationException("This PluginLoader does not support addition.");
    }

    @Override
    public void removePlugin(Plugin plugin) {
        throw new PluginException("This PluginLoader does not support removal.");
    }
}

