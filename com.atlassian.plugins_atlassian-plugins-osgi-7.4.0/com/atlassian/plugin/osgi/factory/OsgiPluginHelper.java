/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.module.ContainerAccessor
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.module.ContainerAccessor;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.util.tracker.ServiceTracker;

interface OsgiPluginHelper {
    public Bundle getBundle();

    public <T> Class<T> loadClass(String var1, Class<?> var2) throws ClassNotFoundException;

    public URL getResource(String var1);

    public InputStream getResourceAsStream(String var1);

    public ClassLoader getClassLoader();

    public Bundle install();

    public void onEnable(ServiceTracker ... var1);

    public void onDisable();

    public void onUninstall();

    @Nonnull
    public PluginDependencies getDependencies();

    public void setPluginContainer(Object var1);

    public ContainerAccessor getContainerAccessor();

    public ContainerAccessor getRequiredContainerAccessor();

    public boolean isRemotePlugin();
}

