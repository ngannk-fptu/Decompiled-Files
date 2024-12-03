/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ContextClassLoaderThreadFactory
 *  com.atlassian.activeobjects.spi.HotRestartEvent
 *  com.atlassian.activeobjects.spi.InitExecutorServiceProvider
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promises
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.activeobjects.osgi;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.internal.ActiveObjectsFactory;
import com.atlassian.activeobjects.osgi.ActiveObjectsDelegate;
import com.atlassian.activeobjects.plugin.ActiveObjectModuleDescriptor;
import com.atlassian.activeobjects.spi.ContextClassLoaderThreadFactory;
import com.atlassian.activeobjects.spi.HotRestartEvent;
import com.atlassian.activeobjects.spi.InitExecutorServiceProvider;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ActiveObjectsServiceFactory
implements ServiceFactory,
InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ActiveObjectsServiceFactory.class);
    private static final String INIT_TASK_TIMEOUT_MS_PROPERTY = "ao-plugin.init.task.timeout";
    private static final String SINGLETON = "singleton";
    @VisibleForTesting
    protected static final int INIT_TASK_TIMEOUT_MS = Integer.getInteger("ao-plugin.init.task.timeout", 30000);
    private final EventPublisher eventPublisher;
    @VisibleForTesting
    final ThreadFactory aoContextThreadFactory;
    @VisibleForTesting
    final LoadingCache<String, ExecutorService> initExecutorsBySingleton;
    @VisibleForTesting
    volatile boolean destroying = false;
    @VisibleForTesting
    volatile boolean cleaning = false;
    @VisibleForTesting
    final Supplier<ExecutorService> initExecutorSp;
    @VisibleForTesting
    final LoadingCache<BundleRef, ActiveObjectsDelegate> aoDelegatesByBundle;
    @VisibleForTesting
    final Map<Bundle, ActiveObjectsConfiguration> unattachedConfigByBundle = new IdentityHashMap<Bundle, ActiveObjectsConfiguration>();
    private final Lock unattachedConfigsLock = new ReentrantLock();

    public ActiveObjectsServiceFactory(final @Nonnull ActiveObjectsFactory factory, @Nonnull EventPublisher eventPublisher, @Nonnull ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, final @Nonnull InitExecutorServiceProvider initExecutorServiceProvider) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        Objects.requireNonNull(factory);
        Objects.requireNonNull(threadLocalDelegateExecutorFactory);
        Objects.requireNonNull(initExecutorServiceProvider);
        ClassLoader bundleContextClassLoader = Thread.currentThread().getContextClassLoader();
        this.aoContextThreadFactory = new ContextClassLoaderThreadFactory(bundleContextClassLoader);
        this.initExecutorsBySingleton = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, ExecutorService>(){

            public ExecutorService load(@Nonnull String tenant) {
                logger.debug("creating new init executor for {}", (Object)tenant);
                return initExecutorServiceProvider.initExecutorService();
            }
        });
        this.initExecutorSp = () -> {
            if (this.destroying) {
                throw new IllegalStateException("applied initExecutorSp after ActiveObjectsServiceFactory destruction");
            }
            if (this.cleaning) {
                throw new IllegalStateException("applied initExecutorSp during ActiveObjects cleaning");
            }
            return (ExecutorService)this.initExecutorsBySingleton.getUnchecked((Object)SINGLETON);
        };
        this.aoDelegatesByBundle = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<BundleRef, ActiveObjectsDelegate>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public ActiveObjectsDelegate load(@Nonnull BundleRef bundleRef) {
                ActiveObjectsDelegate delegate = new ActiveObjectsDelegate(bundleRef.bundle, factory, ActiveObjectsServiceFactory.this.initExecutorSp);
                delegate.init();
                ActiveObjectsServiceFactory.this.unattachedConfigsLock.lock();
                try {
                    ActiveObjectsConfiguration aoConfig = ActiveObjectsServiceFactory.this.unattachedConfigByBundle.get(bundleRef.bundle);
                    if (aoConfig != null) {
                        delegate.setAoConfiguration(aoConfig);
                        ActiveObjectsServiceFactory.this.unattachedConfigByBundle.remove(bundleRef.bundle);
                    }
                }
                finally {
                    ActiveObjectsServiceFactory.this.unattachedConfigsLock.unlock();
                }
                return delegate;
            }
        });
    }

    public void afterPropertiesSet() {
        logger.debug("afterPropertiesSet");
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        logger.debug("destroy");
        this.destroying = true;
        for (ExecutorService initExecutor : this.initExecutorsBySingleton.asMap().values()) {
            initExecutor.shutdownNow();
        }
        for (ActiveObjectsDelegate aoDelegate : this.aoDelegatesByBundle.asMap().values()) {
            aoDelegate.destroy();
        }
        this.eventPublisher.unregister((Object)this);
    }

    public Object getService(Bundle bundle, ServiceRegistration serviceRegistration) {
        Objects.requireNonNull(bundle);
        logger.debug("getService bundle [{}]", (Object)bundle.getSymbolicName());
        if (this.destroying) {
            throw new IllegalStateException("getService after ActiveObjectsServiceFactory destruction");
        }
        return this.aoDelegatesByBundle.getUnchecked((Object)new BundleRef(bundle));
    }

    public void ungetService(Bundle bundle, ServiceRegistration serviceRegistration, Object ao) {
        Objects.requireNonNull(bundle);
        logger.debug("ungetService bundle [{}]", (Object)bundle.getSymbolicName());
        this.aoDelegatesByBundle.invalidate((Object)new BundleRef(bundle));
        if (ao instanceof ActiveObjectsDelegate) {
            ((ActiveObjectsDelegate)ao).destroy();
        }
    }

    public void startCleaning() {
        logger.debug("startCleaning");
        this.cleaning = true;
        for (ExecutorService initExecutor : this.initExecutorsBySingleton.asMap().values()) {
            initExecutor.shutdownNow();
            try {
                if (initExecutor.awaitTermination(INIT_TASK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) continue;
                logger.error("startCleaning timed out after {}ms awaiting init thread completion, continuing; note that this timeout may be adjusted via the system property '{}'", (Object)INIT_TASK_TIMEOUT_MS, (Object)INIT_TASK_TIMEOUT_MS_PROPERTY);
            }
            catch (InterruptedException e) {
                logger.error("startCleaning interrupted while awaiting running init thread completion, continuing", (Throwable)e);
            }
        }
    }

    public void stopCleaning() {
        logger.debug("stopCleaning");
        this.cleaning = false;
    }

    public Promise<List<ActiveObjects>> doHotRestart() {
        ArrayList<Promise<ActiveObjects>> promises = new ArrayList<Promise<ActiveObjects>>();
        logger.debug("onHotRestart performing hot restart");
        ExecutorService initExecutor = (ExecutorService)this.initExecutorsBySingleton.getIfPresent((Object)SINGLETON);
        this.initExecutorsBySingleton.invalidate((Object)SINGLETON);
        for (ActiveObjectsDelegate aoDelegate : ImmutableList.copyOf(this.aoDelegatesByBundle.asMap().values())) {
            String bundleName = aoDelegate.getBundle().getSymbolicName();
            logger.debug("onHotRestart restarting AO delegate for bundle [{}]", (Object)bundleName);
            Promise<ActiveObjects> bundleAOPromise = aoDelegate.restartActiveObjects();
            if (bundleAOPromise == null) {
                logger.warn("Cannot get AO promise for a bundle [{}]. Will try to continue the process.", (Object)bundleName);
                continue;
            }
            promises.add(bundleAOPromise);
        }
        if (initExecutor != null) {
            logger.debug("onHotRestart terminating any initExecutor threads");
            initExecutor.shutdownNow();
        }
        return Promises.when(promises);
    }

    @EventListener
    public void onHotRestart(HotRestartEvent hotRestartEvent) {
        this.doHotRestart();
    }

    @EventListener
    public void onPluginModuleEnabledEvent(PluginModuleEnabledEvent pluginModuleEnabledEvent) {
        Bundle bundle;
        Plugin plugin;
        ModuleDescriptor moduleDescriptor = pluginModuleEnabledEvent.getModule();
        if (moduleDescriptor instanceof ActiveObjectModuleDescriptor && (plugin = moduleDescriptor.getPlugin()) instanceof OsgiPlugin && (bundle = ((OsgiPlugin)plugin).getBundle()) != null) {
            this.tryToAttachAOConfigurationModule((ActiveObjectModuleDescriptor)moduleDescriptor, plugin, bundle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryToAttachAOConfigurationModule(ActiveObjectModuleDescriptor moduleDescriptor, Plugin plugin, Bundle bundle) {
        boolean attachedToDelegate = false;
        ActiveObjectsConfiguration aoConfig = moduleDescriptor.getConfiguration();
        this.unattachedConfigsLock.lock();
        try {
            for (ActiveObjectsDelegate aoDelegate : this.aoDelegatesByBundle.asMap().values()) {
                if (!aoDelegate.getBundle().equals(bundle)) continue;
                logger.debug("onPluginModuleEnabledEvent attaching <ao> configuration module to ActiveObjects service of [{}]", (Object)plugin);
                aoDelegate.setAoConfiguration(aoConfig);
                attachedToDelegate = true;
                break;
            }
            if (!attachedToDelegate) {
                logger.debug("onPluginModuleEnabledEvent storing unattached <ao> configuration module for [{}]", (Object)plugin);
                this.unattachedConfigByBundle.put(bundle, aoConfig);
            }
        }
        finally {
            this.unattachedConfigsLock.unlock();
        }
    }

    @EventListener
    public void onPluginEnabledEvent(PluginEnabledEvent pluginEnabledEvent) {
        Bundle bundle;
        Plugin plugin = pluginEnabledEvent.getPlugin();
        if (plugin instanceof OsgiPlugin && (bundle = ((OsgiPlugin)plugin).getBundle()) != null && this.unattachedConfigByBundle.containsKey(bundle)) {
            logger.debug("onPluginEnabledEvent attaching unbound <ao> to [{}]", (Object)plugin);
            this.aoDelegatesByBundle.getUnchecked((Object)new BundleRef(bundle));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventListener
    public void onPluginDisabledEvent(PluginDisabledEvent pluginDisabledEvent) {
        Bundle bundle;
        Plugin plugin = pluginDisabledEvent.getPlugin();
        if (plugin instanceof OsgiPlugin && (bundle = ((OsgiPlugin)plugin).getBundle()) != null) {
            logger.debug("onPluginDisabledEvent removing delegate for [{}]", (Object)plugin);
            this.aoDelegatesByBundle.invalidate((Object)new BundleRef(bundle));
            this.unattachedConfigsLock.lock();
            try {
                if (this.unattachedConfigByBundle.containsKey(bundle)) {
                    logger.debug("onPluginDisabledEvent removing unbound <ao> for [{}]", (Object)plugin);
                    this.unattachedConfigByBundle.remove(bundle);
                }
            }
            finally {
                this.unattachedConfigsLock.unlock();
            }
        }
    }

    protected static class BundleRef {
        final Bundle bundle;

        public BundleRef(Bundle bundle) {
            this.bundle = Objects.requireNonNull(bundle);
        }

        public boolean equals(Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BundleRef bundleRef = (BundleRef)o;
            return this.bundle == bundleRef.bundle;
        }

        public int hashCode() {
            return System.identityHashCode(this.bundle);
        }
    }
}

