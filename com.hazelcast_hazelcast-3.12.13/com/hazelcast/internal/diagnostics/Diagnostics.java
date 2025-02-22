/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogFile;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Diagnostics {
    public static final String PREFIX = "hazelcast.diagnostics";
    public static final HazelcastProperty METRICS_LEVEL = new HazelcastProperty("hazelcast.diagnostics.metric.level", ProbeLevel.MANDATORY.name()).setDeprecatedName("hazelcast.performance.metric.level");
    public static final HazelcastProperty METRICS_DISTRIBUTED_DATASTRUCTURES = new HazelcastProperty("hazelcast.diagnostics.metric.distributed.datastructures", false);
    public static final HazelcastProperty ENABLED = new HazelcastProperty("hazelcast.diagnostics.enabled", false).setDeprecatedName("hazelcast.performance.monitoring.enabled");
    public static final HazelcastProperty MAX_ROLLED_FILE_SIZE_MB = new HazelcastProperty("hazelcast.diagnostics.max.rolled.file.size.mb", 50).setDeprecatedName("hazelcast.performance.monitor.max.rolled.file.size.mb");
    public static final HazelcastProperty MAX_ROLLED_FILE_COUNT = new HazelcastProperty("hazelcast.diagnostics.max.rolled.file.count", 10).setDeprecatedName("hazelcast.performance.monitor.max.rolled.file.count");
    public static final HazelcastProperty INCLUDE_EPOCH_TIME = new HazelcastProperty("hazelcast.diagnostics.include.epoch", true);
    public static final HazelcastProperty DIRECTORY = new HazelcastProperty("hazelcast.diagnostics.directory", "" + System.getProperty("user.dir"));
    public static final HazelcastProperty FILENAME_PREFIX = new HazelcastProperty("hazelcast.diagnostics.filename.prefix");
    final AtomicReference<DiagnosticsPlugin[]> staticTasks = new AtomicReference<DiagnosticsPlugin[]>(new DiagnosticsPlugin[0]);
    final String baseFileName;
    final ILogger logger;
    final String hzName;
    final HazelcastProperties properties;
    final boolean includeEpochTime;
    final File directory;
    DiagnosticsLogFile diagnosticsLogFile;
    private final ConcurrentMap<Class<? extends DiagnosticsPlugin>, DiagnosticsPlugin> pluginsMap = new ConcurrentHashMap<Class<? extends DiagnosticsPlugin>, DiagnosticsPlugin>();
    private final boolean enabled;
    private ScheduledExecutorService scheduler;

    public Diagnostics(String baseFileName, ILogger logger, String hzName, HazelcastProperties properties) {
        String optionalPrefix = properties.getString(FILENAME_PREFIX);
        this.baseFileName = optionalPrefix == null ? baseFileName : optionalPrefix + "-" + baseFileName;
        this.logger = logger;
        this.hzName = hzName;
        this.properties = properties;
        this.includeEpochTime = properties.getBoolean(INCLUDE_EPOCH_TIME);
        this.directory = new File(properties.getString(DIRECTORY));
        this.enabled = properties.getBoolean(ENABLED);
    }

    public File currentFile() {
        return this.diagnosticsLogFile.file;
    }

    public <P extends DiagnosticsPlugin> P getPlugin(Class<P> pluginClass) {
        return (P)((DiagnosticsPlugin)this.pluginsMap.get(pluginClass));
    }

    public void register(DiagnosticsPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin can't be null");
        if (!this.enabled) {
            return;
        }
        long periodMillis = plugin.getPeriodMillis();
        if (periodMillis < -1L) {
            throw new IllegalArgumentException(plugin + " can't return a periodMillis smaller than -1");
        }
        this.logger.finest(plugin.getClass().toString() + " is " + (periodMillis == 0L ? "disabled" : "enabled"));
        if (periodMillis == 0L) {
            return;
        }
        this.pluginsMap.put(plugin.getClass(), plugin);
        plugin.onStart();
        if (periodMillis > 0L) {
            this.scheduler.scheduleAtFixedRate(new WritePluginTask(plugin), 0L, periodMillis, TimeUnit.MILLISECONDS);
        } else {
            this.addStaticPlugin(plugin);
        }
    }

    private void addStaticPlugin(DiagnosticsPlugin plugin) {
        DiagnosticsPlugin[] newPlugins;
        DiagnosticsPlugin[] oldPlugins;
        do {
            oldPlugins = this.staticTasks.get();
            newPlugins = new DiagnosticsPlugin[oldPlugins.length + 1];
            System.arraycopy(oldPlugins, 0, newPlugins, 0, oldPlugins.length);
            newPlugins[oldPlugins.length] = plugin;
        } while (!this.staticTasks.compareAndSet(oldPlugins, newPlugins));
    }

    public void start() {
        if (!this.enabled) {
            this.logger.info(String.format("Diagnostics disabled. To enable add -D%s=true to the JVM arguments.", ENABLED.getName()));
            return;
        }
        this.diagnosticsLogFile = new DiagnosticsLogFile(this);
        this.scheduler = new ScheduledThreadPoolExecutor(1, new DiagnosticSchedulerThreadFactory());
        this.logger.info("Diagnostics started");
    }

    public void shutdown() {
        if (!this.enabled) {
            return;
        }
        if (this.scheduler != null) {
            this.scheduler.shutdownNow();
        }
    }

    private class DiagnosticSchedulerThreadFactory
    implements ThreadFactory {
        private DiagnosticSchedulerThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable target) {
            return new Thread(target, ThreadUtil.createThreadName(Diagnostics.this.hzName, "DiagnosticsSchedulerThread"));
        }
    }

    private class WritePluginTask
    implements Runnable {
        private final DiagnosticsPlugin plugin;

        WritePluginTask(DiagnosticsPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            try {
                Diagnostics.this.diagnosticsLogFile.write(this.plugin);
            }
            catch (Throwable t) {
                Diagnostics.this.logger.severe(t);
            }
        }
    }
}

