/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.impl.ConfigurableClassLoader;
import com.rometools.rome.io.DelegatingModuleGenerator;
import com.rometools.rome.io.DelegatingModuleParser;
import com.rometools.rome.io.WireFeedGenerator;
import com.rometools.rome.io.WireFeedParser;
import com.rometools.rome.io.impl.PropertiesLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class PluginManager<T> {
    private final String[] propertyValues;
    private final List<String> keys;
    private final WireFeedParser parentParser;
    private final WireFeedGenerator parentGenerator;
    private Map<String, T> pluginsMap;
    private List<T> pluginsList;

    protected PluginManager(String propertyKey) {
        this(propertyKey, null, null);
    }

    protected PluginManager(String propertyKey, WireFeedParser parentParser, WireFeedGenerator parentGenerator) {
        this.parentParser = parentParser;
        this.parentGenerator = parentGenerator;
        this.propertyValues = PropertiesLoader.getPropertiesLoader().getTokenizedProperty(propertyKey, ", ");
        this.loadPlugins();
        this.pluginsMap = Collections.unmodifiableMap(this.pluginsMap);
        this.pluginsList = Collections.unmodifiableList(this.pluginsList);
        this.keys = Collections.unmodifiableList(new ArrayList<String>(this.pluginsMap.keySet()));
    }

    protected abstract String getKey(T var1);

    protected List<String> getKeys() {
        return this.keys;
    }

    protected List<T> getPlugins() {
        return this.pluginsList;
    }

    protected Map<String, T> getPluginMap() {
        return this.pluginsMap;
    }

    protected T getPlugin(String key) {
        return this.pluginsMap.get(key);
    }

    private void loadPlugins() {
        ArrayList<T> finalPluginsList = new ArrayList<T>();
        this.pluginsList = new ArrayList<T>();
        this.pluginsMap = new HashMap<String, T>();
        String className = null;
        try {
            Class<T>[] classes;
            for (Class<T> clazz : classes = this.getClasses()) {
                className = clazz.getName();
                T plugin = clazz.newInstance();
                if (plugin instanceof DelegatingModuleParser) {
                    ((DelegatingModuleParser)plugin).setFeedParser(this.parentParser);
                }
                if (plugin instanceof DelegatingModuleGenerator) {
                    ((DelegatingModuleGenerator)plugin).setFeedGenerator(this.parentGenerator);
                }
                this.pluginsMap.put(this.getKey(plugin), plugin);
                this.pluginsList.add(plugin);
            }
            Collection<T> plugins = this.pluginsMap.values();
            for (T plugin : plugins) {
                finalPluginsList.add(plugin);
            }
            Iterator<T> iterator = this.pluginsList.iterator();
            while (iterator.hasNext()) {
                T plugin = iterator.next();
                if (finalPluginsList.contains(plugin)) continue;
                iterator.remove();
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("could not instantiate plugin " + className, ex);
        }
        catch (ExceptionInInitializerError er) {
            throw new RuntimeException("could not instantiate plugin " + className, er);
        }
    }

    private Class<T>[] getClasses() throws ClassNotFoundException {
        ClassLoader classLoader = ConfigurableClassLoader.INSTANCE.getClassLoader();
        ArrayList classes = new ArrayList();
        boolean useLoadClass = Boolean.valueOf(System.getProperty("rome.pluginmanager.useloadclass", "false"));
        for (String propertyValue : this.propertyValues) {
            Class<?> mClass = useLoadClass ? classLoader.loadClass(propertyValue) : Class.forName(propertyValue, true, classLoader);
            classes.add(mClass);
        }
        Class[] array = new Class[classes.size()];
        classes.toArray(array);
        return array;
    }
}

