/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.wiring.BundleWiring
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.plugin;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.diagnostics.internal.platform.plugin.PluginSystemMonitoringConfig;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class ClassNameToPluginKeyStore {
    private static final Logger log = LoggerFactory.getLogger(ClassNameToPluginKeyStore.class);
    @VisibleForTesting
    static final String CLASS_FILE_EXTENSION = ".class";
    private final BundleSupplier bundleSupplier;
    private static volatile Map<String, String> classNameToPluginKeyMap = Collections.emptyMap();
    private final PluginSystemMonitoringConfig pluginSystemMonitoringConfig;
    private static Timer delayMapGenerationTimer = ClassNameToPluginKeyStore.newTimer();

    public ClassNameToPluginKeyStore(EventPublisher eventPublisher, OsgiContainerManager osgiContainerManager, PluginSystemMonitoringConfig pluginSystemMonitoringConfig) {
        this(() -> ((OsgiContainerManager)osgiContainerManager).getBundles(), eventPublisher, pluginSystemMonitoringConfig);
    }

    public ClassNameToPluginKeyStore(BundleContext bundleContext, EventPublisher eventPublisher, PluginSystemMonitoringConfig pluginSystemMonitoringConfig) {
        this(() -> ((BundleContext)bundleContext).getBundles(), eventPublisher, pluginSystemMonitoringConfig);
    }

    private ClassNameToPluginKeyStore(BundleSupplier bundleSupplier, EventPublisher eventPublisher, PluginSystemMonitoringConfig pluginSystemMonitoringConfig) {
        this.bundleSupplier = bundleSupplier;
        this.pluginSystemMonitoringConfig = pluginSystemMonitoringConfig;
        eventPublisher.register((Object)this);
    }

    public Optional<String> getPluginKey(@Nullable String classname) {
        if (classname == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(classNameToPluginKeyMap.get(classname));
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        this.scheduleNewMapGeneration(event);
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        this.scheduleNewMapGeneration(event);
    }

    @EventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.scheduleNewMapGeneration(event);
    }

    private void scheduleNewMapGeneration(Object triggeringEvent) {
        try {
            classNameToPluginKeyMap = Collections.emptyMap();
            delayMapGenerationTimer.cancel();
            delayMapGenerationTimer = ClassNameToPluginKeyStore.newTimer();
            delayMapGenerationTimer.schedule(this.newMapRefreshTask(triggeringEvent), TimeUnit.SECONDS.toMillis(30L));
        }
        catch (Exception exception) {
            log.debug("Failed to schedule task to generate a map of class names to plugin keys", (Throwable)exception);
        }
    }

    @VisibleForTesting
    TimerTask newMapRefreshTask(final Object triggeringEvent) {
        return new TimerTask(){

            @Override
            public void run() {
                log.debug("Refreshing classname to plugin key map after: {}", triggeringEvent);
                classNameToPluginKeyMap = ClassNameToPluginKeyStore.this.generateClassNameToPluginKeyMap();
            }
        };
    }

    @VisibleForTesting
    private Map<String, String> generateClassNameToPluginKeyMap() {
        if (Objects.isNull(this.bundleSupplier) || this.pluginSystemMonitoringConfig.classNameToPluginKeyStoreDisabled()) {
            return Collections.emptyMap();
        }
        return Arrays.stream((Object[])this.bundleSupplier.get()).filter(Objects::nonNull).filter(ClassNameToPluginKeyStore::pluginIsEnabled).map(ClassNameToPluginKeyStore::getBundleWiring).filter(Objects::nonNull).flatMap(ClassNameToPluginKeyStore::classNameToPluginKeyFanOut).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> null)).entrySet().stream().filter(entry -> Objects.nonNull(entry.getValue())).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean pluginIsEnabled(Bundle bundle) {
        return bundle.getState() == 32;
    }

    private static BundleWiring getBundleWiring(Bundle bundle) {
        return (BundleWiring)bundle.adapt(BundleWiring.class);
    }

    private static Stream<Map.Entry<String, String>> classNameToPluginKeyFanOut(BundleWiring bundleWiring) {
        String pluginKey = OsgiHeaderUtil.getPluginKey((Bundle)bundleWiring.getBundle());
        return ClassNameToPluginKeyStore.listAllClasses(bundleWiring).stream().map(ClassNameToPluginKeyStore::resourceFilePathToCanonicalClassName).map(className -> new AbstractMap.SimpleEntry<String, String>((String)className, pluginKey));
    }

    private static Collection<String> listAllClasses(BundleWiring bundleWiring) {
        return bundleWiring.listResources("/", "*.class", 1);
    }

    @VisibleForTesting
    static String resourceFilePathToCanonicalClassName(String resourceName) {
        return resourceName.substring(0, resourceName.length() - CLASS_FILE_EXTENSION.length()).replaceAll("/", ".");
    }

    private static Timer newTimer() {
        return new Timer("generate-classname-to-pluginKey-map", true);
    }

    private static interface BundleSupplier
    extends Supplier<Bundle[]> {
    }
}

