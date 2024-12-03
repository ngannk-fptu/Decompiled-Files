/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.config.JfrConfigurationRegistry;
import com.atlassian.troubleshooting.jfr.domain.RecordingConfig;
import com.atlassian.troubleshooting.jfr.domain.RecordingWrapper;
import com.atlassian.troubleshooting.jfr.service.JfrRecordingService;
import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jdk.jfr.Configuration;
import jdk.jfr.FlightRecorder;
import jdk.jfr.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJfrRecordingService
implements JfrRecordingService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJfrRecordingService.class);
    private final JfrConfigurationRegistry jfrConfigurationRegistry;

    @Autowired
    public DefaultJfrRecordingService(JfrConfigurationRegistry jfrConfigurationRegistry) {
        this.jfrConfigurationRegistry = Objects.requireNonNull(jfrConfigurationRegistry);
    }

    @Override
    public Optional<RecordingWrapper> createRecording(RecordingConfig recordingConfig) {
        Objects.requireNonNull(recordingConfig);
        Optional<RecordingWrapper> maybeRecording = this.getRecordings().stream().filter(recordingWrapper -> recordingWrapper.getName().equals(recordingConfig.getName())).findAny();
        if (maybeRecording.isPresent()) {
            LOG.warn("Recording with name \"{}\" already exists. No recording has been created.", (Object)recordingConfig.getName());
            return maybeRecording;
        }
        Configuration configuration = this.getConfigurationTemplate();
        this.jfrConfigurationRegistry.storeActiveConfiguration(configuration);
        RecordingWrapper recordingWrapper2 = RecordingWrapper.create(configuration, recordingConfig);
        return Optional.of(recordingWrapper2);
    }

    @Override
    public Optional<Path> dumpRecording(long recordingId) {
        return this.getRecordingById(recordingId).map(RecordingWrapper::dump);
    }

    @Override
    public List<RecordingWrapper> getRecordings() {
        return this.getRecordingsInternal();
    }

    @Override
    public Configuration getConfigurationTemplate() {
        return this.jfrConfigurationRegistry.getConfigurationTemplate();
    }

    @Override
    public Configuration getActiveConfiguration() {
        return this.jfrConfigurationRegistry.getActiveConfiguration();
    }

    private Optional<RecordingWrapper> getRecordingById(long recordingId) {
        return this.getRecordings().stream().filter(recordingWrapper -> recordingWrapper.getId() == recordingId).findFirst();
    }

    @VisibleForTesting
    List<RecordingWrapper> getRecordingsInternal() {
        List<Recording> recordings = FlightRecorder.getFlightRecorder().getRecordings();
        for (Recording recording : recordings) {
            if (recording != null) continue;
            LOG.debug("Recording is null");
            break;
        }
        return recordings.stream().filter(Objects::nonNull).map(RecordingWrapper::new).sorted(Comparator.comparing(RecordingWrapper::getName)).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }
}

