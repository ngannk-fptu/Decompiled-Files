/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  com.atlassian.instrumentation.operations.OpTimer
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginDependencies$Type
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.PluginRegistry$ReadOnly
 *  com.atlassian.plugin.PluginRegistry$ReadWrite
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.event.NotificationException
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginContainerUnavailableEvent
 *  com.atlassian.plugin.event.events.PluginDependentsChangedEvent
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginDisablingEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginEnablingEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkDelayedEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkResumingEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartingEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkWarmRestartedEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkWarmRestartingEvent
 *  com.atlassian.plugin.event.events.PluginInstalledEvent
 *  com.atlassian.plugin.event.events.PluginInstallingEvent
 *  com.atlassian.plugin.event.events.PluginModuleAvailableEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisablingEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnablingEvent
 *  com.atlassian.plugin.event.events.PluginModuleUnavailableEvent
 *  com.atlassian.plugin.event.events.PluginRefreshedEvent
 *  com.atlassian.plugin.event.events.PluginUninstalledEvent
 *  com.atlassian.plugin.event.events.PluginUninstallingEvent
 *  com.atlassian.plugin.event.events.PluginUpgradedEvent
 *  com.atlassian.plugin.event.events.PluginUpgradingEvent
 *  com.atlassian.plugin.scope.ScopeManager
 *  com.atlassian.plugin.util.Assertions
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginInstaller;
import com.atlassian.plugin.PluginInternal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.PluginRegistry;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.RevertablePluginInstaller;
import com.atlassian.plugin.SplitStartupPluginSystemLifecycle;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.classloader.PluginsClassLoader;
import com.atlassian.plugin.descriptors.CannotDisable;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptor;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptorFactory;
import com.atlassian.plugin.event.NotificationException;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginContainerUnavailableEvent;
import com.atlassian.plugin.event.events.PluginDependentsChangedEvent;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginDisablingEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginEnablingEvent;
import com.atlassian.plugin.event.events.PluginFrameworkDelayedEvent;
import com.atlassian.plugin.event.events.PluginFrameworkResumingEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartingEvent;
import com.atlassian.plugin.event.events.PluginFrameworkWarmRestartedEvent;
import com.atlassian.plugin.event.events.PluginFrameworkWarmRestartingEvent;
import com.atlassian.plugin.event.events.PluginInstalledEvent;
import com.atlassian.plugin.event.events.PluginInstallingEvent;
import com.atlassian.plugin.event.events.PluginModuleAvailableEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisablingEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnablingEvent;
import com.atlassian.plugin.event.events.PluginModuleUnavailableEvent;
import com.atlassian.plugin.event.events.PluginRefreshedEvent;
import com.atlassian.plugin.event.events.PluginUninstalledEvent;
import com.atlassian.plugin.event.events.PluginUninstallingEvent;
import com.atlassian.plugin.event.events.PluginUpgradedEvent;
import com.atlassian.plugin.event.events.PluginUpgradingEvent;
import com.atlassian.plugin.exception.NoOpPluginExceptionInterception;
import com.atlassian.plugin.exception.PluginExceptionInterception;
import com.atlassian.plugin.impl.AbstractPlugin;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.impl.UnloadablePluginFactory;
import com.atlassian.plugin.instrumentation.PluginSystemInstrumentation;
import com.atlassian.plugin.instrumentation.SingleTimer;
import com.atlassian.plugin.instrumentation.Timer;
import com.atlassian.plugin.loaders.DiscardablePluginLoader;
import com.atlassian.plugin.loaders.DynamicPluginLoader;
import com.atlassian.plugin.loaders.PermissionCheckingPluginLoader;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.manager.DefaultPluginManagerJmxBridge;
import com.atlassian.plugin.manager.DependentPlugins;
import com.atlassian.plugin.manager.NoOpRevertablePluginInstaller;
import com.atlassian.plugin.manager.PluginEnabler;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateModifier;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.manager.PluginRegistryImpl;
import com.atlassian.plugin.manager.PluginStateChangeCountEmitter;
import com.atlassian.plugin.manager.PluginTransactionContext;
import com.atlassian.plugin.manager.PluginsInEnableOrder;
import com.atlassian.plugin.manager.SafeModeManager;
import com.atlassian.plugin.manager.SafeModuleExtractor;
import com.atlassian.plugin.manager.StateTracker;
import com.atlassian.plugin.manager.UnsupportedPluginInstaller;
import com.atlassian.plugin.metadata.ClasspathFilePluginMetadata;
import com.atlassian.plugin.metadata.DefaultRequiredPluginValidator;
import com.atlassian.plugin.predicate.EnabledModulePredicate;
import com.atlassian.plugin.predicate.EnabledPluginPredicate;
import com.atlassian.plugin.predicate.ModuleOfClassPredicate;
import com.atlassian.plugin.scope.ScopeManager;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.util.PluginUtils;
import com.atlassian.plugin.util.VersionStringComparator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPluginManager
implements PluginController,
PluginAccessor,
SplitStartupPluginSystemLifecycle {
    private static final Logger log = LoggerFactory.getLogger(DefaultPluginManager.class);
    private final List<DiscardablePluginLoader> pluginLoaders;
    private final PluginPersistentStateModifier persistentStateModifier;
    private final ModuleDescriptorFactory moduleDescriptorFactory;
    private final PluginEventManager pluginEventManager;
    private final PluginRegistry.ReadWrite pluginRegistry;
    private final PluginsClassLoader classLoader;
    private final PluginEnabler pluginEnabler;
    private final StateTracker tracker;
    private final boolean verifyRequiredPlugins;
    private final Predicate<Plugin> delayLoadOf;
    private RevertablePluginInstaller pluginInstaller;
    private final Map<Plugin, PluginLoader> installedPluginsToPluginLoader;
    private final Map<Plugin, DiscardablePluginLoader> candidatePluginsToPluginLoader;
    private final Collection<Plugin> additionalPluginsToEnable;
    private final DefaultPluginManagerJmxBridge defaultPluginManagerJmxBridge;
    private final List<Plugin> delayedPlugins;
    private final Map<Plugin, DiscardablePluginLoader> delayedPluginRemovalsToLoader;
    private final SafeModuleExtractor safeModuleExtractor;
    private final SafeModeManager safeModeManager;
    private final PluginTransactionContext pluginTransactionContext;

    @Internal
    public static String getStartupOverrideFileProperty() {
        return DefaultPluginManager.class.getName() + ".startupOverrideFile";
    }

    @Internal
    public static String getLateStartupEnableRetryProperty() {
        return DefaultPluginManager.class.getName() + ".lateStartupEnableRetry";
    }

    @Internal
    public static String getMinimumPluginVersionsFileProperty() {
        return DefaultPluginManager.class.getName() + ".minimumPluginVersionsFile";
    }

    public DefaultPluginManager(PluginPersistentStateStore store, List<PluginLoader> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager) {
        this((Builder<? extends Builder>)((Builder)((Builder)DefaultPluginManager.newBuilder().withStore(store).withPluginLoaders(pluginLoaders)).withModuleDescriptorFactory(moduleDescriptorFactory)).withPluginEventManager(pluginEventManager));
    }

    public DefaultPluginManager(PluginPersistentStateStore store, List<PluginLoader> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, PluginExceptionInterception pluginExceptionInterception) {
        this((Builder<? extends Builder>)((Builder)((Builder)((Builder)DefaultPluginManager.newBuilder().withStore(store).withPluginLoaders(pluginLoaders)).withModuleDescriptorFactory(moduleDescriptorFactory)).withPluginEventManager(pluginEventManager)).withPluginExceptionInterception(pluginExceptionInterception));
    }

    public DefaultPluginManager(PluginPersistentStateStore store, List<PluginLoader> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, boolean verifyRequiredPlugins) {
        this((Builder<? extends Builder>)((Builder)((Builder)((Builder)DefaultPluginManager.newBuilder().withStore(store).withPluginLoaders(pluginLoaders)).withModuleDescriptorFactory(moduleDescriptorFactory)).withPluginEventManager(pluginEventManager)).withVerifyRequiredPlugins(verifyRequiredPlugins));
    }

    @ExperimentalApi
    public DefaultPluginManager(PluginPersistentStateStore store, List<PluginLoader> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, Predicate<Plugin> delayLoadOf) {
        this((Builder<? extends Builder>)((Builder)((Builder)((Builder)DefaultPluginManager.newBuilder().withStore(store).withPluginLoaders(pluginLoaders)).withModuleDescriptorFactory(moduleDescriptorFactory)).withPluginEventManager(pluginEventManager)).withDelayLoadOf(delayLoadOf));
    }

    @ExperimentalApi
    public DefaultPluginManager(PluginPersistentStateStore store, List<PluginLoader> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, PluginExceptionInterception pluginExceptionInterception, Predicate<Plugin> delayLoadOf) {
        this((Builder<? extends Builder>)((Builder)((Builder)((Builder)((Builder)DefaultPluginManager.newBuilder().withStore(store).withPluginLoaders(pluginLoaders)).withModuleDescriptorFactory(moduleDescriptorFactory)).withPluginEventManager(pluginEventManager)).withPluginExceptionInterception(pluginExceptionInterception)).withDelayLoadOf(delayLoadOf));
    }

    public DefaultPluginManager(PluginPersistentStateStore store, List<PluginLoader> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, PluginExceptionInterception pluginExceptionInterception, boolean verifyRequiredPlugins) {
        this((Builder<? extends Builder>)((Builder)((Builder)((Builder)((Builder)DefaultPluginManager.newBuilder().withStore(store).withPluginLoaders(pluginLoaders)).withModuleDescriptorFactory(moduleDescriptorFactory)).withPluginEventManager(pluginEventManager)).withPluginExceptionInterception(pluginExceptionInterception)).withVerifyRequiredPlugins(verifyRequiredPlugins));
    }

    public DefaultPluginManager(PluginPersistentStateStore store, List<PluginLoader> pluginLoaders, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, PluginExceptionInterception pluginExceptionInterception, boolean verifyRequiredPlugins, Predicate<Plugin> delayLoadOf) {
        this((Builder<? extends Builder>)((Builder)((Builder)((Builder)((Builder)((Builder)DefaultPluginManager.newBuilder().withStore(store).withPluginLoaders(pluginLoaders)).withModuleDescriptorFactory(moduleDescriptorFactory)).withPluginEventManager(pluginEventManager)).withPluginExceptionInterception(pluginExceptionInterception)).withVerifyRequiredPlugins(verifyRequiredPlugins)).withDelayLoadOf(delayLoadOf));
    }

    protected DefaultPluginManager(Builder<? extends Builder> builder) {
        this.safeModeManager = ((Builder)builder).safeModeManager;
        this.pluginLoaders = this.toPermissionCheckingPluginLoaders((List)Assertions.notNull((String)"Plugin Loaders list", (Object)((Builder)builder).pluginLoaders));
        this.persistentStateModifier = new PluginPersistentStateModifier((PluginPersistentStateStore)Assertions.notNull((String)"PluginPersistentStateStore", (Object)((Builder)builder).store));
        this.moduleDescriptorFactory = (ModuleDescriptorFactory)Assertions.notNull((String)"ModuleDescriptorFactory", (Object)((Builder)builder).moduleDescriptorFactory);
        this.pluginEventManager = (PluginEventManager)Assertions.notNull((String)"PluginEventManager", (Object)((Builder)builder).pluginEventManager);
        this.pluginEnabler = new PluginEnabler(this, this, (PluginExceptionInterception)Assertions.notNull((String)"PluginExceptionInterception", (Object)((Builder)builder).pluginExceptionInterception));
        this.verifyRequiredPlugins = ((Builder)builder).verifyRequiredPlugins;
        this.delayLoadOf = this.wrapDelayPredicateWithOverrides(((Builder)builder).delayLoadOf);
        this.pluginRegistry = ((Builder)builder).pluginRegistry;
        this.classLoader = ((Builder)builder).pluginAccessor.map(pa -> (PluginsClassLoader)PluginsClassLoader.class.cast(pa.getClassLoader())).orElseGet(() -> new PluginsClassLoader(null, this, this.pluginEventManager));
        this.tracker = new StateTracker();
        this.pluginInstaller = new NoOpRevertablePluginInstaller(new UnsupportedPluginInstaller());
        this.installedPluginsToPluginLoader = new HashMap<Plugin, PluginLoader>();
        this.candidatePluginsToPluginLoader = new HashMap<Plugin, DiscardablePluginLoader>();
        this.additionalPluginsToEnable = new ArrayList<Plugin>();
        this.delayedPlugins = new ArrayList<Plugin>();
        this.delayedPluginRemovalsToLoader = new HashMap<Plugin, DiscardablePluginLoader>();
        this.pluginEventManager.register((Object)this);
        this.defaultPluginManagerJmxBridge = new DefaultPluginManagerJmxBridge(this);
        this.safeModuleExtractor = new SafeModuleExtractor(this);
        this.pluginTransactionContext = new PluginTransactionContext(this.pluginEventManager);
    }

    public static Builder<? extends Builder<?>> newBuilder() {
        return new Builder();
    }

    private static Iterable<String> toPluginKeys(Iterable<Plugin> plugins) {
        return StreamSupport.stream(plugins.spliterator(), false).map(Plugin::getKey).collect(Collectors.toList());
    }

    private List<DiscardablePluginLoader> toPermissionCheckingPluginLoaders(List<PluginLoader> fromIterable) {
        return fromIterable.stream().map(PermissionCheckingPluginLoader::new).collect(Collectors.toList());
    }

    private Predicate<Plugin> wrapDelayPredicateWithOverrides(final Predicate<Plugin> pluginPredicate) {
        final Map<String, String> startupOverridesMap = this.parseFileNamedByPropertyAsMap(DefaultPluginManager.getStartupOverrideFileProperty());
        return new Predicate<Plugin>(){

            @Override
            public boolean test(Plugin plugin) {
                String pluginKey = plugin.getKey();
                String stringFromFile = (String)startupOverridesMap.get(pluginKey);
                Optional<Boolean> parsedFromFile = this.parseStartupToDelay(stringFromFile, pluginKey, "override file");
                if (parsedFromFile.isPresent()) {
                    return parsedFromFile.get();
                }
                PluginInformation pluginInformation = plugin.getPluginInformation();
                String stringFromInformation = null != pluginInformation ? pluginInformation.getStartup() : null;
                Optional<Boolean> parsedFromInformation = this.parseStartupToDelay(stringFromInformation, pluginKey, "PluginInformation");
                return parsedFromInformation.orElseGet(() -> pluginPredicate.test(plugin));
            }

            private Optional<Boolean> parseStartupToDelay(String startup, String pluginKey, String source) {
                if (null != startup) {
                    if ("early".equals(startup)) {
                        return Optional.of(Boolean.FALSE);
                    }
                    if ("late".equals(startup)) {
                        return Optional.of(Boolean.TRUE);
                    }
                    log.warn("Unknown startup '{}' for plugin '{}' from {}", new Object[]{startup, pluginKey, source});
                }
                return Optional.empty();
            }
        };
    }

    private Map<String, String> parseFileNamedByPropertyAsMap(String property) {
        Properties properties = new Properties();
        String fileName = System.getProperty(property);
        if (null != fileName) {
            try (FileInputStream inStream = new FileInputStream(fileName);){
                properties.load(inStream);
            }
            catch (IOException eio) {
                log.warn("Failed to load file named by property {}, that is '{}'.", new Object[]{property, fileName, eio});
            }
        }
        return Collections.unmodifiableMap(this.propertiesToMap(properties));
    }

    private Map<String, String> propertiesToMap(Properties properties) {
        HashMap<String, String> propertiesMap = new HashMap<String, String>();
        properties.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(key, value) -> propertiesMap.put((String)key, (String)value)));
        return propertiesMap;
    }

    public void init() {
        this.pluginTransactionContext.wrap(() -> {
            this.earlyStartup();
            this.lateStartup();
        });
    }

    @Override
    @ExperimentalApi
    public void earlyStartup() {
        this.pluginTransactionContext.wrap(() -> {
            try (SingleTimer timer = PluginSystemInstrumentation.instance().pullSingleTimer("earlyStartup");){
                log.info("Plugin system earlyStartup begun");
                this.tracker.setState(StateTracker.State.STARTING);
                this.defaultPluginManagerJmxBridge.register();
                this.broadcastIgnoreError(new PluginFrameworkStartingEvent((PluginController)this, (PluginAccessor)this));
                this.pluginInstaller.clearBackups();
                PluginPersistentState pluginPersistentState = this.getState();
                TreeMap<String, List> candidatePluginKeyToVersionedPlugins = new TreeMap<String, List>();
                for (DiscardablePluginLoader discardablePluginLoader : this.pluginLoaders) {
                    if (discardablePluginLoader == null) continue;
                    Iterable<Plugin> possiblePluginsToLoad = discardablePluginLoader.loadAllPlugins(this.moduleDescriptorFactory);
                    if (log.isDebugEnabled()) {
                        log.debug("Found {} plugins to possibly load: {}", (Object)StreamSupport.stream(possiblePluginsToLoad.spliterator(), false).count(), DefaultPluginManager.toPluginKeys(possiblePluginsToLoad));
                    }
                    for (Plugin plugin : possiblePluginsToLoad) {
                        if (pluginPersistentState.getPluginRestartState(plugin.getKey()) == PluginRestartState.REMOVE) {
                            log.info("Plugin {} was marked to be removed on restart. Removing now.", (Object)plugin);
                            this.delayedPluginRemovalsToLoader.put(plugin, discardablePluginLoader);
                            continue;
                        }
                        this.candidatePluginsToPluginLoader.put(plugin, discardablePluginLoader);
                        List plugins = candidatePluginKeyToVersionedPlugins.computeIfAbsent(plugin.getKey(), key -> new ArrayList());
                        plugins.add(plugin);
                    }
                }
                ArrayList<Plugin> pluginsToInstall = new ArrayList<Plugin>();
                for (Iterator plugins : candidatePluginKeyToVersionedPlugins.values()) {
                    Plugin plugin = (Plugin)Collections.max(plugins, Comparator.naturalOrder());
                    if (plugins.size() > 1) {
                        log.debug("Plugin {} contained multiple versions. installing version {}.", (Object)plugin.getKey(), (Object)plugin.getPluginInformation().getVersion());
                    }
                    pluginsToInstall.add(plugin);
                }
                ArrayList<Plugin> arrayList = new ArrayList<Plugin>();
                for (Plugin plugin : pluginsToInstall) {
                    if (this.delayLoadOf.test(plugin)) {
                        this.delayedPlugins.add(plugin);
                        continue;
                    }
                    arrayList.add(plugin);
                }
                this.addPlugins(null, arrayList);
                for (Plugin plugin : arrayList) {
                    this.candidatePluginsToPluginLoader.remove(plugin);
                }
                if (Boolean.getBoolean(DefaultPluginManager.getLateStartupEnableRetryProperty())) {
                    for (Plugin plugin : arrayList) {
                        if (PluginState.ENABLED == plugin.getPluginState() || !pluginPersistentState.isEnabled(plugin)) continue;
                        this.additionalPluginsToEnable.add(plugin);
                    }
                    if (!this.additionalPluginsToEnable.isEmpty()) {
                        log.warn("Failed to enable some ({}) early plugins, will fallback during lateStartup. Plugins: {}", (Object)this.additionalPluginsToEnable.size(), this.additionalPluginsToEnable);
                    }
                }
                HashMap<Plugin, DiscardablePluginLoader> delayedPluginsLoaders = new HashMap<Plugin, DiscardablePluginLoader>();
                for (Plugin plugin : this.delayedPlugins) {
                    DiscardablePluginLoader loader = this.candidatePluginsToPluginLoader.remove(plugin);
                    delayedPluginsLoaders.put(plugin, loader);
                }
                for (Map.Entry entry : this.candidatePluginsToPluginLoader.entrySet()) {
                    Plugin plugin = (Plugin)entry.getKey();
                    DiscardablePluginLoader loader = (DiscardablePluginLoader)entry.getValue();
                    loader.discardPlugin(plugin);
                }
                this.candidatePluginsToPluginLoader.clear();
                this.candidatePluginsToPluginLoader.putAll(delayedPluginsLoaders);
                this.tracker.setState(StateTracker.State.DELAYED);
                this.logTime(timer, "Plugin system earlyStartup ended");
                this.broadcastIgnoreError(new PluginFrameworkDelayedEvent((PluginController)this, (PluginAccessor)this));
            }
        });
    }

    @Override
    @ExperimentalApi
    public void lateStartup() {
        this.pluginTransactionContext.wrap(() -> {
            try (SingleTimer timer = PluginSystemInstrumentation.instance().pullSingleTimer("lateStartup");){
                log.info("Plugin system lateStartup begun");
                this.tracker.setState(StateTracker.State.RESUMING);
                this.broadcastIgnoreError(new PluginFrameworkResumingEvent((PluginController)this, (PluginAccessor)this));
                this.addPlugins(null, this.delayedPlugins);
                this.delayedPlugins.clear();
                this.candidatePluginsToPluginLoader.clear();
                this.persistentStateModifier.clearPluginRestartState();
                for (Map.Entry<Plugin, DiscardablePluginLoader> entry : this.delayedPluginRemovalsToLoader.entrySet()) {
                    Plugin plugin = entry.getKey();
                    DiscardablePluginLoader loader = entry.getValue();
                    loader.removePlugin(plugin);
                    this.persistentStateModifier.removeState(plugin);
                }
                this.delayedPluginRemovalsToLoader.clear();
                this.logTime(timer, "Plugin system lateStartup ended");
                this.tracker.setState(StateTracker.State.STARTED);
                if (this.verifyRequiredPlugins) {
                    this.validateRequiredPlugins();
                }
                this.broadcastIgnoreError(new PluginFrameworkStartedEvent((PluginController)this, (PluginAccessor)this));
            }
        });
    }

    private void validateRequiredPlugins() {
        DefaultRequiredPluginValidator validator = new DefaultRequiredPluginValidator(this, new ClasspathFilePluginMetadata());
        Collection<String> errors = validator.validate();
        if (!errors.isEmpty()) {
            log.error("Unable to validate required plugins or modules - plugin system shutting down");
            log.error("Failures:");
            for (String error : errors) {
                log.error("\t{}", (Object)error);
            }
            this.shutdown();
            throw new PluginException("Unable to validate required plugins or modules");
        }
    }

    private void logTime(Timer timer, String message) {
        Optional<OpTimer> opTimer = timer.getOpTimer();
        if (opTimer.isPresent()) {
            long elapsedSeconds = opTimer.get().snapshot().getElapsedTotalTime(TimeUnit.SECONDS);
            log.info("{} in {}s", (Object)message, (Object)elapsedSeconds);
        } else {
            log.info(message);
        }
    }

    public void shutdown() {
        this.pluginTransactionContext.wrap(() -> {
            try (SingleTimer ignored = PluginSystemInstrumentation.instance().pullSingleTimer("shutdown");){
                this.tracker.setState(StateTracker.State.SHUTTING_DOWN);
                log.info("Preparing to shut down the plugin system");
                this.broadcastIgnoreError(new PluginFrameworkShuttingDownEvent((PluginController)this, (PluginAccessor)this));
                log.info("Shutting down the plugin system");
                this.broadcastIgnoreError(new PluginFrameworkShutdownEvent((PluginController)this, (PluginAccessor)this));
                this.pluginRegistry.clear();
                this.pluginEventManager.unregister((Object)this);
                this.tracker.setState(StateTracker.State.SHUTDOWN);
                this.defaultPluginManagerJmxBridge.unregister();
            }
        });
    }

    public final void warmRestart() {
        this.pluginTransactionContext.wrap(() -> {
            this.tracker.setState(StateTracker.State.WARM_RESTARTING);
            log.info("Initiating a warm restart of the plugin system");
            this.broadcastIgnoreError(new PluginFrameworkWarmRestartingEvent((PluginController)this, (PluginAccessor)this));
            ArrayList<Plugin> restartedPlugins = new ArrayList<Plugin>();
            ArrayList<DiscardablePluginLoader> loaders = new ArrayList<DiscardablePluginLoader>(this.pluginLoaders);
            Collections.reverse(loaders);
            for (PluginLoader pluginLoader : this.pluginLoaders) {
                for (Map.Entry<Plugin, PluginLoader> entry : this.installedPluginsToPluginLoader.entrySet()) {
                    Plugin plugin;
                    if (entry.getValue() != pluginLoader || !this.isPluginEnabled((plugin = entry.getKey()).getKey())) continue;
                    this.disablePluginModules(plugin);
                    restartedPlugins.add(plugin);
                }
            }
            Collections.reverse(restartedPlugins);
            for (Plugin plugin : restartedPlugins) {
                this.enableConfiguredPluginModules(plugin);
            }
            this.broadcastIgnoreError(new PluginFrameworkWarmRestartedEvent((PluginController)this, (PluginAccessor)this));
            this.tracker.setState(StateTracker.State.STARTED);
        });
    }

    @PluginEventListener
    public void onPluginModuleAvailable(PluginModuleAvailableEvent event) {
        this.pluginTransactionContext.wrap(() -> this.enableConfiguredPluginModule(event.getModule().getPlugin(), event.getModule(), new HashSet()));
    }

    @PluginEventListener
    public void onPluginModuleUnavailable(PluginModuleUnavailableEvent event) {
        this.pluginTransactionContext.wrap(() -> this.disablePluginModuleNoPersist(event.getModule()));
    }

    @PluginEventListener
    public void onPluginContainerUnavailable(PluginContainerUnavailableEvent event) {
        this.pluginTransactionContext.wrap(() -> this.disablePluginWithoutPersisting(event.getPluginKey()));
    }

    @PluginEventListener
    public void onPluginRefresh(PluginRefreshedEvent event) {
        this.pluginTransactionContext.wrap(() -> {
            Plugin plugin = event.getPlugin();
            this.disablePluginModules(plugin);
            this.broadcastIgnoreError(new PluginEnablingEvent(plugin));
            if (this.enableConfiguredPluginModules(plugin)) {
                this.broadcastPluginEnabled(plugin);
            }
        });
    }

    public void setPluginInstaller(PluginInstaller pluginInstaller) {
        this.pluginInstaller = pluginInstaller instanceof RevertablePluginInstaller ? (RevertablePluginInstaller)pluginInstaller : new NoOpRevertablePluginInstaller(pluginInstaller);
    }

    public Set<String> installPlugins(PluginArtifact ... pluginArtifacts) {
        LinkedHashMap validatedArtifacts = new LinkedHashMap();
        this.pluginTransactionContext.wrap(() -> {
            try {
                for (PluginArtifact pluginArtifact : pluginArtifacts) {
                    validatedArtifacts.put(this.validatePlugin(pluginArtifact), pluginArtifact);
                }
            }
            catch (PluginParseException ex) {
                throw new PluginParseException("All plugins could not be validated", (Throwable)ex);
            }
            for (Map.Entry entry : validatedArtifacts.entrySet()) {
                this.pluginInstaller.installPlugin((String)entry.getKey(), (PluginArtifact)entry.getValue());
            }
            this.scanForNewPlugins();
        });
        return validatedArtifacts.keySet();
    }

    String validatePlugin(PluginArtifact pluginArtifact) {
        boolean foundADynamicPluginLoader = false;
        for (PluginLoader pluginLoader : this.pluginLoaders) {
            if (!pluginLoader.isDynamicPluginLoader()) continue;
            foundADynamicPluginLoader = true;
            String key = ((DynamicPluginLoader)pluginLoader).canLoad(pluginArtifact);
            if (key == null) continue;
            return key;
        }
        if (!foundADynamicPluginLoader) {
            throw new IllegalStateException("Should be at least one DynamicPluginLoader in the plugin loader list");
        }
        throw new PluginParseException("Jar " + pluginArtifact.getName() + " is not a valid plugin!");
    }

    public int scanForNewPlugins() {
        StateTracker.State state = this.tracker.get();
        Preconditions.checkState((StateTracker.State.RESUMING == state || StateTracker.State.STARTED == state ? 1 : 0) != 0, (String)"Cannot scanForNewPlugins in state %s", (Object)((Object)state));
        AtomicInteger numberFound = new AtomicInteger(0);
        this.pluginTransactionContext.wrap(() -> {
            Iterator<DiscardablePluginLoader> iterator = this.pluginLoaders.iterator();
            while (iterator.hasNext()) {
                PluginLoader pluginLoader = iterator.next();
                if (pluginLoader == null || !pluginLoader.supportsAddition()) continue;
                ArrayList<Plugin> pluginsToAdd = new ArrayList<Plugin>();
                for (Plugin plugin : pluginLoader.loadFoundPlugins(this.moduleDescriptorFactory)) {
                    block9: {
                        Plugin oldPlugin = this.pluginRegistry.get(plugin.getKey());
                        if (plugin instanceof UnloadablePlugin) continue;
                        if (PluginUtils.doesPluginRequireRestart(plugin)) {
                            if (oldPlugin == null) {
                                this.markPluginInstallThatRequiresRestart(plugin);
                                UnloadablePlugin unloadablePlugin = UnloadablePluginFactory.createUnloadablePlugin(plugin);
                                unloadablePlugin.setErrorText("Plugin requires a restart of the application due to the following modules: " + PluginUtils.getPluginModulesThatRequireRestart(plugin));
                                plugin = unloadablePlugin;
                                break block9;
                            } else {
                                if (PluginRestartState.INSTALL.equals((Object)this.getPluginRestartState(plugin.getKey()))) continue;
                                this.markPluginUpgradeThatRequiresRestart(plugin);
                                continue;
                            }
                        }
                        if (oldPlugin != null && PluginUtils.doesPluginRequireRestart(oldPlugin)) {
                            if (PluginRestartState.INSTALL.equals((Object)this.getPluginRestartState(oldPlugin.getKey()))) {
                                this.revertRestartRequiredChange(oldPlugin.getKey());
                            } else {
                                this.markPluginUpgradeThatRequiresRestart(plugin);
                                continue;
                            }
                        }
                    }
                    pluginsToAdd.add(plugin);
                }
                this.addPlugins(pluginLoader, pluginsToAdd);
                numberFound.addAndGet(pluginsToAdd.size());
            }
            return;
        });
        return numberFound.get();
    }

    private void markPluginInstallThatRequiresRestart(Plugin plugin) {
        log.info("Installed plugin '{}' requires a restart due to the following modules: {}", (Object)plugin, PluginUtils.getPluginModulesThatRequireRestart(plugin));
        this.updateRequiresRestartState(plugin.getKey(), PluginRestartState.INSTALL);
    }

    private void markPluginUpgradeThatRequiresRestart(Plugin plugin) {
        log.info("Upgraded plugin '{}' requires a restart due to the following modules: {}", (Object)plugin, PluginUtils.getPluginModulesThatRequireRestart(plugin));
        this.updateRequiresRestartState(plugin.getKey(), PluginRestartState.UPGRADE);
    }

    private void markPluginUninstallThatRequiresRestart(Plugin plugin) {
        log.info("Uninstalled plugin '{}' requires a restart due to the following modules: {}", (Object)plugin, PluginUtils.getPluginModulesThatRequireRestart(plugin));
        this.updateRequiresRestartState(plugin.getKey(), PluginRestartState.REMOVE);
    }

    private void updateRequiresRestartState(String pluginKey, PluginRestartState pluginRestartState) {
        this.persistentStateModifier.setPluginRestartState(pluginKey, pluginRestartState);
        this.onUpdateRequiresRestartState(pluginKey, pluginRestartState);
    }

    protected void onUpdateRequiresRestartState(String pluginKey, PluginRestartState pluginRestartState) {
    }

    public void uninstall(Plugin plugin) {
        this.pluginTransactionContext.wrap(() -> this.uninstallPlugins(Collections.singletonList(plugin)));
    }

    public void uninstallPlugins(Collection<Plugin> plugins) {
        this.pluginTransactionContext.wrap(() -> {
            Map requireRestart = plugins.stream().collect(Collectors.partitioningBy(PluginUtils::doesPluginRequireRestart, Collectors.toSet()));
            requireRestart.get(true).forEach(plugin -> {
                this.ensurePluginAndLoaderSupportsUninstall((Plugin)plugin);
                this.markPluginUninstallThatRequiresRestart((Plugin)plugin);
            });
            Set<Plugin> pluginsToDisable = requireRestart.get(false);
            if (!pluginsToDisable.isEmpty()) {
                DependentPlugins disabledPlugins = this.disablePluginsAndTheirDependencies(pluginsToDisable.stream().map(Plugin::getKey).collect(Collectors.toList()), Collections.unmodifiableSet(new HashSet<PluginDependencies.Type>(Arrays.asList(PluginDependencies.Type.MANDATORY, PluginDependencies.Type.OPTIONAL, PluginDependencies.Type.DYNAMIC))));
                disabledPlugins.getPluginsByTypes(Collections.singleton(PluginDependencies.Type.MANDATORY), true).forEach(this.persistentStateModifier::disable);
                pluginsToDisable.forEach(p -> this.broadcastIgnoreError(new PluginUninstallingEvent(p)));
                pluginsToDisable.forEach(this::uninstallNoEvent);
                pluginsToDisable.forEach(p -> this.broadcastIgnoreError(new PluginUninstalledEvent(p)));
                this.reenableDependent(pluginsToDisable, disabledPlugins, PluginState.UNINSTALLED);
            }
        });
    }

    protected void uninstallNoEvent(Plugin plugin) {
        this.unloadPlugin(plugin);
        this.persistentStateModifier.removeState(plugin);
    }

    public void revertRestartRequiredChange(String pluginKey) {
        this.pluginTransactionContext.wrap(() -> {
            Assertions.notNull((String)"pluginKey", (Object)pluginKey);
            PluginRestartState restartState = this.getState().getPluginRestartState(pluginKey);
            if (restartState == PluginRestartState.UPGRADE) {
                this.pluginInstaller.revertInstalledPlugin(pluginKey);
            } else if (restartState == PluginRestartState.INSTALL) {
                this.pluginInstaller.revertInstalledPlugin(pluginKey);
                this.pluginRegistry.remove(pluginKey);
            }
            this.updateRequiresRestartState(pluginKey, PluginRestartState.NONE);
        });
    }

    protected void removeStateFromStore(PluginPersistentStateStore stateStore, Plugin plugin) {
        new PluginPersistentStateModifier(stateStore).removeState(plugin);
    }

    protected void unloadPlugin(Plugin plugin) {
        this.pluginTransactionContext.wrap(() -> {
            PluginLoader loader = this.ensurePluginAndLoaderSupportsUninstall(plugin);
            if (this.isPluginEnabled(plugin.getKey())) {
                this.notifyPluginDisabled(plugin);
            }
            this.notifyUninstallPlugin(plugin);
            if (loader != null) {
                this.removePluginFromLoader(plugin);
            }
            this.pluginRegistry.remove(plugin.getKey());
        });
    }

    private PluginLoader ensurePluginAndLoaderSupportsUninstall(Plugin plugin) {
        if (!plugin.isUninstallable()) {
            throw new PluginException("Plugin is not uninstallable: " + plugin);
        }
        PluginLoader loader = this.installedPluginsToPluginLoader.get(plugin);
        if (loader != null && !loader.supportsRemoval()) {
            throw new PluginException("Not uninstalling plugin - loader doesn't allow removal. Plugin: " + plugin);
        }
        return loader;
    }

    private void removePluginFromLoader(Plugin plugin) {
        if (plugin.isUninstallable()) {
            PluginLoader pluginLoader = this.installedPluginsToPluginLoader.get(plugin);
            pluginLoader.removePlugin(plugin);
        }
        this.installedPluginsToPluginLoader.remove(plugin);
    }

    protected void notifyUninstallPlugin(Plugin plugin) {
        this.classLoader.notifyUninstallPlugin(plugin);
        for (ModuleDescriptor descriptor : plugin.getModuleDescriptors()) {
            descriptor.destroy();
        }
    }

    protected PluginPersistentState getState() {
        return this.persistentStateModifier.getState();
    }

    protected void addPlugins(@Nullable PluginLoader loader, Collection<Plugin> pluginsToInstall) {
        this.pluginTransactionContext.wrap(() -> this.lambda$addPlugins$22(pluginsToInstall, loader));
    }

    private boolean isPluginEnabledInSafeMode(Plugin plugin, Collection<Plugin> pluginsToInstall) {
        return this.safeModeManager.pluginShouldBeStarted(plugin, this.getModuleDescriptors(pluginsToInstall, moduleDescriptor -> true).collect(Collectors.toList()));
    }

    private void enableDependentPlugins(Collection<Plugin> pluginsToEnable) {
        this.pluginTransactionContext.wrap(() -> {
            if (pluginsToEnable.isEmpty()) {
                log.debug("No dependent plugins found to enable.");
                return;
            }
            List<Plugin> pluginsInEnableOrder = new PluginsInEnableOrder(pluginsToEnable, (PluginRegistry.ReadOnly)this.pluginRegistry).get();
            if (log.isDebugEnabled()) {
                log.debug("Found {} plugins to enable: {}", (Object)pluginsInEnableOrder.size(), DefaultPluginManager.toPluginKeys(pluginsInEnableOrder));
            }
            for (Plugin plugin : pluginsInEnableOrder) {
                this.broadcastIgnoreError(new PluginEnablingEvent(plugin));
            }
            this.pluginEnabler.enable(pluginsInEnableOrder);
            for (Plugin plugin : this.additionalPluginsToEnable) {
                if (PluginState.ENABLED != plugin.getPluginState()) continue;
                log.warn("Plugin {} was early but failed to enable, but was fallback enabled in lateStartup. It likely has dependencies on plugins which are late, in which case you should fix those plugins and make them early, or as a last resort make the offending plugin late", (Object)plugin);
            }
            this.additionalPluginsToEnable.clear();
            for (Plugin plugin : pluginsInEnableOrder) {
                if (plugin.getPluginState() != PluginState.ENABLED || !this.enableConfiguredPluginModules(plugin)) continue;
                this.broadcastPluginEnabled(plugin);
            }
        });
    }

    private void discardPlugin(@Nullable PluginLoader loader, Plugin plugin) {
        if (null == loader) {
            this.candidatePluginsToPluginLoader.get(plugin).discardPlugin(plugin);
        } else if (loader instanceof DiscardablePluginLoader) {
            ((DiscardablePluginLoader)loader).discardPlugin(plugin);
        } else {
            log.debug("Ignoring discardPlugin({}, version {}) as delegate is not a DiscardablePluginLoader", (Object)plugin.getKey(), (Object)plugin.getPluginInformation().getVersion());
        }
    }

    private boolean pluginVersionIsAcceptable(Plugin plugin, Map<String, String> minimumPluginVersions) {
        String pluginKey = plugin.getKey();
        String rawMinimumVersion = minimumPluginVersions.get(pluginKey);
        if (null == rawMinimumVersion) {
            return true;
        }
        String cleanMinimumVersion = AbstractPlugin.cleanVersionString(rawMinimumVersion);
        try {
            PluginInformation pluginInformation = plugin.getPluginInformation();
            String pluginVersion = AbstractPlugin.cleanVersionString(pluginInformation != null ? pluginInformation.getVersion() : null);
            VersionStringComparator versionStringComparator = new VersionStringComparator();
            return versionStringComparator.compare(pluginVersion, cleanMinimumVersion) >= 0;
        }
        catch (IllegalArgumentException e_ia) {
            log.warn("Cannot compare minimum version '{}' for plugin {}: {}", new Object[]{rawMinimumVersion, plugin, e_ia.getMessage()});
            return true;
        }
    }

    private DependentPlugins disablePluginsAndTheirDependencies(Collection<String> pluginKeys, Set<PluginDependencies.Type> dependencyTypes) {
        return this.disablePlugins(pluginKeys, dependencyTypes, true);
    }

    private DependentPlugins disableOnlyPluginDependencies(Collection<String> pluginKeys, Set<PluginDependencies.Type> dependencyTypes) {
        return this.disablePlugins(pluginKeys, dependencyTypes, false);
    }

    private DependentPlugins disablePlugins(Collection<String> rootPluginKeys, Set<PluginDependencies.Type> dependencyTypes, boolean disableRoots) {
        DependentPlugins dependentPlugins = new DependentPlugins(rootPluginKeys, this.getEnabledPlugins(), dependencyTypes);
        this.pluginTransactionContext.wrap(() -> {
            List<Plugin> pluginsToDisable;
            List<Plugin> list = pluginsToDisable = disableRoots ? dependentPlugins.getPlugins(true) : dependentPlugins.getPlugins(false);
            if (!pluginsToDisable.isEmpty()) {
                log.info("To disable plugins '{}', we need to first disable all dependent enabled plugins: {}", (Object)rootPluginKeys, dependentPlugins.toStringList());
                for (Plugin p : pluginsToDisable) {
                    this.broadcastPluginDisabling(p);
                }
                for (Plugin p : pluginsToDisable) {
                    this.disablePluginWithModuleEvents(p);
                }
                for (Plugin p : pluginsToDisable) {
                    this.broadcastPluginDisabled(p);
                }
            }
        });
        return dependentPlugins;
    }

    protected void updatePlugin(Plugin oldPlugin, Plugin newPlugin) {
        this.pluginTransactionContext.wrap(() -> {
            if (!oldPlugin.getKey().equals(newPlugin.getKey())) {
                throw new IllegalArgumentException("New plugin '" + newPlugin + "' must have the same key as the old plugin '" + oldPlugin + "'");
            }
            if (log.isInfoEnabled()) {
                PluginInformation oldInformation = oldPlugin.getPluginInformation();
                String oldVersion = oldInformation == null ? "?" : oldInformation.getVersion();
                PluginInformation newInformation = newPlugin.getPluginInformation();
                String newVersion = newInformation == null ? "?" : newInformation.getVersion();
                log.info("Updating plugin '{}' from version '{}' to version '{}'", new Object[]{oldPlugin, oldVersion, newVersion});
            }
            HashMap oldPluginState = new HashMap(this.getState().getPluginEnabledStateMap(oldPlugin));
            log.debug("Uninstalling old plugin: {}", (Object)oldPlugin);
            this.uninstallNoEvent(oldPlugin);
            log.debug("Plugin uninstalled '{}', preserving old state", (Object)oldPlugin);
            HashSet<String> newModuleKeys = new HashSet<String>();
            newModuleKeys.add(newPlugin.getKey());
            for (ModuleDescriptor moduleDescriptor : newPlugin.getModuleDescriptors()) {
                newModuleKeys.add(moduleDescriptor.getCompleteKey());
            }
            Map states = Maps.filterKeys(oldPluginState, newModuleKeys::contains);
            this.persistentStateModifier.addPluginEnabledState(states);
        });
    }

    public Collection<Plugin> getPlugins() {
        return this.pluginRegistry.getAll();
    }

    public Collection<Plugin> getPlugins(Predicate<Plugin> pluginPredicate) {
        return this.getPlugins().stream().filter(pluginPredicate).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public Collection<Plugin> getEnabledPlugins() {
        return this.getPlugins(new EnabledPluginPredicate(this.pluginEnabler.getPluginsBeingEnabled()));
    }

    public <M> Collection<M> getModules(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate) {
        return this.getModuleDescriptors(this.getPlugins(), moduleDescriptorPredicate).map(this.safeModuleExtractor::getModule).filter(Objects::nonNull).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate) {
        return this.getModuleDescriptors(this.getPlugins(), moduleDescriptorPredicate).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private <M> Stream<ModuleDescriptor<M>> getModuleDescriptors(Collection<Plugin> plugins, Predicate<ModuleDescriptor<M>> predicate) {
        return plugins.stream().flatMap(plugin -> plugin.getModuleDescriptors().stream()).map(descriptor -> descriptor).filter(predicate);
    }

    public Plugin getPlugin(String key) {
        return this.pluginRegistry.get((String)Assertions.notNull((String)"The plugin key must be specified", (Object)key));
    }

    public Plugin getEnabledPlugin(String pluginKey) {
        if (!this.isPluginEnabled(pluginKey)) {
            return null;
        }
        return this.getPlugin(pluginKey);
    }

    public ModuleDescriptor<?> getPluginModule(String completeKey) {
        return this.getPluginModule(new ModuleCompleteKey(completeKey));
    }

    private ModuleDescriptor<?> getPluginModule(ModuleCompleteKey key) {
        Plugin plugin = this.getPlugin(key.getPluginKey());
        if (plugin == null) {
            return null;
        }
        return plugin.getModuleDescriptor(key.getModuleKey());
    }

    public ModuleDescriptor<?> getEnabledPluginModule(String completeKey) {
        ModuleCompleteKey key = new ModuleCompleteKey(completeKey);
        if (!this.isPluginModuleEnabled(key)) {
            return null;
        }
        return this.getEnabledPlugin(key.getPluginKey()).getModuleDescriptor(key.getModuleKey());
    }

    public <M> List<M> getEnabledModulesByClass(Class<M> moduleClass) {
        return this.getEnabledModuleDescriptorsByModuleClass(moduleClass).map(this.safeModuleExtractor::getModule).filter(Objects::nonNull).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private <M> Stream<ModuleDescriptor<M>> getEnabledModuleDescriptorsByModuleClass(Class<M> moduleClass) {
        ModuleOfClassPredicate<M> ofType = new ModuleOfClassPredicate<M>(moduleClass);
        EnabledModulePredicate enabled = new EnabledModulePredicate();
        return this.getModuleDescriptors(this.getEnabledPlugins(), ofType.and(enabled));
    }

    public <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz) {
        return this.getEnabledPlugins().stream().flatMap(plugin -> plugin.getModuleDescriptors().stream()).filter(descriptorClazz::isInstance).filter(new EnabledModulePredicate()).map(descriptorClazz::cast).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public void enablePlugins(String ... keys) {
        this.pluginTransactionContext.wrap(() -> {
            ArrayList<Plugin> pluginsToEnable = new ArrayList<Plugin>(keys.length);
            for (String key : keys) {
                if (key == null) {
                    throw new IllegalArgumentException("Keys passed to enablePlugins must be non-null");
                }
                Plugin plugin = this.pluginRegistry.get(key);
                if (plugin == null) {
                    Plugin delayedPlugin = this.findDelayedPlugin(key);
                    if (delayedPlugin == null) {
                        log.info("No plugin was found for key '{}'. Not enabling.", (Object)key);
                        continue;
                    }
                    this.persistentStateModifier.enable(delayedPlugin);
                    continue;
                }
                if (!plugin.getPluginInformation().satisfiesMinJavaVersion()) {
                    log.error("Minimum Java version of '{}' was not satisfied for module '{}'. Not enabling.", (Object)plugin.getPluginInformation().getMinJavaVersion(), (Object)key);
                    continue;
                }
                if (plugin.getPluginState() == PluginState.ENABLED) continue;
                pluginsToEnable.add(plugin);
            }
            for (Plugin plugin : pluginsToEnable) {
                this.broadcastIgnoreError(new PluginEnablingEvent(plugin));
            }
            Collection<Plugin> enabledPlugins = this.pluginEnabler.enableAllRecursively(pluginsToEnable);
            for (Plugin plugin : enabledPlugins) {
                this.persistentStateModifier.enable(plugin);
                if (!this.enableConfiguredPluginModules(plugin)) continue;
                this.broadcastPluginEnabled(plugin);
            }
        });
    }

    private boolean enableConfiguredPluginModules(Plugin plugin) {
        return this.pluginTransactionContext.wrap(() -> {
            HashSet enabledDescriptors = new HashSet();
            for (ModuleDescriptor descriptor : plugin.getModuleDescriptors()) {
                if (this.enableConfiguredPluginModule(plugin, descriptor, enabledDescriptors)) continue;
                return false;
            }
            return true;
        });
    }

    private boolean enableConfiguredPluginModule(Plugin plugin, ModuleDescriptor<?> descriptor, Set<ModuleDescriptor<?>> enabledDescriptors) {
        return this.pluginTransactionContext.wrap(() -> {
            try {
                if (this.pluginEnabler.isPluginBeingEnabled(plugin)) {
                    log.debug("The plugin is currently being enabled, so we won't bother trying to enable the '{}' module", (Object)descriptor.getKey());
                    return true;
                }
                if (!this.isPluginEnabled(descriptor.getPluginKey()) || !this.getState().isEnabled(descriptor)) {
                    log.debug("Plugin module '{}' is explicitly disabled (or so by default), so not re-enabling.", (Object)descriptor.getDisplayName());
                    return true;
                }
                this.notifyModuleEnabled(descriptor);
                enabledDescriptors.add(descriptor);
                return true;
            }
            catch (Throwable enableException) {
                log.error("There was an error loading the descriptor '{}' of plugin '{}'. Disabling.", new Object[]{descriptor.getDisplayName(), plugin, enableException});
                for (ModuleDescriptor descriptorToDisable : enabledDescriptors) {
                    try {
                        this.notifyModuleDisabled(descriptorToDisable);
                    }
                    catch (Exception disableException) {
                        log.error("Could not notify previously enabled descriptor {} of module disabled in plugin {}", new Object[]{descriptorToDisable.getDisplayName(), plugin, disableException});
                    }
                }
                this.replacePluginWithUnloadablePlugin(plugin, descriptor, enableException);
                return false;
            }
        });
    }

    public void disablePlugin(String key) {
        this.pluginTransactionContext.wrap(() -> {
            if (this.isPluginEnabled(key)) {
                this.disablePluginInternal(key, true);
            } else {
                log.debug("Plugin {} already disabled", (Object)key);
            }
        });
    }

    public void disablePluginWithoutPersisting(String key) {
        this.pluginTransactionContext.wrap(() -> this.disablePluginInternal(key, false));
    }

    protected void disablePluginInternal(String key, boolean persistDisabledState) {
        if (key == null) {
            throw new IllegalArgumentException("You must specify a plugin key to disable.");
        }
        this.pluginTransactionContext.wrap(() -> {
            Plugin plugin = this.pluginRegistry.get(key);
            if (plugin == null) {
                Plugin delayedPlugin = this.findDelayedPlugin(key);
                if (delayedPlugin == null) {
                    log.info("No plugin was found for key '{}'. Not disabling.", (Object)key);
                } else if (persistDisabledState) {
                    this.persistentStateModifier.disable(delayedPlugin);
                }
                return;
            }
            if (plugin.getPluginState() != PluginState.DISABLED) {
                DependentPlugins disabledPlugins = this.disablePluginsAndTheirDependencies(Collections.singletonList(plugin.getKey()), Collections.unmodifiableSet(new HashSet<PluginDependencies.Type>(Arrays.asList(PluginDependencies.Type.MANDATORY, PluginDependencies.Type.OPTIONAL))));
                if (persistDisabledState) {
                    disabledPlugins.getPluginsByTypes(Collections.singleton(PluginDependencies.Type.MANDATORY), true).forEach(this.persistentStateModifier::disable);
                }
                this.reenableDependent(Collections.singletonList(plugin), disabledPlugins, PluginState.DISABLED);
            }
        });
    }

    private void reenableDependent(Collection<Plugin> plugins, DependentPlugins disabledPlugins, PluginState state) {
        this.pluginTransactionContext.wrap(() -> {
            List<Plugin> disabled;
            EnumSet<PluginDependencies.Type> cycledTypes = EnumSet.of(PluginDependencies.Type.OPTIONAL);
            if (state == PluginState.UNINSTALLED) {
                cycledTypes.add(PluginDependencies.Type.DYNAMIC);
            } else if (state != PluginState.DISABLED) {
                throw new IllegalArgumentException("State must be one of (UNINSTALLED,DISABLED)");
            }
            List<Plugin> cycled = disabledPlugins.getPluginsByTypes(cycledTypes, false);
            if (!cycled.isEmpty()) {
                log.info("Found optional/dynamic dependent plugins to re-enable after plugins {} '{}': {}. Enabling...", new Object[]{state, plugins, disabledPlugins.toStringList(cycledTypes)});
                this.enableDependentPlugins(cycled);
            }
            if (!(disabled = disabledPlugins.getPluginsByTypes(Collections.singleton(PluginDependencies.Type.MANDATORY), false)).isEmpty() || !cycled.isEmpty()) {
                plugins.forEach(p -> this.broadcastIgnoreError(new PluginDependentsChangedEvent(p, state, disabled, cycled)));
            }
        });
    }

    private Plugin findDelayedPlugin(String key) {
        return this.delayedPlugins.stream().filter(plugin -> plugin.getKey().equals(key)).findFirst().orElse(null);
    }

    private void disablePluginWithModuleEvents(Plugin plugin) {
        if (plugin.getPluginState() == PluginState.DISABLED) {
            return;
        }
        this.disablePluginModules(plugin);
        plugin.disable();
    }

    private void broadcastPluginDisabling(Plugin plugin) {
        this.pluginTransactionContext.wrap(() -> {
            log.info("Disabling {}", (Object)plugin);
            this.broadcastIgnoreError(new PluginDisablingEvent(plugin));
        });
    }

    private void broadcastPluginDisabled(Plugin plugin) {
        this.pluginTransactionContext.wrap(() -> {
            this.broadcastIgnoreError(new PluginDisabledEvent(plugin));
            PluginStateChangeCountEmitter.emitPluginDisabledCounter();
        });
    }

    private void broadcastPluginEnabled(Plugin plugin) {
        this.pluginTransactionContext.wrap(() -> {
            this.broadcastIgnoreError(new PluginEnabledEvent(plugin));
            PluginStateChangeCountEmitter.emitPluginEnabledCounter();
        });
    }

    private void notifyPluginDisabled(Plugin plugin) {
        this.pluginTransactionContext.wrap(() -> {
            this.broadcastPluginDisabling(plugin);
            this.disablePluginWithModuleEvents(plugin);
            this.broadcastPluginDisabled(plugin);
        });
    }

    private void disablePluginModules(Plugin plugin) {
        this.pluginTransactionContext.wrap(() -> {
            ArrayList moduleDescriptors = new ArrayList(plugin.getModuleDescriptors());
            Collections.reverse(moduleDescriptors);
            for (ModuleDescriptor module : moduleDescriptors) {
                this.disablePluginModuleNoPersist(module);
            }
        });
    }

    private void disablePluginModuleNoPersist(ModuleDescriptor<?> module) {
        if (this.isPluginModuleEnabled(module.getCompleteKey())) {
            this.publishModuleDisabledEvents(module, false);
        }
    }

    public void disablePluginModule(String completeKey) {
        this.pluginTransactionContext.wrap(() -> {
            if (completeKey == null) {
                throw new IllegalArgumentException("You must specify a plugin module key to disable.");
            }
            ModuleDescriptor<?> module = this.getPluginModule(completeKey);
            if (module == null) {
                log.info("Returned module for key '{}' was null. Not disabling.", (Object)completeKey);
                return;
            }
            if (module.getClass().isAnnotationPresent(CannotDisable.class)) {
                log.info("Plugin module '{}' cannot be disabled; it is annotated with {}", (Object)completeKey, (Object)CannotDisable.class.getName());
                return;
            }
            this.persistentStateModifier.disable(module);
            this.notifyModuleDisabled(module);
        });
    }

    protected void notifyModuleDisabled(ModuleDescriptor<?> module) {
        this.publishModuleDisabledEvents(module, true);
    }

    private void publishModuleDisabledEvents(ModuleDescriptor<?> module, boolean persistent) {
        this.pluginTransactionContext.wrap(() -> {
            log.debug("Disabling {}", (Object)module.getKey());
            this.broadcastIgnoreError(new PluginModuleDisablingEvent(module, persistent));
            if (module instanceof StateAware) {
                ((StateAware)module).disabled();
            }
            this.broadcastIgnoreError(new PluginModuleDisabledEvent(module, persistent));
        });
    }

    public void enablePluginModule(String completeKey) {
        this.pluginTransactionContext.wrap(() -> {
            if (completeKey == null) {
                throw new IllegalArgumentException("You must specify a plugin module key to disable.");
            }
            ModuleDescriptor<?> module = this.getPluginModule(completeKey);
            if (module == null) {
                log.info("Returned module for key '{}' was null. Not enabling.", (Object)completeKey);
                return;
            }
            if (!module.satisfiesMinJavaVersion()) {
                log.error("Minimum Java version of '{}' was not satisfied for module '{}'. Not enabling.", (Object)module.getMinJavaVersion(), (Object)completeKey);
                return;
            }
            this.persistentStateModifier.enable(module);
            this.notifyModuleEnabled(module);
        });
    }

    protected void notifyModuleEnabled(ModuleDescriptor<?> module) {
        this.pluginTransactionContext.wrap(() -> {
            log.debug("Enabling {}", (Object)module.getKey());
            this.broadcastIgnoreError(new PluginModuleEnablingEvent(module));
            if (module instanceof StateAware) {
                ((StateAware)module).enabled();
            }
            this.broadcastIgnoreError(new PluginModuleEnabledEvent(module));
        });
    }

    public boolean isPluginModuleEnabled(String completeKey) {
        return completeKey != null && this.isPluginModuleEnabled(new ModuleCompleteKey(completeKey));
    }

    private boolean isPluginModuleEnabled(ModuleCompleteKey key) {
        if (!this.isPluginEnabled(key.getPluginKey())) {
            return false;
        }
        ModuleDescriptor<?> pluginModule = this.getPluginModule(key);
        return pluginModule != null && pluginModule.isEnabled();
    }

    public boolean isPluginEnabled(String key) {
        Plugin plugin = this.pluginRegistry.get((String)Assertions.notNull((String)"The plugin key must be specified", (Object)key));
        return plugin != null && plugin.getPluginState() == PluginState.ENABLED;
    }

    public InputStream getDynamicResourceAsStream(String name) {
        return this.getClassLoader().getResourceAsStream(name);
    }

    public Class<?> getDynamicPluginClass(String className) throws ClassNotFoundException {
        return this.getClassLoader().loadClass(className);
    }

    public PluginsClassLoader getClassLoader() {
        return this.classLoader;
    }

    private void replacePluginWithUnloadablePlugin(Plugin plugin, ModuleDescriptor<?> descriptor, Throwable throwable) {
        UnloadableModuleDescriptor unloadableDescriptor = UnloadableModuleDescriptorFactory.createUnloadableModuleDescriptor(plugin, descriptor, throwable);
        UnloadablePlugin unloadablePlugin = UnloadablePluginFactory.createUnloadablePlugin(plugin, unloadableDescriptor);
        unloadablePlugin.setErrorText(unloadableDescriptor.getErrorText());
        this.pluginRegistry.put((Plugin)unloadablePlugin);
    }

    public boolean isSystemPlugin(String key) {
        Plugin plugin = this.getPlugin(key);
        return plugin != null && plugin.isSystemPlugin();
    }

    public PluginRestartState getPluginRestartState(String key) {
        return this.getState().getPluginRestartState(key);
    }

    private void broadcastIgnoreError(Object event) {
        try {
            this.pluginEventManager.broadcast(event);
            this.pluginTransactionContext.addEvent(event);
        }
        catch (NotificationException ex) {
            log.warn("Error broadcasting '{}'. Continuing anyway.", event, (Object)ex);
            for (Throwable throwable : ex.getAllCauses()) {
                log.debug("Cause:", throwable);
            }
        }
    }

    public ModuleDescriptor<?> addDynamicModule(Plugin maybePluginInternal, Element module) {
        AtomicReference moduleDescriptorRef = new AtomicReference();
        this.pluginTransactionContext.wrap(() -> {
            PluginInternal plugin = this.checkPluginInternal(maybePluginInternal);
            PluginLoader pluginLoader = this.installedPluginsToPluginLoader.get(plugin);
            if (pluginLoader == null) {
                throw new PluginException("cannot locate PluginLoader that created plugin '" + plugin + "'");
            }
            ModuleDescriptor<?> moduleDescriptor = pluginLoader.createModule(plugin, module, this.moduleDescriptorFactory);
            moduleDescriptorRef.set(moduleDescriptor);
            if (moduleDescriptor == null) {
                throw new PluginException("cannot add dynamic module of type '" + module.getName() + "' to plugin '" + plugin + "' as the PluginLoader does not know how to create the module");
            }
            if (plugin.getModuleDescriptor(moduleDescriptor.getKey()) != null) {
                throw new PluginException("duplicate module key '" + moduleDescriptor.getKey() + "' for plugin '" + plugin + "'");
            }
            if (!plugin.addDynamicModuleDescriptor(moduleDescriptor)) {
                throw new PluginException("cannot add dynamic module '" + moduleDescriptor.getKey() + "' to plugin '" + plugin + "' as it is already present");
            }
            if (plugin.getPluginState() == PluginState.ENABLED && this.getState().isEnabled(moduleDescriptor)) {
                this.notifyModuleEnabled(moduleDescriptor);
            }
        });
        return (ModuleDescriptor)moduleDescriptorRef.get();
    }

    public Iterable<ModuleDescriptor<?>> getDynamicModules(Plugin maybePluginInternal) {
        PluginInternal plugin = this.checkPluginInternal(maybePluginInternal);
        return plugin.getDynamicModuleDescriptors();
    }

    public void removeDynamicModule(Plugin maybePluginInternal, ModuleDescriptor<?> module) {
        this.pluginTransactionContext.wrap(() -> {
            PluginInternal plugin = this.checkPluginInternal(maybePluginInternal);
            if (!plugin.removeDynamicModuleDescriptor(module)) {
                throw new PluginException("cannot remove dynamic module '" + module.getKey() + "' from plugin '" + plugin + "' as it wasn't added by addDynamicModule");
            }
            this.persistentStateModifier.disable(module);
            this.notifyModuleDisabled(module);
            module.destroy();
        });
    }

    @VisibleForTesting
    PluginInternal checkPluginInternal(Plugin maybePluginInternal) {
        if (!(maybePluginInternal instanceof PluginInternal)) {
            throw new IllegalArgumentException(maybePluginInternal + " does not implement com.atlassian.plugin.PluginInternal it is a " + maybePluginInternal.getClass().getCanonicalName());
        }
        return (PluginInternal)maybePluginInternal;
    }

    /*
     * Unable to fully structure code
     */
    private /* synthetic */ void lambda$addPlugins$22(Collection pluginsToInstall, PluginLoader loader) {
        pluginsToEnable = new ArrayList<Plugin>();
        dependentsChangedEvents = new HashSet<PluginDependentsChangedEvent>();
        minimumPluginVersions = this.parseFileNamedByPropertyAsMap(DefaultPluginManager.getMinimumPluginVersionsFileProperty());
        for (Plugin plugin : new TreeSet<E>(pluginsToInstall)) {
            pluginUpgraded = false;
            pluginKey = plugin.getKey();
            existingPlugin = this.pluginRegistry.get(pluginKey);
            if (!this.pluginVersionIsAcceptable(plugin, minimumPluginVersions)) {
                DefaultPluginManager.log.info("Unacceptable plugin {} found - version less than minimum '{}'", (Object)plugin, (Object)minimumPluginVersions.get(pluginKey));
                this.discardPlugin(loader, plugin);
                continue;
            }
            if (null == existingPlugin) {
                this.broadcastIgnoreError(new PluginInstallingEvent(plugin));
            } else if (plugin.compareTo((Object)existingPlugin) >= 0) {
                try {
                    disabledPlugins = this.disableOnlyPluginDependencies(Collections.singletonList(plugin.getKey()), Collections.unmodifiableSet(new HashSet<PluginDependencies.Type>(Arrays.asList(new PluginDependencies.Type[]{PluginDependencies.Type.MANDATORY, PluginDependencies.Type.OPTIONAL, PluginDependencies.Type.DYNAMIC}))));
                    disabledPluginsList = disabledPlugins.getPlugins(false);
                    pluginsToEnable.addAll(disabledPluginsList);
                    if (!disabledPluginsList.isEmpty()) {
                        DefaultPluginManager.log.info("Found mandatory, optional and dynamically dependent plugins to re-enable after plugin upgrade '{}': {}. Enabling...", (Object)plugin, DefaultPluginManager.toPluginKeys(disabledPluginsList));
                    }
                    this.broadcastIgnoreError(new PluginUpgradingEvent(existingPlugin));
                    this.updatePlugin(existingPlugin, plugin);
                    pluginsToEnable.remove(existingPlugin);
                    pluginUpgraded = true;
                    if (disabledPluginsList.isEmpty()) ** GOTO lbl37
                    dependentsChangedEvents.add(new PluginDependentsChangedEvent(plugin, PluginState.INSTALLED, Collections.emptyList(), disabledPluginsList));
                }
                catch (PluginException e) {
                    throw new PluginParseException("Duplicate plugin found (installed version is the same or older) and could not be unloaded: '" + pluginKey + "'", (Throwable)e);
                }
            } else {
                DefaultPluginManager.log.debug("Duplicate plugin found (installed version is newer): '{}'", (Object)pluginKey);
                this.discardPlugin(loader, plugin);
                continue;
            }
lbl37:
            // 3 sources

            plugin.install();
            v0 = isPluginEnabledInSafeMode = this.tracker.get() == StateTracker.State.STARTED || this.isPluginEnabledInSafeMode(plugin, pluginsToInstall) != false;
            if (this.getState().isEnabled(plugin) && isPluginEnabledInSafeMode) {
                DefaultPluginManager.log.debug("Plugin '{}' is to be enabled.", (Object)pluginKey);
                pluginsToEnable.add(plugin);
            } else if (!isPluginEnabledInSafeMode) {
                DefaultPluginManager.log.warn("Plugin '{}' is disabled due to startup options!", (Object)pluginKey);
            } else if (plugin.isSystemPlugin()) {
                DefaultPluginManager.log.warn("System Plugin '{}' is disabled.", (Object)pluginKey);
            } else {
                DefaultPluginManager.log.debug("Plugin '{}' is disabled.", (Object)pluginKey);
            }
            if (pluginUpgraded) {
                this.broadcastIgnoreError(new PluginUpgradedEvent(plugin));
            } else {
                this.broadcastIgnoreError(new PluginInstalledEvent(plugin));
            }
            this.pluginRegistry.put(plugin);
            if (loader == null) {
                this.installedPluginsToPluginLoader.put(plugin, this.candidatePluginsToPluginLoader.get(plugin));
                continue;
            }
            this.installedPluginsToPluginLoader.put(plugin, loader);
        }
        pluginsToEnable.addAll(this.additionalPluginsToEnable);
        this.enableDependentPlugins(pluginsToEnable);
        for (PluginDependentsChangedEvent event : dependentsChangedEvents) {
            this.broadcastIgnoreError(event);
        }
    }

    public static class Builder<T extends Builder<?>> {
        private PluginPersistentStateStore store;
        private List<PluginLoader> pluginLoaders = new ArrayList<PluginLoader>();
        private ModuleDescriptorFactory moduleDescriptorFactory;
        private PluginEventManager pluginEventManager;
        private PluginExceptionInterception pluginExceptionInterception = NoOpPluginExceptionInterception.NOOP_INTERCEPTION;
        private boolean verifyRequiredPlugins = false;
        private Predicate<Plugin> delayLoadOf = p -> false;
        private PluginRegistry.ReadWrite pluginRegistry = new PluginRegistryImpl();
        private Optional<PluginAccessor> pluginAccessor = Optional.empty();
        private SafeModeManager safeModeManager = SafeModeManager.START_ALL_PLUGINS;

        public T withSafeModeManager(SafeModeManager safeModeManager) {
            this.safeModeManager = safeModeManager;
            return (T)this;
        }

        public T withStore(PluginPersistentStateStore store) {
            this.store = store;
            return (T)this;
        }

        public T withPluginLoaders(List<PluginLoader> pluginLoaders) {
            this.pluginLoaders.addAll(pluginLoaders);
            return (T)this;
        }

        public T withPluginLoader(PluginLoader pluginLoader) {
            this.pluginLoaders.add(pluginLoader);
            return (T)this;
        }

        public T withModuleDescriptorFactory(ModuleDescriptorFactory moduleDescriptorFactory) {
            this.moduleDescriptorFactory = moduleDescriptorFactory;
            return (T)this;
        }

        public T withPluginEventManager(PluginEventManager pluginEventManager) {
            this.pluginEventManager = pluginEventManager;
            return (T)this;
        }

        public T withPluginExceptionInterception(PluginExceptionInterception pluginExceptionInterception) {
            this.pluginExceptionInterception = pluginExceptionInterception;
            return (T)this;
        }

        public T withVerifyRequiredPlugins(boolean verifyRequiredPlugins) {
            this.verifyRequiredPlugins = verifyRequiredPlugins;
            return (T)this;
        }

        public T withDelayLoadOf(Predicate<Plugin> delayLoadOf) {
            this.delayLoadOf = delayLoadOf;
            return (T)this;
        }

        public T withPluginRegistry(PluginRegistry.ReadWrite pluginRegistry) {
            this.pluginRegistry = pluginRegistry;
            return (T)this;
        }

        public T withPluginAccessor(PluginAccessor pluginAccessor) {
            this.pluginAccessor = Optional.of(pluginAccessor);
            return (T)this;
        }

        @Deprecated
        public T withScopeManager(ScopeManager ignored) {
            return (T)this;
        }

        public DefaultPluginManager build() {
            return new DefaultPluginManager(this);
        }
    }
}

