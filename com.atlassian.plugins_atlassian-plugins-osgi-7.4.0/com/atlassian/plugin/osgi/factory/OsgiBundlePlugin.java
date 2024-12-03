/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.IllegalPluginStateException
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.impl.AbstractPlugin
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.SynchronousBundleListener
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.IllegalPluginStateException;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.impl.AbstractPlugin;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.osgi.container.OsgiContainerException;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.factory.OsgiBackedPlugin;
import com.atlassian.plugin.osgi.util.BundleClassLoaderAccessor;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugin.osgi.util.OsgiPluginUtil;
import com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.jar.Manifest;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiBundlePlugin
extends AbstractPlugin
implements OsgiBackedPlugin,
ContainerManagedPlugin,
SynchronousBundleListener {
    private static final Logger log = LoggerFactory.getLogger(OsgiBundlePlugin.class);
    private final Date dateLoaded = new Date();
    private OsgiContainerManager osgiContainerManager;
    private volatile Bundle bundle;
    private ClassLoader bundleClassLoader;
    @Nullable
    private ServiceTracker<ContainerAccessor, ContainerAccessor> containerAccessorTracker;
    @Nullable
    private ServiceTracker<PackageAdmin, PackageAdmin> pkgAdminService;

    private OsgiBundlePlugin(String pluginKey, PluginArtifact pluginArtifact) {
        super((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact));
        this.setPluginsVersion(2);
        this.setKey(pluginKey);
        this.setSystemPlugin(false);
    }

    @Override
    public Bundle getBundle() {
        if (this.bundle == null) {
            throw new IllegalPluginStateException("This operation must occur while the plugin '" + this.getKey() + "' is installed");
        }
        return this.bundle;
    }

    public OsgiBundlePlugin(OsgiContainerManager osgiContainerManager, String pluginKey, PluginArtifact pluginArtifact) {
        this(pluginKey, pluginArtifact);
        this.osgiContainerManager = (OsgiContainerManager)Preconditions.checkNotNull((Object)osgiContainerManager);
        Manifest manifest = OsgiHeaderUtil.getManifest(pluginArtifact);
        if (null != manifest) {
            this.setName(OsgiHeaderUtil.getAttributeWithoutValidation(manifest, "Bundle-Name"));
            this.setPluginInformation(OsgiHeaderUtil.extractOsgiPluginInformation(manifest, false));
        }
    }

    public Date getDateLoaded() {
        return this.dateLoaded;
    }

    public Date getDateInstalled() {
        long date = this.getPluginArtifact().toFile().lastModified();
        if (date == 0L) {
            date = this.getDateLoaded().getTime();
        }
        return new Date(date);
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

    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) throws ClassNotFoundException {
        return BundleClassLoaderAccessor.loadClass(this.getBundleOrFail(), clazz);
    }

    public URL getResource(String name) {
        return this.getBundleClassLoaderOrFail().getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        return this.getBundleClassLoaderOrFail().getResourceAsStream(name);
    }

    public void resolve() {
        if (this.pkgAdminService == null) {
            return;
        }
        PackageAdmin packageAdmin = (PackageAdmin)this.pkgAdminService.getService();
        packageAdmin.resolveBundles(new Bundle[]{this.bundle});
    }

    @Nonnull
    public PluginDependencies getDependencies() {
        if (this.getPluginState() == PluginState.UNINSTALLED) {
            throw new IllegalPluginStateException("This operation requires the plugin '" + this.getKey() + "' to be installed");
        }
        return OsgiPluginUtil.getDependencies(this.bundle);
    }

    protected void installInternal() throws OsgiContainerException, IllegalPluginStateException {
        super.installInternal();
        if (null != this.osgiContainerManager) {
            this.osgiContainerManager.addBundleListener((BundleListener)this);
            File file = this.pluginArtifact.toFile();
            this.bundle = this.osgiContainerManager.installBundle(file, this.pluginArtifact.getReferenceMode());
            this.bundleClassLoader = BundleClassLoaderAccessor.getClassLoader(this.bundle, (AlternativeResourceLoader)new AlternativeDirectoryResourceLoader());
            this.pkgAdminService = this.osgiContainerManager.getServiceTracker(PackageAdmin.class.getName());
        } else if (null == this.bundle) {
            throw new IllegalPluginStateException("Cannot reuse instance for bundle '" + this.getKey() + "'");
        }
    }

    protected void uninstallInternal() {
        try {
            if (this.bundleIsUsable("uninstall")) {
                if (this.bundle.getState() != 1) {
                    if (null != this.osgiContainerManager && this.osgiContainerManager.isRunning()) {
                        this.pkgAdminService.close();
                        this.osgiContainerManager.removeBundleListener((BundleListener)this);
                    } else {
                        log.warn("OSGi container not running or undefined: Will not remove bundle listener and will not close package admin service");
                    }
                    this.bundle.uninstall();
                } else {
                    log.warn("Bundle '{}' already UNINSTALLED, but still held", (Object)this.getKey());
                }
                this.bundle = null;
                this.bundleClassLoader = null;
            }
        }
        catch (BundleException e) {
            throw new PluginException((Throwable)e);
        }
    }

    protected PluginState enableInternal() {
        log.debug("Enabling OSGi bundled plugin '{}'", (Object)this.getKey());
        try {
            if (this.bundleIsUsable("enable")) {
                if (this.bundle.getHeaders().get("Fragment-Host") == null) {
                    log.debug("Plugin '{}' bundle is NOT a fragment, starting.", (Object)this.getKey());
                    this.setPluginState(PluginState.ENABLING);
                    if (this.bundle.getState() == 2 || this.bundle.getState() == 4) {
                        log.debug("Start plugin '{}' bundle", (Object)this.getKey());
                        this.bundle.start();
                    } else {
                        log.debug("Skip plugin '{}' bundle start because of its state: {}", (Object)this.getKey(), (Object)this.bundle.getState());
                    }
                    this.containerAccessorTracker = new ServiceTracker(this.bundle.getBundleContext(), ContainerAccessor.class, (ServiceTrackerCustomizer)new ServiceTrackerCustomizer<ContainerAccessor, ContainerAccessor>(){

                        public ContainerAccessor addingService(ServiceReference<ContainerAccessor> reference) {
                            if (reference.getBundle() == OsgiBundlePlugin.this.bundle) {
                                return (ContainerAccessor)OsgiBundlePlugin.this.bundle.getBundleContext().getService(reference);
                            }
                            return null;
                        }

                        public void modifiedService(ServiceReference<ContainerAccessor> reference, ContainerAccessor service) {
                        }

                        public void removedService(ServiceReference<ContainerAccessor> reference, ContainerAccessor service) {
                            if (reference.getBundle() == OsgiBundlePlugin.this.bundle) {
                                OsgiBundlePlugin.this.bundle.getBundleContext().ungetService(reference);
                            }
                        }
                    });
                    this.containerAccessorTracker.open();
                } else {
                    log.debug("Plugin '{}' bundle is a fragment, not doing anything.", (Object)this.getKey());
                }
            }
            return PluginState.ENABLED;
        }
        catch (BundleException e) {
            throw new PluginException((Throwable)e);
        }
    }

    protected void disableInternal() {
        try {
            if (this.bundleIsUsable("disable")) {
                if (this.bundle.getState() == 32) {
                    if (this.containerAccessorTracker != null) {
                        this.containerAccessorTracker.close();
                    }
                    this.bundle.stop();
                } else {
                    log.warn("Cannot disable Bundle '{}', not ACTIVE", (Object)this.getKey());
                }
            }
        }
        catch (BundleException e) {
            throw new PluginException((Throwable)e);
        }
    }

    public void bundleChanged(BundleEvent event) {
        if (event.getBundle() != this.bundle) {
            return;
        }
        switch (event.getType()) {
            case 2: {
                log.info("Plugin '{}' bundle started: {}", (Object)this.getKey(), (Object)this.getPluginState());
                if (this.getPluginState() == PluginState.ENABLING) break;
                this.enable();
                break;
            }
            case 4: {
                log.info("Plugin '{}' bundle stopped: {}", (Object)this.getKey(), (Object)this.getPluginState());
                if (this.getPluginState() == PluginState.DISABLING) break;
                this.disable();
                break;
            }
        }
    }

    public ContainerAccessor getContainerAccessor() {
        ContainerAccessor tmp;
        ContainerAccessor result = OsgiPluginUtil.createNonExistingPluginContainer(this.getKey());
        if (this.containerAccessorTracker != null && (tmp = (ContainerAccessor)this.containerAccessorTracker.getService()) != null) {
            result = tmp;
        }
        return result;
    }

    public ClassLoader getClassLoader() {
        return this.getBundleClassLoaderOrFail();
    }

    private String getInstallationStateExplanation() {
        return null != this.osgiContainerManager ? "not yet installed" : "already uninstalled";
    }

    private boolean bundleIsUsable(String task) {
        if (null != this.bundle) {
            return true;
        }
        String why = this.getInstallationStateExplanation();
        log.warn("Cannot {} {} bundle '{}'", new Object[]{task, why, this.getKey()});
        return false;
    }

    private <T> T getOrFail(T what, String name) {
        if (null == what) {
            throw new IllegalPluginStateException("Cannot use " + name + " of " + this.getInstallationStateExplanation() + " '" + this.getKey() + "' from '" + this.pluginArtifact + "'");
        }
        return what;
    }

    private Bundle getBundleOrFail() {
        return this.getOrFail(this.bundle, "bundle");
    }

    private ClassLoader getBundleClassLoaderOrFail() {
        return this.getOrFail(this.bundleClassLoader, "bundleClassLoader");
    }
}

