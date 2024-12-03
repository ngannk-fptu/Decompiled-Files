/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.IllegalPluginStateException
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.IllegalPluginStateException;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.osgi.factory.OsgiPluginHelper;
import com.atlassian.plugin.osgi.spring.DefaultSpringContainerAccessor;
import com.atlassian.plugin.osgi.util.BundleClassLoaderAccessor;
import com.atlassian.plugin.osgi.util.OsgiPluginUtil;
import com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OsgiPluginInstalledHelper
implements OsgiPluginHelper {
    private static final Logger logger = LoggerFactory.getLogger(OsgiPluginInstalledHelper.class);
    private final ClassLoader bundleClassLoader;
    private final Bundle bundle;
    private final PackageAdmin packageAdmin;
    private volatile ContainerAccessor containerAccessor;
    private volatile ServiceTracker[] serviceTrackers;

    public OsgiPluginInstalledHelper(Bundle bundle, PackageAdmin packageAdmin) {
        this.bundle = (Bundle)Preconditions.checkNotNull((Object)bundle);
        this.packageAdmin = (PackageAdmin)Preconditions.checkNotNull((Object)packageAdmin);
        this.bundleClassLoader = BundleClassLoaderAccessor.getClassLoader(bundle, (AlternativeResourceLoader)new AlternativeDirectoryResourceLoader());
    }

    @Override
    public Bundle getBundle() {
        return this.bundle;
    }

    @Override
    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) throws ClassNotFoundException {
        return BundleClassLoaderAccessor.loadClass(this.getBundle(), clazz);
    }

    @Override
    public URL getResource(String name) {
        return this.bundleClassLoader.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.bundleClassLoader.getResourceAsStream(name);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.bundleClassLoader;
    }

    @Override
    public Bundle install() {
        logger.debug("Not installing OSGi plugin '{}' since it's already installed.", (Object)this.bundle.getSymbolicName());
        throw new IllegalPluginStateException("Plugin '" + this.bundle.getSymbolicName() + "' has already been installed");
    }

    @Override
    public void onEnable(ServiceTracker ... serviceTrackers) {
        for (ServiceTracker svc : (ServiceTracker[])Preconditions.checkNotNull((Object)serviceTrackers)) {
            svc.open();
        }
        this.serviceTrackers = serviceTrackers;
    }

    @Override
    public void onDisable() {
        ServiceTracker[] serviceTrackers = this.serviceTrackers;
        if (serviceTrackers != null) {
            for (ServiceTracker svc : serviceTrackers) {
                svc.close();
            }
            this.serviceTrackers = null;
        }
        this.setPluginContainer(null);
    }

    @Override
    public void onUninstall() {
    }

    @Override
    @Nonnull
    public PluginDependencies getDependencies() {
        if (this.availableForTraversal()) {
            return OsgiPluginUtil.getDependencies(this.bundle);
        }
        return new PluginDependencies(null, null, null);
    }

    @Deprecated
    private boolean availableForTraversal() {
        if (this.bundle.getState() == 2) {
            logger.debug("Bundle is in INSTALLED for {}", (Object)this.bundle.getSymbolicName());
            if (!this.packageAdmin.resolveBundles(new Bundle[]{this.bundle})) {
                logger.error("Cannot determine required plugins, cannot resolve bundle '{}'", (Object)this.bundle.getSymbolicName());
                return false;
            }
            logger.debug("Bundle state is now {}", (Object)this.bundle.getState());
        }
        return true;
    }

    @Override
    public void setPluginContainer(Object container) {
        this.containerAccessor = container == null ? null : (container instanceof ContainerAccessor ? (ContainerAccessor)container : new DefaultSpringContainerAccessor(container));
    }

    @Override
    public ContainerAccessor getContainerAccessor() {
        return this.containerAccessor;
    }

    @Override
    public ContainerAccessor getRequiredContainerAccessor() {
        if (this.containerAccessor == null) {
            throw new IllegalStateException("Cannot create object because the plugin container is unavailable for bundle '" + this.bundle.getSymbolicName() + "'");
        }
        return this.containerAccessor;
    }

    @Override
    public boolean isRemotePlugin() {
        return Optional.ofNullable(this.getBundle().getHeaders()).map(headers -> headers.get("Remote-Plugin") != null).orElse(false);
    }
}

