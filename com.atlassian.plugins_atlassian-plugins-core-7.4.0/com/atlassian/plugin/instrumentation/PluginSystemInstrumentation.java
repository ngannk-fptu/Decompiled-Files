/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.DefaultInstrumentRegistry
 *  com.atlassian.instrumentation.InstrumentRegistry
 *  com.atlassian.instrumentation.RegistryConfiguration
 *  com.atlassian.instrumentation.operations.OpTimer
 *  com.atlassian.instrumentation.operations.OpTimerFactory
 *  com.atlassian.instrumentation.operations.SimpleOpTimerFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.instrumentation;

import com.atlassian.instrumentation.DefaultInstrumentRegistry;
import com.atlassian.instrumentation.InstrumentRegistry;
import com.atlassian.instrumentation.RegistryConfiguration;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.operations.OpTimerFactory;
import com.atlassian.instrumentation.operations.SimpleOpTimerFactory;
import com.atlassian.plugin.instrumentation.SingleTimer;
import com.atlassian.plugin.instrumentation.Timer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginSystemInstrumentation {
    private static final Logger log = LoggerFactory.getLogger(PluginSystemInstrumentation.class);
    public static final String INSTRUMENT_REGISTRY_CLASS = "com.atlassian.instrumentation.InstrumentRegistry";
    public static final String REGISTRY_NAME = "plugin.system";
    public static final File REGISTRY_HOME_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    private static final String SINGLE_TIMER_NAME_FORMAT = "%s.%s";
    private static final DateTimeFormatter timerDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Optional<InstrumentRegistryProxy> instrumentRegistryProxy;

    @Nonnull
    public static PluginSystemInstrumentation instance() {
        return LazyHolder.INSTANCE;
    }

    @Nonnull
    public static String getEnabledProperty() {
        return PluginSystemInstrumentation.class.getName() + ".enabled";
    }

    @VisibleForTesting
    PluginSystemInstrumentation() {
        boolean instrumentationPresent;
        try {
            Class.forName(INSTRUMENT_REGISTRY_CLASS);
            instrumentationPresent = true;
        }
        catch (ClassNotFoundException e) {
            instrumentationPresent = false;
        }
        Boolean instrumentationEnabled = Boolean.getBoolean(PluginSystemInstrumentation.getEnabledProperty());
        if (!instrumentationPresent && instrumentationEnabled.booleanValue()) {
            log.warn("Instrumentation class ({}) not found. Instrumentation cannot be enabled", (Object)INSTRUMENT_REGISTRY_CLASS);
        }
        if (instrumentationPresent && instrumentationEnabled.booleanValue()) {
            log.info("Plugin System instrumentation ENABLED via system property '{}'", (Object)PluginSystemInstrumentation.getEnabledProperty());
            this.instrumentRegistryProxy = Optional.of(new InstrumentRegistryProxy());
        } else {
            this.instrumentRegistryProxy = Optional.empty();
        }
    }

    @Nonnull
    public Optional<InstrumentRegistry> getInstrumentRegistry() {
        return this.instrumentRegistryProxy.map(p -> Optional.of(p.getInstrumentRegistry())).orElse(Optional.empty());
    }

    @Nonnull
    public Timer pullTimer(@Nonnull String name) {
        return new Timer(this.instrumentRegistryProxy.map(p -> Optional.of(p.pullTimer((String)Preconditions.checkNotNull((Object)name)))).orElse(Optional.empty()));
    }

    @Nonnull
    public SingleTimer pullSingleTimer(@Nonnull String name) {
        return new SingleTimer(this.instrumentRegistryProxy.map(p -> Optional.of(p.pullTimer(this.formatSingleName((String)Preconditions.checkNotNull((Object)name))))).orElse(Optional.empty()), name);
    }

    @Nonnull
    private String formatSingleName(@Nonnull String name) {
        return String.format(SINGLE_TIMER_NAME_FORMAT, Preconditions.checkNotNull((Object)name), timerDateFormatter.format(LocalDateTime.now()));
    }

    private class InstrumentRegistryProxy {
        final InstrumentRegistry instrumentRegistry = new DefaultInstrumentRegistry((OpTimerFactory)new SimpleOpTimerFactory(), new RegistryConfiguration(){

            public String getRegistryName() {
                return PluginSystemInstrumentation.REGISTRY_NAME;
            }

            public boolean isCPUCostCollected() {
                return true;
            }

            public File getRegistryHomeDirectory() {
                return REGISTRY_HOME_DIRECTORY;
            }
        });

        private InstrumentRegistryProxy() {
        }

        @Nonnull
        InstrumentRegistry getInstrumentRegistry() {
            return this.instrumentRegistry;
        }

        @Nonnull
        OpTimer pullTimer(@Nonnull String name) {
            return this.instrumentRegistry.pullTimer((String)Preconditions.checkNotNull((Object)name));
        }
    }

    private static class LazyHolder {
        private static final PluginSystemInstrumentation INSTANCE = new PluginSystemInstrumentation();

        private LazyHolder() {
        }
    }
}

