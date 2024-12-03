/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.utils.process.BaseProcessMonitor
 *  com.atlassian.utils.process.ExternalProcess
 *  com.atlassian.utils.process.ProcessMonitor
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.errorprone.annotations.concurrent.GuardedBy
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promises
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProxyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.events.CollaborativeEditingOffEvent;
import com.atlassian.confluence.plugins.synchrony.api.events.CollaborativeEditingOnEvent;
import com.atlassian.confluence.plugins.synchrony.api.events.SynchronyStatusStartupEvents;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyEnvironmentBuilder;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyExecutorServiceProvider;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyProcessBuilder;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.utils.process.BaseProcessMonitor;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ProcessMonitor;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchronyProcessManager")
@ExportAsService(value={SynchronyProcessManager.class})
public class DefaultSynchronyProcessManager
implements SynchronyProcessManager,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultSynchronyProcessManager.class);
    @VisibleForTesting
    static final BandanaContext SYNCHRONY_DISABLED_BANDANA_CONTEXT = new ConfluenceBandanaContext();
    private static final String SYNCHRONY_STANDALONE_JAR = "synchrony-standalone.jar";
    private static final long DEFAULT_SYNCHRONY_STOP_TIMEOUT_MILLIS = Long.getLong("confluence.synchrony.process.stop.timeout.millis", 10000L);
    private static final long DEFAULT_SYNCHRONY_START_TIMEOUT_MILLIS = Long.getLong("confluence.synchrony.process.start.timeout.millis", 30000L);
    private static final int DEFAULT_STOP_DELAY_MILLIS = 3000;
    private final BootstrapManager bootstrapManager;
    private final SystemInformationService systemInformationService;
    private final SynchronyMonitor synchronyMonitor;
    private final SynchronyProxyMonitor synchronyProxyMonitor;
    private final SynchronyConfigurationManager synchronyConfigurationManager;
    private final ExecutorService executorService;
    private final ClusterManager clusterManager;
    private final EventPublisher eventPublisher;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicReference<SynchronyEnvironmentBuilder.SynchronyEnvironment> synchronyEnvironment = new AtomicReference();
    private final AtomicReference<ExternalProcess> synchronyProcess = new AtomicReference<Object>(null);
    private final AtomicReference<SynchronyProcessManager.ExternalProcessState> expectedProcessState = new AtomicReference<SynchronyProcessManager.ExternalProcessState>(SynchronyProcessManager.ExternalProcessState.BeforeStart);
    private final AtomicBoolean isSynchronyRestarting = new AtomicBoolean(false);
    private final BandanaManager bandanaManager;
    private final SynchronyEnvironmentBuilder synchronyEnvironmentBuilder;
    private final SynchronyProcessBuilder synchronyProcessBuilder;
    private final LongSupplier timeSupplier;
    private final long stopTimeoutMillis;
    private final long startTimeoutMillis;
    private final long stopDelayMillis;

    @Autowired
    public DefaultSynchronyProcessManager(@ComponentImport(value="bootstrapManager") BootstrapManager bootstrapManager, @ComponentImport SystemInformationService systemInformationService, @ComponentImport SynchronyConfigurationManager synchronyConfigurationManager, SynchronyMonitor synchronyMonitor, SynchronyExecutorServiceProvider executorServiceProvider, @ComponentImport(value="clusterManager") ClusterManager clusterManager, SynchronyProxyMonitor synchronyProxyMonitor, EventPublisher eventPublisher, @ComponentImport(value="bandanaManager") BandanaManager bandanaManager, SynchronyEnvironmentBuilder synchronyEnvironmentBuilder, SynchronyProcessBuilder synchronyProcessBuilder) {
        this(bootstrapManager, systemInformationService, synchronyConfigurationManager, synchronyMonitor, executorServiceProvider, clusterManager, synchronyProxyMonitor, eventPublisher, bandanaManager, synchronyEnvironmentBuilder, synchronyProcessBuilder, System::nanoTime, DEFAULT_SYNCHRONY_STOP_TIMEOUT_MILLIS, DEFAULT_SYNCHRONY_START_TIMEOUT_MILLIS, 3000L);
    }

    @VisibleForTesting
    DefaultSynchronyProcessManager(BootstrapManager bootstrapManager, SystemInformationService systemInformationService, SynchronyConfigurationManager synchronyConfigurationManager, SynchronyMonitor synchronyMonitor, SynchronyExecutorServiceProvider executorServiceProvider, ClusterManager clusterManager, SynchronyProxyMonitor synchronyProxyMonitor, EventPublisher eventPublisher, BandanaManager bandanaManager, SynchronyEnvironmentBuilder synchronyEnvironmentBuilder, SynchronyProcessBuilder synchronyProcessBuilder, LongSupplier timeSupplier, long stopTimeoutMillis, long startTimeoutMillis, long stopDelayMillis) {
        this.bootstrapManager = bootstrapManager;
        this.systemInformationService = systemInformationService;
        this.synchronyConfigurationManager = synchronyConfigurationManager;
        this.synchronyMonitor = synchronyMonitor;
        this.synchronyProxyMonitor = synchronyProxyMonitor;
        this.executorService = executorServiceProvider.getExecutorService();
        this.clusterManager = clusterManager;
        this.eventPublisher = eventPublisher;
        this.bandanaManager = bandanaManager;
        this.synchronyEnvironmentBuilder = synchronyEnvironmentBuilder;
        this.synchronyProcessBuilder = synchronyProcessBuilder;
        this.timeSupplier = timeSupplier;
        this.stopTimeoutMillis = stopTimeoutMillis;
        this.startTimeoutMillis = startTimeoutMillis;
        this.stopDelayMillis = stopDelayMillis;
    }

    @Override
    public Map<String, String> getConfiguration() {
        SynchronyEnvironmentBuilder.SynchronyEnvironment environment = this.synchronyEnvironment.get();
        boolean isProxyEnabled = environment != null && environment.isProxyEnabled();
        return new ImmutableMap.Builder().put((Object)"port", (Object)this.getSynchronyProperty(SynchronyEnv.Port)).put((Object)"contextPath", (Object)this.getSynchronyProperty(SynchronyEnv.ContextPath)).put((Object)"memory", (Object)this.getSynchronyProperty(SynchronyEnv.Memory)).put((Object)"driver", (Object)this.systemInformationService.getDatabaseInfo().getDriverName()).put((Object)"serviceUrl", (Object)this.synchronyConfigurationManager.getExternalServiceUrl()).put((Object)"internalServiceUrl", (Object)this.synchronyConfigurationManager.getInternalServiceUrl()).put((Object)"internalPort", (Object)String.valueOf(this.synchronyConfigurationManager.getInternalPort())).put((Object)"isProxyEnabled", (Object)String.valueOf(isProxyEnabled)).put((Object)"isProxyRunning", (Object)String.valueOf(this.synchronyProxyMonitor.isSynchronyProxyUp())).build();
    }

    @Override
    public String getSynchronyProperty(SynchronyEnv env) {
        SynchronyEnvironmentBuilder.SynchronyEnvironment environment = this.synchronyEnvironment.get();
        return environment == null ? env.getDefaultValue() : environment.getSynchronyProperty(env);
    }

    @Override
    public boolean isSynchronyStartingUp() {
        switch (this.expectedProcessState.get()) {
            case BeforeStart: {
                return true;
            }
            case Terminated: 
            case Terminating: {
                return this.isSynchronyRestarting.get();
            }
        }
        return false;
    }

    @Override
    public boolean isSynchronyClusterManuallyManaged() {
        return this.clusterManager.isClustered() && StringUtils.isNotBlank((CharSequence)System.getProperty("synchrony.service.url"));
    }

    private void updateSynchronyConfiguration() {
        SynchronyEnvironmentBuilder.SynchronyEnvironment environment = this.synchronyEnvironment.get();
        Objects.requireNonNull(environment);
        String synchronyExternalBaseUrl = environment.getExternalBaseUrl();
        log.debug("Updating Synchrony configuration...");
        this.synchronyConfigurationManager.setExternalBaseUrl(synchronyExternalBaseUrl);
        int synchronyPort = NumberUtils.toInt((String)this.getSynchronyProperty(SynchronyEnv.Port), (int)0);
        this.synchronyConfigurationManager.setInternalPort(synchronyPort);
        this.synchronyConfigurationManager.setInternalBaseUrl(environment.getInternalBaseUrl());
        log.info("Synchrony External Base URL: {}", (Object)synchronyExternalBaseUrl);
        log.info("Synchrony External Service URL: {}", (Object)this.synchronyConfigurationManager.getExternalServiceUrl());
        log.info("Synchrony Internal Service URL: {}", (Object)this.synchronyConfigurationManager.getInternalServiceUrl());
        this.synchronyConfigurationManager.generateStorePassphraseIfMissing();
        if (environment.isProxyEnabled() && !Boolean.getBoolean("synchrony.proxy.healthcheck.disabled")) {
            this.synchronyProxyMonitor.startHealthcheck();
        }
    }

    private void registerWithSynchrony() {
        this.synchronyConfigurationManager.registerWithSynchrony();
        this.synchronyConfigurationManager.retrievePublicKey();
    }

    @GuardedBy(value="lock")
    private boolean startProcess() {
        ExternalProcess newSynchronyProcess;
        if (ConfluenceSystemProperties.isSynchronyDisabled()) {
            return this.logAndExtract("External Synchrony process startup disabled by a system property '{}'", "synchrony.btf.disabled");
        }
        if (this.isSynchronyClusterManuallyManaged()) {
            return this.logAndExtract("External Synchrony cluster ({}) is used. No need to start up another Synchrony process", System.getProperty("synchrony.service.url"));
        }
        if (this.isSynchronyOff()) {
            return this.logAndExtract("External Synchrony process startup disabled by parameter '{}'", "synchrony.btf.off");
        }
        this.logAndExtract("Starting Synchrony process", "");
        try {
            log.debug("Build synchrony environment");
            this.synchronyEnvironment.set(this.synchronyEnvironmentBuilder.build(this.isSynchronyClusterManuallyManaged()));
            log.debug("Build new synchrony process");
            newSynchronyProcess = this.synchronyProcessBuilder.build((ProcessMonitor)new SynchronyProcessMonitor(), this.synchronyEnvironment.get());
        }
        catch (Exception e) {
            log.warn("Failed to setup Synchrony, turn on debug for stack trace: {}", (Object)e.getMessage());
            log.debug("", (Throwable)e);
            return false;
        }
        this.synchronyProcess.set(newSynchronyProcess);
        log.debug("Starting up new Synchrony process");
        newSynchronyProcess.start();
        return true;
    }

    private boolean logAndExtract(String msg, String property) {
        log.debug(msg, (Object)property);
        if (this.clusterManager.isClustered()) {
            this.synchronyProcessBuilder.extractSynchronyBinaryTo(new File(this.bootstrapManager.getLocalHome(), SYNCHRONY_STANDALONE_JAR));
        }
        return false;
    }

    @Override
    public boolean isSynchronyOff() {
        return Boolean.parseBoolean(String.valueOf(this.bandanaManager.getValue(SYNCHRONY_DISABLED_BANDANA_CONTEXT, "synchrony.btf.off")));
    }

    @Override
    public void setSynchronyOff(boolean off) {
        this.bandanaManager.setValue(SYNCHRONY_DISABLED_BANDANA_CONTEXT, "synchrony.btf.off", (Object)off);
    }

    private boolean shouldManuallyManagedSynchronyBeEnabledByDefault() {
        return Boolean.getBoolean("synchrony.by.default.enable.collab.editing.if.manually.managed");
    }

    @Override
    public Promise<Boolean> startup() {
        return Promises.forFuture(this.executorService.submit(() -> {
            try {
                log.debug("Starting acquire lock to start synchrony process");
                if (!this.lock.tryLock(this.startTimeoutMillis, TimeUnit.MILLISECONDS)) {
                    log.warn("Failed to acquire a lock to start synchrony process");
                    return false;
                }
                try {
                    log.debug("Acquired lock to start synchrony process");
                    Boolean bl = (Boolean)this.startupSynchrony().get();
                    return bl;
                }
                finally {
                    this.lock.unlock();
                    log.debug("Lock released after synchrony process started");
                }
            }
            catch (InterruptedException e) {
                log.warn("Thread was interrupted during attempt to acquire a lock to start synchrony process");
                Thread.currentThread().interrupt();
                return false;
            }
        }), (Executor)this.executorService);
    }

    @GuardedBy(value="lock")
    private Promise<Boolean> startupSynchrony() {
        if (this.synchronyProcessIsAlive()) {
            log.warn("Synchrony is already running, no need to start up new process");
            return Promises.promise((Object)true);
        }
        log.debug("Reset process");
        this.synchronyProcess.set(null);
        this.expectedProcessState.set(SynchronyProcessManager.ExternalProcessState.BeforeStart);
        if (!this.bootstrapManager.isSetupComplete()) {
            return Promises.promise((Object)false);
        }
        log.info("Starting Synchrony and enabling Collaborative Editing");
        if (!this.synchronyConfigurationManager.isSharedDraftsExplicitlyDisabled() && this.isSynchronyClusterManuallyManaged() && this.shouldManuallyManagedSynchronyBeEnabledByDefault()) {
            this.enableDarkFeatures();
        }
        boolean processStarted = this.startProcess();
        return this.postStartup(processStarted);
    }

    @GuardedBy(value="lock")
    private Promise<Boolean> postStartup(boolean processStarted) {
        if (processStarted) {
            log.debug("Synchrony process started, updating configuration.");
            this.updateSynchronyConfiguration();
            log.debug("Checking for heartbeat.");
            return this.synchronyMonitor.pollHeartbeat().flatMap(heartbeatDetected -> {
                if (heartbeatDetected.booleanValue()) {
                    log.debug("Heartbeat detected, attempting to register with Synchrony service.");
                    this.expectedProcessState.set(SynchronyProcessManager.ExternalProcessState.Started);
                    this.registerWithSynchrony();
                    log.debug("Registration complete.");
                    log.debug("Collaborative Editing was enabled successfully.");
                }
                this.enableDarkFeatures();
                this.eventPublisher.publish(heartbeatDetected != false ? new SynchronyStatusStartupEvents.Up() : new SynchronyStatusStartupEvents.Down());
                return Promises.promise((Object)heartbeatDetected);
            });
        }
        this.eventPublisher.publish((Object)new SynchronyStatusStartupEvents.Failed());
        this.expectedProcessState.set(SynchronyProcessManager.ExternalProcessState.Terminated);
        return Promises.promise((Object)false);
    }

    private void enableDarkFeatures() {
        this.synchronyConfigurationManager.enableSharedDrafts();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.stop();
        this.eventPublisher.unregister((Object)this);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean stop() {
        try {
            log.debug("Starting acquire lock to stop synchrony process");
            if (!this.lock.tryLock(this.stopTimeoutMillis, TimeUnit.MILLISECONDS)) {
                log.warn("Failed to acquire a lock to stop Synchrony process");
                return false;
            }
            try {
                boolean isShutdown;
                log.debug("Acquired lock to stop synchrony process");
                this.stopProcess(this.synchronyProcess.get());
                boolean bl = isShutdown = !this.synchronyProcessIsAlive();
                if (isShutdown) {
                    this.expectedProcessState.set(SynchronyProcessManager.ExternalProcessState.Terminated);
                }
                boolean bl2 = isShutdown;
                return bl2;
            }
            finally {
                this.lock.unlock();
                log.debug("Lock released after synchrony process stopped");
            }
        }
        catch (InterruptedException e) {
            log.warn("Thread was interrupted during attempt to acquire a lock to stop synchrony process");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @GuardedBy(value="lock")
    private void stopProcess(ExternalProcess process) {
        if (process != null) {
            log.info("Stopping Synchrony...");
            this.expectedProcessState.set(SynchronyProcessManager.ExternalProcessState.Terminating);
            this.synchronyMonitor.cancelHeartbeat();
            process.cancel();
            long startTime = this.timeSupplier.getAsLong();
            long elapsedTimeNanos = 0L;
            while (process.isAlive() && (elapsedTimeNanos = this.timeSupplier.getAsLong() - startTime) < TimeUnit.NANOSECONDS.convert(this.stopTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    log.trace("waited for Synchrony process to shutdown for {}ms", (Object)TimeUnit.MILLISECONDS.convert(elapsedTimeNanos, TimeUnit.NANOSECONDS));
                    Thread.sleep(this.stopDelayMillis);
                }
                catch (InterruptedException interruptedException) {}
            }
            log.debug("Synchrony process finished shutdown in {}ms", (Object)TimeUnit.MILLISECONDS.convert(elapsedTimeNanos, TimeUnit.NANOSECONDS));
        }
    }

    @Override
    public Promise<Boolean> restart() {
        return Promises.forFuture(this.executorService.submit(() -> {
            log.warn("Begin synchrony restart");
            this.isSynchronyRestarting.set(true);
            return this.stop();
        }), (Executor)this.executorService).flatMap(stopResult -> stopResult != false ? this.startup() : Promises.promise((Object)false)).then(Promises.compose(successResult -> {
            this.isSynchronyRestarting.set(false);
            log.warn("Synchrony restart finished");
        }, failureResult -> {
            this.isSynchronyRestarting.set(false);
            log.warn("Synchrony restart failed");
        }));
    }

    @EventListener
    public void onCollabEditingModeChangedEvent(ClusterEventWrapper eventWrapper) {
        if (!this.isSynchronyClusterManuallyManaged()) {
            if (eventWrapper.getEvent() instanceof CollaborativeEditingOnEvent) {
                this.startup();
            } else if (eventWrapper.getEvent() instanceof CollaborativeEditingOffEvent) {
                this.stop();
            }
        }
    }

    private boolean synchronyProcessIsAlive() {
        ExternalProcess externalProcess = this.synchronyProcess.get();
        return externalProcess != null && externalProcess.isAlive();
    }

    private class SynchronyProcessMonitor
    extends BaseProcessMonitor {
        private final AtomicReference<Thread> shutdownHook = new AtomicReference();

        private SynchronyProcessMonitor() {
        }

        public void onBeforeStart(ExternalProcess process) {
            super.onBeforeStart(process);
            log.debug("Added shutdown hook for Synchrony");
            Thread hook = new Thread(() -> {
                log.debug("Shutting down Synchrony when JVM stop");
                DefaultSynchronyProcessManager.this.stop();
                log.debug("Synchrony is stopped");
            });
            if (this.shutdownHook.compareAndSet(null, hook)) {
                Runtime.getRuntime().addShutdownHook(this.shutdownHook.get());
            } else {
                log.error("more than one shutdown hook registered");
            }
        }

        public void onAfterFinished(ExternalProcess process) {
            Runtime.getRuntime().removeShutdownHook(this.shutdownHook.get());
            switch (DefaultSynchronyProcessManager.this.expectedProcessState.get()) {
                case BeforeStart: 
                case Started: {
                    if (DefaultSynchronyProcessManager.this.synchronyProcessIsAlive()) break;
                    log.debug("Synchrony process died unexpectedly. Restarting...");
                    DefaultSynchronyProcessManager.this.startup();
                    break;
                }
                case Terminated: 
                case Terminating: {
                    process.finish();
                    break;
                }
                default: {
                    log.error("no such process state : {}", (Object)DefaultSynchronyProcessManager.this.expectedProcessState.get());
                }
            }
        }
    }
}

