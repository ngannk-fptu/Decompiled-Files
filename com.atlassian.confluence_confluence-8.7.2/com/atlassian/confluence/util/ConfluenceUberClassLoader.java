/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.cache.CacheMonitoringUtils;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

public class ConfluenceUberClassLoader
extends ClassLoader {
    private final boolean cacheMonitoringEnabled = Boolean.getBoolean("cache.monitoring.enabled");
    private ClassLoader pluginsClassLoader;
    private ClassLoader applicationClassLoader = ConfluenceUberClassLoader.class.getClassLoader();
    private ConfluenceMonitoring confluenceMonitoring;

    public void setPluginsClassLoader(ClassLoader pluginsClassLoader) {
        this.pluginsClassLoader = pluginsClassLoader;
    }

    public void setConfluenceMonitoring(ConfluenceMonitoring confluenceMonitoring) {
        this.confluenceMonitoring = confluenceMonitoring;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            Split split = this.createSplit("loadClass.applicationClassLoader");
            try {
                if (null != name && name.length() > 0 && name.charAt(0) == '[') {
                    Class<?> clazz = Class.forName(name, false, this.applicationClassLoader);
                    return clazz;
                }
                Class<?> clazz = this.applicationClassLoader.loadClass(name);
                return clazz;
            }
            finally {
                split.stop();
            }
        }
        catch (ClassNotFoundException e) {
            if (this.pluginsClassLoader == null) {
                throw e;
            }
            Split split2 = this.createSplit("loadClass.pluginClassLoader");
            try {
                Class<?> clazz2 = this.pluginsClassLoader.loadClass(name);
                return clazz2;
            }
            finally {
                split2.stop();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public URL getResource(String name) {
        URL resource;
        Split appClassLoaderSplit = this.createSplit("getResource.applicationClassLoader");
        try {
            resource = this.applicationClassLoader.getResource(name);
        }
        finally {
            appClassLoaderSplit.stop();
        }
        if (resource == null && this.pluginsClassLoader != null) {
            Split pluginClassLoaderSplit = this.createSplit("getResource.pluginsClassLoader");
            try {
                resource = this.pluginsClassLoader.getResource(name);
            }
            finally {
                pluginClassLoaderSplit.stop();
            }
        }
        return resource;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> resources;
        Split appClassLoaderSplit = this.createSplit("getResources.applicationClassLoader");
        try {
            resources = this.applicationClassLoader.getResources(name);
        }
        finally {
            appClassLoaderSplit.stop();
        }
        if (this.pluginsClassLoader != null) {
            Split pluginClassLoaderSplit = this.createSplit("getResources.pluginsClassLoader");
            try {
                Enumeration<URL> pluginResources = this.pluginsClassLoader.getResources(name);
                if (pluginResources.hasMoreElements()) {
                    Vector<URL> allResources = new Vector<URL>();
                    while (resources.hasMoreElements()) {
                        allResources.add(resources.nextElement());
                    }
                    while (pluginResources.hasMoreElements()) {
                        allResources.add(pluginResources.nextElement());
                    }
                    resources = allResources.elements();
                }
            }
            finally {
                pluginClassLoaderSplit.stop();
            }
        }
        return resources;
    }

    private Split createSplit(String operation) {
        return CacheMonitoringUtils.startSplit(this.confluenceMonitoring, "UBER", (Map<String, String>)ImmutableMap.of((Object)"operation", (Object)operation));
    }
}

