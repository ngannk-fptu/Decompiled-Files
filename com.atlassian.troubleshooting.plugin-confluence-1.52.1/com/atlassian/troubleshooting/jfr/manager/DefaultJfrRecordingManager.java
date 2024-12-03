/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.manager;

import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.troubleshooting.api.ClusterMessagingService;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.healthcheck.LocalHomeFileSystemInfo;
import com.atlassian.troubleshooting.cluster.JsonSerialiser;
import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.config.JfrServiceProductSupport;
import com.atlassian.troubleshooting.jfr.domain.ConfigurationDetails;
import com.atlassian.troubleshooting.jfr.domain.JfrCapabilities;
import com.atlassian.troubleshooting.jfr.domain.JfrConfigurationPropertiesDto;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.domain.RecordingDetails;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.event.JfrAvailabilityAnalyticsEvent;
import com.atlassian.troubleshooting.jfr.event.JfrDumpCreatedEvent;
import com.atlassian.troubleshooting.jfr.event.JfrLocalStateChangedEvent;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.atlassian.troubleshooting.jfr.exception.JfrWriteException;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.jfr.service.JfrAlwaysOnRecordingService;
import com.atlassian.troubleshooting.jfr.service.JfrRecordingCleanUpService;
import com.atlassian.troubleshooting.jfr.service.JfrRecordingService;
import com.atlassian.troubleshooting.jfr.service.JfrSettingsService;
import com.atlassian.troubleshooting.stp.audit.Auditor;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jdk.jfr.Configuration;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJfrRecordingManager
implements JfrRecordingManager,
LifecycleAware {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJfrRecordingManager.class);
    private static final int TEN_MB = 0xA00000;
    private final EventPublisher eventPublisher;
    private final ClusterMessagingService clusterMessagingService;
    private final JsonSerialiser jsonSerialiser;
    private final JfrRecordingService jfrRecordingService;
    private final JfrSettingsService jfrSettingsService;
    private final JfrRecordingCleanUpService jfrRecordingCleanUpService;
    private final JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService;
    private final Optional<JfrServiceProductSupport> jfrServiceProductSupport;
    private final ClusterService clusterService;
    private final JfrProperties jfrProperties;
    private final SupportApplicationInfo applicationInfo;
    private final I18nResolver i18nResolver;
    private final Auditor auditor;
    private final LocalHomeFileSystemInfo localHomeFileSystemInfo;
    private final PluginSettingsFactory pluginSettingsFactory;
    private boolean isJfrManagerStopping;

    @Autowired
    public DefaultJfrRecordingManager(EventPublisher eventPublisher, JsonSerialiser jsonSerialiser, ClusterMessagingService clusterMessagingService, JfrRecordingService jfrRecordingService, JfrSettingsService jfrSettingsService, JfrRecordingCleanUpService jfrRecordingCleanUpService, JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService, Optional<JfrServiceProductSupport> jfrServiceProductSupport, ClusterService clusterService, JfrProperties jfrProperties, SupportApplicationInfo applicationInfo, Auditor auditor, I18nResolver i18nResolver, LocalHomeFileSystemInfo localHomeFileSystemInfo, PluginSettingsFactory pluginSettingsFactory) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.jsonSerialiser = Objects.requireNonNull(jsonSerialiser);
        this.clusterMessagingService = Objects.requireNonNull(clusterMessagingService);
        this.jfrRecordingService = Objects.requireNonNull(jfrRecordingService);
        this.jfrSettingsService = Objects.requireNonNull(jfrSettingsService);
        this.jfrRecordingCleanUpService = Objects.requireNonNull(jfrRecordingCleanUpService);
        this.jfrAlwaysOnRecordingService = Objects.requireNonNull(jfrAlwaysOnRecordingService);
        this.jfrServiceProductSupport = Objects.requireNonNull(jfrServiceProductSupport);
        this.clusterService = Objects.requireNonNull(clusterService);
        this.jfrProperties = Objects.requireNonNull(jfrProperties);
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
        this.auditor = Objects.requireNonNull(auditor);
        this.localHomeFileSystemInfo = Objects.requireNonNull(localHomeFileSystemInfo);
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public JfrCapabilities getCapabilities() {
        return new JfrCapabilities(this.isJfrFeatureFlagEnabled(), this.getNodeId());
    }

    @Override
    public ConfigurationDetails getActiveConfiguration() {
        if (this.isJfrRecordingEnabled()) {
            Configuration configuration = this.jfrRecordingService.getActiveConfiguration();
            return ConfigurationDetails.from(this.getNodeId(), configuration, new JfrConfigurationPropertiesDto(this.jfrProperties));
        }
        return ConfigurationDetails.builder().build();
    }

    @Override
    public List<RecordingDetails> getRecordingDetails() {
        if (this.isJfrRecordingEnabled()) {
            return this.jfrRecordingService.getRecordings().stream().map(recordingWrapper -> RecordingDetails.from(this.getNodeId(), recordingWrapper)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Path> dumpRecording(long recordingId) {
        if (this.isJfrRecordingEnabled()) {
            Optional<Path> maybeDumpPath = this.jfrRecordingService.dumpRecording(recordingId);
            if (maybeDumpPath.isPresent()) {
                this.eventPublisher.publish((Object)new JfrDumpCreatedEvent());
            }
            return maybeDumpPath;
        }
        return Optional.empty();
    }

    @Override
    public JfrSettings getSettings() {
        return this.jfrSettingsService.getSettings();
    }

    @Override
    public JfrSettings storeSettings(JfrSettings settings) {
        JfrSettings activeSettings = this.jfrSettingsService.getSettings();
        if (!this.isJfrFeatureFlagEnabled()) {
            return activeSettings;
        }
        if (!this.isJfrFolderHasWritePermissions()) {
            String reason = this.i18nResolver.getText("stp.jfr.error.folder.no.write.permissions");
            throw new JfrWriteException(reason);
        }
        if (!this.ensureFreeDiskSpaceAvailable()) {
            String reason = this.i18nResolver.getText("stp.jfr.error.no.free.disk.space");
            throw new JfrWriteException(reason);
        }
        if (activeSettings.equals(settings)) {
            throw new IllegalStateException("Inconsistent state of JFR settings");
        }
        if (settings.isEnabled()) {
            this.jfrRecordingService.getConfigurationTemplate();
        }
        this.updateJfrClusterState(settings);
        return this.jfrSettingsService.getSettings();
    }

    @Override
    public void handleFeatureFlagStateChanged(JfrSettings settings) {
        this.updateJfrClusterState(settings);
    }

    public void onStart() {
        this.eventPublisher.publish((Object)new JfrAvailabilityAnalyticsEvent(this.getCapabilities().isAvailable()));
        this.eventPublisher.register((Object)this);
        this.registerJfrRecordingStateChangedListener();
        this.createJfrDirectoryIfNotExists();
        if (this.isJfrRecordingEnabled() && !this.isJfrRecordingCanBeStarted()) {
            this.updateJfrClusterState(new JfrSettings(false));
            LOG.info("Cluster JFR recording disabled due to disabled feature flag");
        } else if (this.isJfrRecordingEnabled() && !this.ensureFreeDiskSpaceAvailable()) {
            this.updateJfrClusterState(new JfrSettings(false));
            LOG.info("Cluster JFR recording disabled due to lack of free disk space");
        } else {
            JfrSettings activeSettings = this.jfrSettingsService.getSettings();
            this.manageDefaultRecording(activeSettings);
            this.auditIfRecordingStarted(activeSettings);
        }
    }

    private boolean ensureFreeDiskSpaceAvailable() {
        try {
            boolean isEnoughFreeSpace;
            long requiredSpace = this.jfrProperties.getMaxSize() * (long)(this.jfrProperties.getNumberOfFilesToRemain() + 1) + 0xA00000L;
            long availableSpace = this.localHomeFileSystemInfo.getLocalHomeFileStore().getUsableSpace();
            boolean bl = isEnoughFreeSpace = requiredSpace < availableSpace;
            if (!isEnoughFreeSpace) {
                LOG.warn("Cannot start/stop JFR recording. Required free space is {} MB but available is {} MB", (Object)(requiredSpace / 1024L / 1024L), (Object)(availableSpace / 1024L / 1024L));
            }
            return isEnoughFreeSpace;
        }
        catch (Exception e) {
            LOG.error("Exception while checking if free space is available for JFR recording", (Throwable)e);
            return true;
        }
    }

    @EventListener
    public void onApplicationStoppingEvent(ApplicationStoppingEvent applicationStoppingEvent) {
        this.onStop();
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public boolean isJfrFeatureFlagEnabled() {
        return this.jfrServiceProductSupport.map(JfrServiceProductSupport::isSupported).orElse(false);
    }

    private void manageDefaultRecording(JfrSettings activeSettings) {
        if (activeSettings.isEnabled()) {
            this.jfrRecordingCleanUpService.cleanUpDumpOnExitStaleRecordings();
            this.jfrAlwaysOnRecordingService.startDefaultRecording(true);
        } else {
            this.jfrAlwaysOnRecordingService.stopDefaultRecording();
        }
    }

    private void auditIfRecordingStarted(JfrSettings activeSettings) {
        if (activeSettings.isEnabled()) {
            String configurationType = this.jfrProperties.isDefaultConfiguration() ? "Default" : "Custom";
            HashMap<String, String> extraAttributes = new HashMap<String, String>();
            extraAttributes.put("stp.jfr.audit.configuration", configurationType);
            extraAttributes.put("stp.jfr.audit.start.type", String.valueOf(true));
            this.auditor.audit("stp.jfr.audit.recording.started", extraAttributes);
        }
    }

    private boolean isJfrConfigurationValid() {
        try {
            this.jfrRecordingService.getConfigurationTemplate();
            return true;
        }
        catch (JfrException exc) {
            LOG.error("Failed to start default recording due to invalid custom configuration", (Throwable)exc);
            return false;
        }
    }

    private boolean isJfrRecordingCanBeStarted() {
        return this.isJfrFeatureFlagEnabled() && this.isJfrConfigurationValid() && this.isJfrFolderHasWritePermissions();
    }

    private boolean isJfrFolderHasWritePermissions() {
        Path recordingsFolderPath = Paths.get(this.applicationInfo.getLocalApplicationHome(), this.jfrProperties.getRecordingPath());
        return recordingsFolderPath.toFile().canWrite();
    }

    private void createJfrDirectoryIfNotExists() {
        Path pathToFolder = Paths.get(this.applicationInfo.getLocalApplicationHome(), this.jfrProperties.getRecordingPath());
        try {
            if (Files.notExists(pathToFolder, new LinkOption[0])) {
                Files.createDirectories(pathToFolder, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            String reason = this.i18nResolver.getText("stp.jfr.error.folder.cannot.create");
            throw new JfrWriteException(reason + pathToFolder, e);
        }
    }

    public void onStop() {
        this.isJfrManagerStopping = true;
        if (this.isJfrRecordingEnabled()) {
            this.jfrAlwaysOnRecordingService.stopDefaultRecording();
        }
    }

    @VisibleForTesting
    protected boolean isJfrRecordingEnabled() {
        return this.jfrSettingsService.getSettings().isEnabled();
    }

    private void updateJfrClusterState(JfrSettings settings) {
        JfrSettings activeSettings;
        if (this.jfrSettingsService.isPluginSystemReady()) {
            activeSettings = this.jfrSettingsService.storeSettings(settings);
        } else {
            this.pluginSettingsFactory.createGlobalSettings().put("com.atlassian.troubleshooting.jfr.settings.v2.enabled", (Object)Boolean.toString(settings.isEnabled()));
            LOG.debug("JFR settings stored successfully! Value set to : {}", (Object)settings.isEnabled());
            activeSettings = settings;
        }
        this.clusterMessagingService.sendMessage("jfr_settings", this.jsonSerialiser.toJson(activeSettings));
        this.eventPublisher.publish((Object)new JfrLocalStateChangedEvent(activeSettings.isEnabled()));
        LOG.info("Set cluster JFR settings set to: {}", (Object)settings.isEnabled());
    }

    private String getNodeId() {
        return this.clusterService.getCurrentNodeId().orElse(null);
    }

    private void registerJfrRecordingStateChangedListener() {
        FlightRecorder.addListener(new FlightRecorderListener(){

            @Override
            public void recordingStateChanged(Recording recording) {
                JfrSettings activeSettings = DefaultJfrRecordingManager.this.jfrSettingsService.getSettings();
                boolean isDefaultRecording = recording.getName().equals(RecordingTemplate.DEFAULT.getRecordingName());
                boolean isClosed = RecordingState.CLOSED == recording.getState();
                boolean isRecordingEnabled = activeSettings.isEnabled();
                boolean isRestarting = DefaultJfrRecordingManager.this.jfrAlwaysOnRecordingService.isRestarting();
                if (isDefaultRecording && isClosed && isRecordingEnabled && !DefaultJfrRecordingManager.this.isShuttingDown() && !isRestarting && !DefaultJfrRecordingManager.this.isJfrManagerStopping) {
                    DefaultJfrRecordingManager.this.updateJfrClusterState(new JfrSettings(false));
                }
            }
        });
    }

    private boolean isShuttingDown() {
        Thread dummyThread = new Thread();
        try {
            Runtime.getRuntime().addShutdownHook(dummyThread);
            Runtime.getRuntime().removeShutdownHook(dummyThread);
        }
        catch (IllegalStateException e) {
            return true;
        }
        return false;
    }
}

