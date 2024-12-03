/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.IllegalPluginStateException
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.IllegalPluginStateException;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.osgi.factory.OsgiPluginHelper;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.util.tracker.ServiceTracker;

abstract class OsgiPluginNotInstalledHelperBase
implements OsgiPluginHelper {
    private final String key;

    OsgiPluginNotInstalledHelperBase(String key) {
        this.key = (String)Preconditions.checkNotNull((Object)key);
    }

    @Override
    public Bundle getBundle() {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    public URL getResource(String name) {
        throw new IllegalPluginStateException("Cannot getResource(" + name + "): " + this.getNotInstalledMessage());
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        throw new IllegalPluginStateException("Cannot getResourceAsStream(" + name + "): " + this.getNotInstalledMessage());
    }

    @Override
    public ClassLoader getClassLoader() {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    public void onEnable(ServiceTracker ... serviceTrackers) {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    public void onDisable() {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    public void onUninstall() {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    @Nonnull
    public PluginDependencies getDependencies() {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    public void setPluginContainer(Object container) {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    public ContainerAccessor getContainerAccessor() {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    @Override
    public ContainerAccessor getRequiredContainerAccessor() {
        throw new IllegalPluginStateException(this.getNotInstalledMessage());
    }

    protected String getKey() {
        return this.key;
    }

    protected abstract String getNotInstalledMessage();
}

