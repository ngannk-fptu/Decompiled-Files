/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 */
package com.atlassian.plugin.impl;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.impl.AbstractPlugin;
import com.atlassian.plugin.util.ClassLoaderUtils;
import java.io.InputStream;
import java.net.URL;

public class XmlDynamicPlugin
extends AbstractPlugin {
    public XmlDynamicPlugin(PluginArtifact pluginArtifact) {
        super(pluginArtifact);
    }

    public boolean isUninstallable() {
        return true;
    }

    public boolean isDeleteable() {
        return true;
    }

    public boolean isDynamicallyLoaded() {
        return true;
    }

    @Override
    public void close() {
    }

    public <M> Class<M> loadClass(String clazz, Class<?> callingClass) throws ClassNotFoundException {
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
}

