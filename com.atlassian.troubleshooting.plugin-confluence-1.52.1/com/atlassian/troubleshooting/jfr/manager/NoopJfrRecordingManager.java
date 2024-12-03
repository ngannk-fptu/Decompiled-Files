/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.manager;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.jfr.config.JfrServiceProductSupport;
import com.atlassian.troubleshooting.jfr.domain.ConfigurationDetails;
import com.atlassian.troubleshooting.jfr.domain.JfrCapabilities;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.domain.RecordingDetails;
import com.atlassian.troubleshooting.jfr.event.JfrAvailabilityAnalyticsEvent;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class NoopJfrRecordingManager
implements JfrRecordingManager,
LifecycleAware {
    private final EventPublisher eventPublisher;
    private final Optional<JfrServiceProductSupport> jfrServiceProductSupport;
    private final ClusterService clusterService;

    @Autowired
    public NoopJfrRecordingManager(EventPublisher eventPublisher, Optional<JfrServiceProductSupport> jfrServiceProductSupport, ClusterService clusterService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.jfrServiceProductSupport = Objects.requireNonNull(jfrServiceProductSupport);
        this.clusterService = clusterService;
    }

    @Override
    public JfrCapabilities getCapabilities() {
        String nodeId = this.clusterService.getCurrentNodeId().orElse(null);
        return new JfrCapabilities(this.isJfrFeatureFlagEnabled(), nodeId);
    }

    @Override
    public ConfigurationDetails getActiveConfiguration() {
        return ConfigurationDetails.builder().build();
    }

    @Override
    public List<RecordingDetails> getRecordingDetails() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Path> dumpRecording(long recordingId) {
        return Optional.empty();
    }

    @Override
    public JfrSettings getSettings() {
        return new JfrSettings(Boolean.FALSE.toString());
    }

    @Override
    public JfrSettings storeSettings(JfrSettings jfrSettings) {
        return new JfrSettings(Boolean.FALSE.toString());
    }

    @Override
    public void handleFeatureFlagStateChanged(JfrSettings settings) {
    }

    public void onStart() {
        this.eventPublisher.publish((Object)new JfrAvailabilityAnalyticsEvent(this.getCapabilities().isAvailable()));
    }

    public void onStop() {
    }

    @Override
    public boolean isJfrFeatureFlagEnabled() {
        return this.jfrServiceProductSupport.map(JfrServiceProductSupport::isSupported).orElse(false);
    }
}

