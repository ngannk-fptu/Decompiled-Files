/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.util.Assertions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.classloader;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.classloader.AbstractClassLoader;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.event.impl.DefaultPluginEventManager;
import com.atlassian.plugin.util.Assertions;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginsClassLoader
extends AbstractClassLoader {
    private static final Logger log = LoggerFactory.getLogger(PluginsClassLoader.class);
    private final PluginAccessor pluginAccessor;
    private final Map<String, Plugin> pluginResourceIndex = new HashMap<String, Plugin>();
    private final Map<String, Plugin> pluginClassIndex = new HashMap<String, Plugin>();
    private final Set<String> missedPluginResource = new HashSet<String>();
    private final Set<String> missedPluginClass = new HashSet<String>();
    private ClassLoader parentClassLoader;

    public PluginsClassLoader(PluginAccessor pluginAccessor) {
        this(null, pluginAccessor, new DefaultPluginEventManager());
    }

    public PluginsClassLoader(ClassLoader parent, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        super(parent);
        this.parentClassLoader = parent;
        this.pluginAccessor = (PluginAccessor)Assertions.notNull((String)"pluginAccessor", (Object)pluginAccessor);
        pluginEventManager.register((Object)this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected URL findResource(String name) {
        Plugin indexedPlugin;
        PluginsClassLoader pluginsClassLoader = this;
        synchronized (pluginsClassLoader) {
            indexedPlugin = this.pluginResourceIndex.get(name);
        }
        URL result = this.isPluginEnabled(indexedPlugin) ? indexedPlugin.getClassLoader().getResource(name) : this.getResourceFromPlugins(name);
        if (log.isDebugEnabled()) {
            log.debug("Find resource [ {} ], found [ {} ]", (Object)name, (Object)result);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        Plugin indexedPlugin;
        PluginsClassLoader pluginsClassLoader = this;
        synchronized (pluginsClassLoader) {
            indexedPlugin = this.pluginClassIndex.get(className);
        }
        Class<?> result = this.isPluginEnabled(indexedPlugin) ? indexedPlugin.getClassLoader().loadClass(className) : this.loadClassFromPlugins(className);
        if (log.isDebugEnabled()) {
            log.debug("Find class [ {} ], found [ {} ]", (Object)className, result);
        }
        if (result != null) {
            return result;
        }
        throw new ClassNotFoundException(className);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Class<?> loadClassFromPlugins(String className) {
        boolean isMissedClassName;
        PluginsClassLoader pluginsClassLoader = this;
        synchronized (pluginsClassLoader) {
            isMissedClassName = this.missedPluginClass.contains(className);
        }
        if (isMissedClassName) {
            return null;
        }
        Collection plugins = this.pluginAccessor.getEnabledPlugins();
        if (log.isDebugEnabled()) {
            log.debug("loadClassFromPlugins ({}) looping through plugins...", (Object)className);
        }
        for (Plugin plugin : plugins) {
            if (log.isDebugEnabled()) {
                log.debug("loadClassFromPlugins ({}) looking in plugin '{}'.", (Object)className, (Object)plugin.getKey());
            }
            try {
                Class<?> result = plugin.getClassLoader().loadClass(className);
                PluginsClassLoader pluginsClassLoader2 = this;
                synchronized (pluginsClassLoader2) {
                    this.pluginClassIndex.put(className, plugin);
                }
                if (log.isDebugEnabled()) {
                    log.debug("loadClassFromPlugins ({}) found in plugin '{}'.", (Object)className, (Object)plugin.getKey());
                }
                return result;
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("loadClassFromPlugins ({}) not found - caching the miss.", (Object)className);
        }
        PluginsClassLoader pluginsClassLoader3 = this;
        synchronized (pluginsClassLoader3) {
            this.missedPluginClass.add(className);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private URL getResourceFromPlugins(String name) {
        boolean isMissedResource;
        PluginsClassLoader pluginsClassLoader = this;
        synchronized (pluginsClassLoader) {
            isMissedResource = this.missedPluginResource.contains(name);
        }
        if (isMissedResource) {
            return null;
        }
        Collection plugins = this.pluginAccessor.getEnabledPlugins();
        for (Plugin plugin : plugins) {
            URL resource = plugin.getClassLoader().getResource(name);
            if (resource == null) continue;
            PluginsClassLoader pluginsClassLoader2 = this;
            synchronized (pluginsClassLoader2) {
                this.pluginResourceIndex.put(name, plugin);
            }
            return resource;
        }
        PluginsClassLoader pluginsClassLoader3 = this;
        synchronized (pluginsClassLoader3) {
            this.missedPluginResource.add(name);
        }
        return null;
    }

    private boolean isPluginEnabled(Plugin plugin) {
        return plugin != null && this.pluginAccessor.isPluginEnabled(plugin.getKey());
    }

    public synchronized void notifyUninstallPlugin(Plugin plugin) {
        this.flushMissesCaches();
        Iterator<Map.Entry<String, Plugin>> it = this.pluginResourceIndex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Plugin> resourceEntry = it.next();
            Plugin pluginForResource = resourceEntry.getValue();
            if (!plugin.getKey().equals(pluginForResource.getKey())) continue;
            it.remove();
        }
        it = this.pluginClassIndex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Plugin> pluginClassEntry = it.next();
            Plugin pluginForClass = pluginClassEntry.getValue();
            if (!plugin.getKey().equals(pluginForClass.getKey())) continue;
            it.remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Plugin getPluginForClass(String className) {
        Plugin indexedPlugin;
        PluginsClassLoader pluginsClassLoader = this;
        synchronized (pluginsClassLoader) {
            indexedPlugin = this.pluginClassIndex.get(className);
        }
        if (this.isPluginEnabled(indexedPlugin)) {
            return indexedPlugin;
        }
        if (this.isSystemClass(className)) {
            return null;
        }
        Class<?> clazz = this.loadClassFromPlugins(className);
        if (clazz == null) {
            return null;
        }
        PluginsClassLoader pluginsClassLoader2 = this;
        synchronized (pluginsClassLoader2) {
            indexedPlugin = this.pluginClassIndex.get(className);
        }
        return indexedPlugin;
    }

    private boolean isSystemClass(String className) {
        try {
            this.getClass().getClassLoader().loadClass(className);
            return true;
        }
        catch (ClassNotFoundException ex) {
            if (this.parentClassLoader != null) {
                try {
                    this.parentClassLoader.loadClass(className);
                    return true;
                }
                catch (ClassNotFoundException ex2) {
                    return false;
                }
            }
            return false;
        }
    }

    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        this.notifyPluginOrModuleEnabled();
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.notifyPluginOrModuleEnabled();
    }

    public synchronized void notifyPluginOrModuleEnabled() {
        this.flushMissesCaches();
    }

    private void flushMissesCaches() {
        this.missedPluginClass.clear();
        this.missedPluginResource.clear();
    }
}

