/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 */
package com.atlassian.plugin.impl;

import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.impl.AbstractPlugin;
import com.atlassian.plugin.util.ClassLoaderUtils;
import java.io.InputStream;
import java.net.URL;

public class StaticPlugin
extends AbstractPlugin {
    public StaticPlugin() {
        super(null);
    }

    public boolean isUninstallable() {
        return false;
    }

    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) throws ClassNotFoundException {
        return ClassLoaderUtils.loadClass(clazz, callingClass);
    }

    public ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    public URL getResource(String name) {
        return ClassLoaderUtils.getResource(name, this.getClass());
    }

    public InputStream getResourceAsStream(String name) {
        return ClassLoaderUtils.getResourceAsStream(name, this.getClass());
    }

    public boolean isDynamicallyLoaded() {
        return false;
    }

    public boolean isDeleteable() {
        return false;
    }

    @Override
    protected void uninstallInternal() {
        throw new PluginException("Static plugins cannot be uninstalled");
    }
}

