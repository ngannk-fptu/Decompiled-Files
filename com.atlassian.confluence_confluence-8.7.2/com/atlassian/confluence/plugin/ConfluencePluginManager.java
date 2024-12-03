/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginInstaller
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.PluginRegistry$ReadWrite
 *  com.atlassian.plugin.event.NotificationException
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.impl.StaticPlugin
 *  com.atlassian.plugin.loaders.BundledPluginLoader
 *  com.atlassian.plugin.loaders.PluginLoader
 *  com.atlassian.plugin.loaders.SinglePluginLoader
 *  com.atlassian.plugin.manager.DefaultPluginManager
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 *  com.atlassian.plugin.manager.SafeModeManager
 *  com.atlassian.plugin.predicate.PluginKeyPatternsPredicate
 *  com.atlassian.plugin.predicate.PluginKeyPatternsPredicate$MatchType
 *  com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.plugin.AsyncPluginFrameworkStartedEvent;
import com.atlassian.confluence.event.events.plugin.PluginDisableEvent;
import com.atlassian.confluence.event.events.plugin.PluginEnableEvent;
import com.atlassian.confluence.event.events.plugin.PluginEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.PluginModuleDisableEvent;
import com.atlassian.confluence.event.events.plugin.PluginModuleEnableEvent;
import com.atlassian.confluence.event.events.plugin.PluginModuleEvent;
import com.atlassian.confluence.event.events.plugin.PluginUninstallEvent;
import com.atlassian.confluence.event.events.plugin.XWorkStateChangeEvent;
import com.atlassian.confluence.plugin.PluginsClassLoaderAvailableEvent;
import com.atlassian.confluence.plugin.dev.AlternativeDirectoryResourceLoaderSupportingStaticPlugin;
import com.atlassian.confluence.util.logging.LoggingContext;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginInstaller;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.PluginRegistry;
import com.atlassian.plugin.event.NotificationException;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.impl.StaticPlugin;
import com.atlassian.plugin.loaders.BundledPluginLoader;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.loaders.SinglePluginLoader;
import com.atlassian.plugin.manager.DefaultPluginManager;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.manager.SafeModeManager;
import com.atlassian.plugin.predicate.PluginKeyPatternsPredicate;
import com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ConfluencePluginManager
extends DefaultPluginManager
implements ApplicationListener,
ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ConfluencePluginManager.class);
    private AtomicBoolean initialised = new AtomicBoolean(false);
    private EventPublisher eventPublisher;
    private ApplicationContext applicationContext;
    private PluginAccessor pluginAccessor;

    public ConfluencePluginManager(PluginRegistry.ReadWrite pluginRegistry, PluginAccessor pluginAccessor, PluginPersistentStateStore pluginStateStore, List<Object> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, List<String> tenantAwareOrAgnosticPlugins, EventPublisher eventPublisher, PluginInstaller pluginInstaller, SafeModeManager safeModeManager) {
        super(ConfluencePluginManager.newBuilder().withPluginAccessor(pluginAccessor).withPluginRegistry(pluginRegistry).withStore(pluginStateStore).withModuleDescriptorFactory(moduleDescriptorFactory).withPluginLoaders(ConfluencePluginManager.filterPluginLoaders(pluginLoaders)).withPluginEventManager(pluginEventManager).withSafeModeManager(safeModeManager).withDelayLoadOf((Predicate)new PluginKeyPatternsPredicate(PluginKeyPatternsPredicate.MatchType.MATCHES_NONE, tenantAwareOrAgnosticPlugins)));
        this.pluginAccessor = pluginAccessor;
        this.setPluginInstaller(pluginInstaller);
        this.eventPublisher = eventPublisher;
    }

    private static List<PluginLoader> filterPluginLoaders(List<Object> loaderClassNamesOrObjects) {
        ArrayList<PluginLoader> result = new ArrayList<PluginLoader>();
        ListIterator<Object> it = loaderClassNamesOrObjects.listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof String) {
                result.add((PluginLoader)ConfluencePluginManager.createPluginLoaderForDescriptor((String)o));
                continue;
            }
            if (ConfluenceSystemProperties.isBundledPluginsDisabled() && o instanceof BundledPluginLoader) {
                log.info("Bundled plugins have been disabled. Removing bundled plugin loader.");
                continue;
            }
            result.add((PluginLoader)o);
        }
        return result;
    }

    public void init() throws PluginParseException, NotificationException {
        if (!this.initialised.compareAndSet(false, true)) {
            log.warn("Init() called on an already initialised plugin manager. Ignoring.");
            return;
        }
        super.init();
    }

    public void earlyStartup() throws PluginParseException, NotificationException {
        ConfluencePluginManager.setSystemPropertyIfNone("atlassian.enable.spring.strong.cache.bean.metadata", "true");
        ConfluencePluginManager.setSystemPropertyIfNone("atlassian.enable.spring.strong.cache.bean.metadata.flush", "true");
        ConfluencePluginManager.setSystemPropertyIfNone("com.atlassian.plugin.shutdown.asynchronously", "false");
        String bootDelegation = System.getProperty("atlassian.org.osgi.framework.bootdelegation.extra");
        if (bootDelegation == null) {
            System.setProperty("atlassian.org.osgi.framework.bootdelegation.extra", "org.apache.lucene.*");
        } else {
            System.setProperty("atlassian.org.osgi.framework.bootdelegation.extra", bootDelegation + ",org.apache.lucene.*");
        }
        if (this.applicationContext != null) {
            this.applicationContext.publishEvent((ApplicationEvent)new PluginsClassLoaderAvailableEvent((Object)this, (ClassLoader)this.getClassLoader()));
        }
        super.earlyStartup();
    }

    public void lateStartup() throws PluginParseException, NotificationException {
        int earlyStartupPlugins = this.getEnabledPlugins().size();
        super.lateStartup();
        int lateStartupPlugins = this.getEnabledPlugins().size() - earlyStartupPlugins;
        LoggingContext.executeWithContext((String)"lateStartupPlugins", (Object)lateStartupPlugins, () -> log.info("Enabled {} plugins in lateStartup", (Object)lateStartupPlugins));
        this.eventPublisher.publish((Object)new PluginFrameworkStartedEvent((Object)this));
        this.eventPublisher.publish((Object)new AsyncPluginFrameworkStartedEvent((Object)this));
        if (!this.isSetupPluginManager()) {
            this.eventPublisher.publish((Object)new XWorkStateChangeEvent((Object)this));
        }
    }

    protected boolean isSetupPluginManager() {
        return false;
    }

    public final void onApplicationEvent(ApplicationEvent event) {
        if (this.isSetupPluginManager()) {
            return;
        }
        log.debug("onApplicationEvent [ {} ]", (Object)event);
        if (!(event instanceof ClusterEventWrapper)) {
            return;
        }
        Event wrappedEvent = ((ClusterEventWrapper)event).getEvent();
        if (!(wrappedEvent instanceof PluginEvent)) {
            return;
        }
        PluginEvent pluginEvent = (PluginEvent)wrappedEvent;
        log.debug("Received cluster plugin event: " + pluginEvent);
        if (pluginEvent instanceof PluginModuleEvent) {
            this.processModuleEvent((PluginModuleEvent)pluginEvent);
        } else {
            this.processPluginEvent(pluginEvent);
        }
    }

    private void processModuleEvent(PluginModuleEvent pluginEvent) {
        log.debug("processModuleEvent [ {} ]", (Object)pluginEvent);
        ModuleDescriptor module = this.pluginAccessor.getPluginModule(pluginEvent.getPluginKey());
        if (module == null) {
            log.error("Could not process the event [" + pluginEvent + "] for plugin key '" + pluginEvent.getPluginKey() + "' because the component could not be found.");
            return;
        }
        if (pluginEvent instanceof PluginModuleEnableEvent) {
            this.notifyModuleEnabled(module);
            return;
        }
        if (pluginEvent instanceof PluginModuleDisableEvent) {
            this.notifyModuleDisabled(module);
        }
    }

    private void processPluginEvent(PluginEvent pluginEvent) {
        log.debug("processPluginEvent [ {} ]", (Object)pluginEvent);
        if (pluginEvent instanceof PluginInstallEvent) {
            this.processClusteredInstallEvent(pluginEvent);
            return;
        }
        Plugin plugin = this.pluginAccessor.getPlugin(pluginEvent.getPluginKey());
        if (plugin == null) {
            log.error("Could not process the event [" + pluginEvent + "] for plugin key '" + pluginEvent.getPluginKey() + "' because the component could not be found.");
            return;
        }
        if (pluginEvent instanceof PluginUninstallEvent) {
            this.processClusteredUninstallEvent(plugin);
            return;
        }
        if (pluginEvent instanceof PluginEnableEvent) {
            this.enablePlugins(new String[]{pluginEvent.getPluginKey()});
            return;
        }
        if (pluginEvent instanceof PluginDisableEvent) {
            this.disablePluginWithoutPersisting(pluginEvent.getPluginKey());
        }
    }

    private void processClusteredUninstallEvent(Plugin plugin) {
        log.info("Received clustered plugin uninstall event for {}", (Object)plugin.getKey());
        try {
            this.disablePluginWithoutPersisting(plugin.getKey());
            this.unloadPlugin(plugin);
        }
        catch (PluginException e) {
            log.error("Error uninstalling plugin: " + plugin.getKey(), (Throwable)e);
        }
    }

    private void processClusteredInstallEvent(PluginEvent pluginEvent) {
        log.info("Received clustered plugin install event for {}", (Object)pluginEvent.getPluginKey());
        try {
            this.scanForNewPlugins();
        }
        catch (PluginParseException ppe) {
            log.error("Error installing plugin from another node:" + pluginEvent, (Throwable)ppe);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static SinglePluginLoader createPluginLoaderForDescriptor(String pathToPluginDescriptor) {
        if (Boolean.getBoolean("plugin.resource.directories.webapp")) {
            log.info("static plugin {} now supports {} lookups", (Object)pathToPluginDescriptor, (Object)AlternativeDirectoryResourceLoader.class.getSimpleName());
            return new SinglePluginLoader(pathToPluginDescriptor){

                protected StaticPlugin getNewPlugin() {
                    return new AlternativeDirectoryResourceLoaderSupportingStaticPlugin();
                }
            };
        }
        return new SinglePluginLoader(pathToPluginDescriptor);
    }

    private static void setSystemPropertyIfNone(String name, String value) {
        if (System.getProperty(name) == null) {
            System.setProperty(name, value);
        }
    }
}

