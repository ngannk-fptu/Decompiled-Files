/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.IllegalPluginStateException
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginContainerFailedEvent
 *  com.atlassian.plugin.event.events.PluginContainerRefreshedEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.event.events.PluginRefreshedEvent
 *  com.atlassian.plugin.impl.AbstractPlugin
 *  com.atlassian.plugin.manager.PluginTransactionContext
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitEndedEvent
 *  com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitStartingEvent
 *  com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitTimedOutEvent
 *  com.atlassian.plugin.util.PluginUtils
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.IllegalPluginStateException;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginContainerFailedEvent;
import com.atlassian.plugin.event.events.PluginContainerRefreshedEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.event.events.PluginRefreshedEvent;
import com.atlassian.plugin.impl.AbstractPlugin;
import com.atlassian.plugin.manager.PluginTransactionContext;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.osgi.container.OsgiContainerException;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitEndedEvent;
import com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitStartingEvent;
import com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitTimedOutEvent;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.factory.ModuleDescriptorServiceTrackerCustomizer;
import com.atlassian.plugin.osgi.factory.OsgiBackedPlugin;
import com.atlassian.plugin.osgi.factory.OsgiPluginDeinstalledHelper;
import com.atlassian.plugin.osgi.factory.OsgiPluginHelper;
import com.atlassian.plugin.osgi.factory.OsgiPluginInstalledHelper;
import com.atlassian.plugin.osgi.factory.OsgiPluginUninstalledHelper;
import com.atlassian.plugin.osgi.factory.UnrecognizedModuleDescriptorServiceTrackerCustomizer;
import com.atlassian.plugin.util.PluginUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiPlugin
extends AbstractPlugin
implements OsgiBackedPlugin,
ContainerManagedPlugin {
    private static final Logger log = LoggerFactory.getLogger(OsgiPlugin.class);
    public static final String ATLASSIAN_PLUGIN_KEY = "Atlassian-Plugin-Key";
    public static final String ATLASSIAN_SCAN_FOLDERS = "Atlassian-Scan-Folders";
    public static final String REMOTE_PLUGIN_KEY = "Remote-Plugin";
    private final Map<String, Element> moduleElements = new HashMap<String, Element>();
    private final PluginEventManager pluginEventManager;
    private final PluginTransactionContext pluginTransactionContext;
    private final PackageAdmin packageAdmin;
    private final Set<OutstandingDependency> outstandingDependencies = new CopyOnWriteArraySet<OutstandingDependency>();
    private final BundleListener bundleStartStopListener;
    private volatile boolean treatPluginContainerCreationAsRefresh = false;
    private volatile OsgiPluginHelper helper;
    private volatile boolean frameworkStarted = false;
    private volatile boolean frameworkShuttingDown = false;

    public OsgiPlugin(String key, OsgiContainerManager mgr, PluginArtifact artifact, PluginArtifact originalPluginArtifact, PluginEventManager pluginEventManager) {
        super((PluginArtifact)Preconditions.checkNotNull((Object)originalPluginArtifact));
        this.pluginEventManager = (PluginEventManager)Preconditions.checkNotNull((Object)pluginEventManager);
        this.pluginTransactionContext = new PluginTransactionContext(pluginEventManager);
        this.helper = new OsgiPluginUninstalledHelper((String)Preconditions.checkNotNull((Object)key, (Object)"The plugin key is required"), (OsgiContainerManager)Preconditions.checkNotNull((Object)mgr, (Object)"The osgi container is required"), (PluginArtifact)Preconditions.checkNotNull((Object)artifact, (Object)"The plugin artifact is required"));
        this.packageAdmin = this.extractPackageAdminFromOsgi(mgr);
        super.setKey(key);
        this.bundleStartStopListener = bundleEvent -> {
            if (bundleEvent.getBundle() == this.getBundle()) {
                if (bundleEvent.getType() == 256) {
                    this.helper.onDisable();
                    this.setPluginState(PluginState.DISABLED);
                } else if (bundleEvent.getType() == 2) {
                    BundleContext ctx = this.getBundle().getBundleContext();
                    this.helper.onEnable(this.createServiceTrackers(ctx));
                    this.setPluginState(PluginState.ENABLED);
                }
            }
        };
    }

    @VisibleForTesting
    OsgiPlugin(String key, PluginEventManager pluginEventManager, OsgiPluginHelper helper, PackageAdmin packageAdmin) {
        super(null);
        this.helper = helper;
        this.pluginEventManager = pluginEventManager;
        this.pluginTransactionContext = new PluginTransactionContext(pluginEventManager);
        this.packageAdmin = packageAdmin;
        this.bundleStartStopListener = null;
        super.setKey((String)Preconditions.checkNotNull((Object)key, (Object)"The plugin key is required"));
    }

    @Override
    public Bundle getBundle() {
        return this.helper.getBundle();
    }

    public InstallationMode getInstallationMode() {
        return this.helper.isRemotePlugin() ? InstallationMode.REMOTE : InstallationMode.LOCAL;
    }

    public boolean isUninstallable() {
        return true;
    }

    public boolean isDynamicallyLoaded() {
        return true;
    }

    public boolean isDeleteable() {
        return true;
    }

    public Date getDateInstalled() {
        long date = this.getPluginArtifact().toFile().lastModified();
        if (date == 0L) {
            date = this.getDateLoaded().getTime();
        }
        return new Date(date);
    }

    public void setKey(String key) {
        if (!this.getKey().equals(key)) {
            throw new IllegalArgumentException("setKey() should not be called after initialization.");
        }
    }

    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) throws ClassNotFoundException {
        return this.helper.loadClass(clazz, callingClass);
    }

    public URL getResource(String name) {
        return this.helper.getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        return this.helper.getResourceAsStream(name);
    }

    public ClassLoader getClassLoader() {
        return this.helper.getClassLoader();
    }

    @PluginEventListener
    public void onPluginContainerFailed(PluginContainerFailedEvent event) {
        if (this.getKey() == null) {
            throw new IllegalPluginStateException("Plugin key must be set");
        }
        if (this.getKey().equals(event.getPluginKey())) {
            this.logAndClearOustandingDependencies();
            log.error("Unable to start the plugin container for plugin '{}'", (Object)this.getKey(), (Object)event.getCause());
            this.disable();
        }
    }

    @PluginEventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        this.frameworkStarted = true;
    }

    @PluginEventListener
    public void onPluginFrameworkShutdownEvent(PluginFrameworkShutdownEvent event) {
        this.frameworkStarted = false;
    }

    @PluginEventListener
    public void onPluginFrameworkShuttingDownEvent(PluginFrameworkShuttingDownEvent event) {
        this.frameworkShuttingDown = true;
    }

    boolean isFrameworkShuttingDown() {
        return this.frameworkShuttingDown;
    }

    @PluginEventListener
    public void onServiceDependencyWaitStarting(PluginServiceDependencyWaitStartingEvent event) {
        if (event.getPluginKey() != null && event.getPluginKey().equals(this.getKey())) {
            OutstandingDependency dep = new OutstandingDependency(event.getBeanName(), String.valueOf(event.getFilter()));
            this.outstandingDependencies.add(dep);
            log.info("Plugin '{}' waiting for {}", (Object)this.getKey(), (Object)dep);
        }
    }

    @PluginEventListener
    public void onServiceDependencyWaitEnded(PluginServiceDependencyWaitEndedEvent event) {
        if (event.getPluginKey() != null && event.getPluginKey().equals(this.getKey())) {
            OutstandingDependency dep = new OutstandingDependency(event.getBeanName(), String.valueOf(event.getFilter()));
            this.outstandingDependencies.remove(dep);
            log.info("Plugin '{}' found {}", (Object)this.getKey(), (Object)dep);
        }
    }

    @PluginEventListener
    public void onServiceDependencyWaitEnded(PluginServiceDependencyWaitTimedOutEvent event) {
        if (event.getPluginKey() != null && event.getPluginKey().equals(this.getKey())) {
            OutstandingDependency dep = new OutstandingDependency(event.getBeanName(), String.valueOf(event.getFilter()));
            this.outstandingDependencies.remove(dep);
            log.error("Plugin '{}' timeout waiting for {}", (Object)this.getKey(), (Object)dep);
        }
    }

    @PluginEventListener
    public void onPluginContainerRefresh(PluginContainerRefreshedEvent event) {
        if (this.getKey() == null) {
            throw new IllegalPluginStateException("Plugin key must be set");
        }
        if (this.getKey().equals(event.getPluginKey())) {
            this.outstandingDependencies.clear();
            this.helper.setPluginContainer(event.getContainer());
            if (!this.compareAndSetPluginState(PluginState.ENABLING, PluginState.ENABLED) && this.getPluginState() != PluginState.ENABLED) {
                log.warn("Ignoring the bean container that was just created for plugin " + this.getKey() + ". The plugin is in an invalid state, " + this.getPluginState() + ", that doesn't support a transition to enabled. Most likely, it was disabled due to a timeout.");
                this.helper.setPluginContainer(null);
                return;
            }
            if (this.treatPluginContainerCreationAsRefresh) {
                this.pluginTransactionContext.wrap(() -> {
                    PluginRefreshedEvent pluginRefreshedEvent = new PluginRefreshedEvent((Plugin)this);
                    this.pluginTransactionContext.addEvent((Object)pluginRefreshedEvent);
                    this.pluginEventManager.broadcast((Object)pluginRefreshedEvent);
                });
            } else {
                this.treatPluginContainerCreationAsRefresh = true;
            }
        }
    }

    @Nonnull
    public PluginDependencies getDependencies() {
        return this.helper.getDependencies();
    }

    public String toString() {
        return this.getKey();
    }

    protected void installInternal() {
        log.debug("Installing OSGi plugin '{}'", (Object)this.getKey());
        Bundle bundle = this.helper.install();
        this.helper = new OsgiPluginInstalledHelper(bundle, this.packageAdmin);
    }

    protected synchronized PluginState enableInternal() {
        log.debug("Enabling OSGi plugin '{}'", (Object)this.getKey());
        try {
            PluginState stateResult;
            if (this.getBundle().getState() == 32) {
                log.debug("Plugin '{}' bundle is already active, not doing anything", (Object)this.getKey());
                stateResult = PluginState.ENABLED;
            } else if (this.getBundle().getState() == 4 || this.getBundle().getState() == 2) {
                this.pluginEventManager.register((Object)this);
                if (!this.treatPluginContainerCreationAsRefresh) {
                    this.setPluginState(PluginState.ENABLING);
                    stateResult = PluginState.PENDING;
                } else {
                    stateResult = PluginState.ENABLED;
                }
                log.debug("Plugin '{}' bundle is resolved or installed, starting.", (Object)this.getKey());
                this.getBundle().start();
                BundleContext ctx = this.getBundle().getBundleContext();
                this.helper.onEnable(this.createServiceTrackers(ctx));
                ctx.addBundleListener(this.bundleStartStopListener);
            } else {
                throw new OsgiContainerException("Cannot enable the plugin '" + this.getKey() + "' when the bundle is not in the resolved or installed state: " + this.getBundle().getState() + "(" + this.getBundle().getBundleId() + ")");
            }
            return stateResult;
        }
        catch (BundleException e) {
            log.error("Detected an error (BundleException) enabling the plugin '" + this.getKey() + "' : " + e.getMessage() + ".  This error usually occurs when your plugin imports a package from another bundle with a specific version constraint and either the bundle providing that package doesn't meet those version constraints, or there is no bundle available that provides the specified package. For more details on how to fix this, see https://developer.atlassian.com/x/mQAN");
            throw new OsgiContainerException("Cannot start plugin: " + this.getKey(), e);
        }
    }

    private ServiceTracker[] createServiceTrackers(BundleContext ctx) {
        return new ServiceTracker[]{new ServiceTracker(ctx, ModuleDescriptor.class.getName(), (ServiceTrackerCustomizer)new ModuleDescriptorServiceTrackerCustomizer(this, this.pluginEventManager)), new ServiceTracker(ctx, ListableModuleDescriptorFactory.class.getName(), (ServiceTrackerCustomizer)new UnrecognizedModuleDescriptorServiceTrackerCustomizer(this, this.pluginEventManager))};
    }

    protected synchronized void disableInternal() {
        if (!this.requiresRestart()) {
            try {
                if (this.getPluginState() == PluginState.DISABLING) {
                    this.logAndClearOustandingDependencies();
                }
                this.helper.onDisable();
                this.pluginEventManager.unregister((Object)this);
                this.getBundle().stop();
                this.treatPluginContainerCreationAsRefresh = false;
            }
            catch (BundleException e) {
                log.error("Detected an error (BundleException) disabling the plugin '{}' : {}.", (Object)this.getKey(), (Object)e.getMessage());
                throw new OsgiContainerException("Cannot stop plugin: " + this.getKey(), e);
            }
        }
    }

    private boolean requiresRestart() {
        return this.frameworkStarted && PluginUtils.doesPluginRequireRestart((Plugin)this);
    }

    private void logAndClearOustandingDependencies() {
        for (OutstandingDependency dep : this.outstandingDependencies) {
            log.error("Plugin '{}' never resolved {}", (Object)this.getKey(), (Object)dep);
        }
        this.outstandingDependencies.clear();
    }

    protected void uninstallInternal() {
        String key = this.getKey();
        int retryCount = 0;
        BundleException rootCause = null;
        long sleepTime = 500L;
        while (true) {
            try {
                this.pluginEventManager.unregister((Object)this);
                Bundle bundle = this.getBundleIfInstalled();
                if (null != bundle) {
                    boolean remotePlugin = this.helper.isRemotePlugin();
                    if (bundle.getState() != 1) {
                        bundle.uninstall();
                    } else {
                        log.warn("Bundle for '{}' already UNINSTALLED, but still held by helper '{}'", (Object)key, (Object)this.helper);
                    }
                    OsgiPluginHelper oldHelper = this.helper;
                    this.helper = new OsgiPluginDeinstalledHelper(key, remotePlugin);
                    this.setPluginState(PluginState.UNINSTALLED);
                    oldHelper.onUninstall();
                    break;
                }
                log.debug("Trying to uninstall '{}', but it is not installed (helper '{}')", (Object)key, (Object)this.helper);
            }
            catch (BundleException e) {
                BundleException bundleException = rootCause = retryCount == 0 ? e : rootCause;
                if (++retryCount < 3) {
                    log.debug("Possible transient fail on try {} to uninstall '{}', retrying in {} mSecs", new Object[]{retryCount, key, 500L});
                    log.debug(e.getMessage(), (Throwable)e);
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException ei) {
                        throw new OsgiContainerException("Cannot uninstall '" + key + "', retry sleep was interrupted: " + ei.getMessage());
                    }
                    continue;
                }
                log.error("Detected an error (BundleException) disabling the plugin '{}'.", (Object)key);
                log.error(rootCause.getMessage(), (Throwable)rootCause);
                throw new OsgiContainerException("Cannot uninstall '" + key + "'");
            }
            break;
        }
    }

    private Bundle getBundleIfInstalled() {
        try {
            return this.getBundle();
        }
        catch (IllegalPluginStateException eips) {
            return null;
        }
    }

    void addModuleDescriptorElement(String key, Element element) {
        this.moduleElements.put(key, element);
    }

    void clearModuleDescriptor(String key) {
        this.removeModuleDescriptor(key);
    }

    Map<String, Element> getModuleElements() {
        return this.moduleElements;
    }

    private PackageAdmin extractPackageAdminFromOsgi(OsgiContainerManager mgr) {
        Bundle bundle = mgr.getBundles()[0];
        ServiceReference ref = bundle.getBundleContext().getServiceReference(PackageAdmin.class.getName());
        return (PackageAdmin)bundle.getBundleContext().getService(ref);
    }

    public ContainerAccessor getContainerAccessor() {
        return this.helper.getRequiredContainerAccessor();
    }

    public void resolve() {
        this.packageAdmin.resolveBundles(new Bundle[]{this.getBundle()});
    }

    private static class OutstandingDependency {
        private final String beanName;
        private final String filter;

        public OutstandingDependency(String beanName, String filter) {
            this.beanName = beanName;
            this.filter = filter;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            OutstandingDependency that = (OutstandingDependency)o;
            if (this.beanName != null ? !this.beanName.equals(that.beanName) : that.beanName != null) {
                return false;
            }
            return this.filter.equals(that.filter);
        }

        public int hashCode() {
            int result = this.beanName != null ? this.beanName.hashCode() : 0;
            result = 31 * result + this.filter.hashCode();
            return result;
        }

        public String toString() {
            return "service '" + this.beanName + "' with filter '" + this.filter + "'";
        }
    }
}

