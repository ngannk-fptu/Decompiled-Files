/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.impl;

import com.atlassian.plugin.JarPluginArtifact;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.classloader.PluginClassLoader;
import com.atlassian.plugin.impl.AbstractPlugin;
import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.net.URL;

public class DefaultDynamicPlugin
extends AbstractPlugin {
    private final PluginClassLoader loader;

    public DefaultDynamicPlugin(DeploymentUnit deploymentUnit, PluginClassLoader loader) {
        this(new JarPluginArtifact(deploymentUnit.getPath()), loader);
    }

    public DefaultDynamicPlugin(PluginArtifact pluginArtifact, PluginClassLoader loader) {
        super((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact));
        this.loader = (PluginClassLoader)Preconditions.checkNotNull((Object)loader);
    }

    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) throws ClassNotFoundException {
        Class<?> result = this.loader.loadClass(clazz);
        return result;
    }

    public boolean isUninstallable() {
        return true;
    }

    public URL getResource(String name) {
        return this.loader.getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        return this.loader.getResourceAsStream(name);
    }

    public ClassLoader getClassLoader() {
        return this.loader;
    }

    public boolean isDynamicallyLoaded() {
        return true;
    }

    public boolean isDeleteable() {
        return true;
    }

    @Override
    protected void uninstallInternal() {
        this.loader.close();
    }
}

