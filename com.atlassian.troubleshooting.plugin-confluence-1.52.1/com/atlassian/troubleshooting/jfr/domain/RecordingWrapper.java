/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.jfr.domain;

import com.atlassian.troubleshooting.jfr.domain.RecordingConfig;
import com.atlassian.troubleshooting.jfr.enums.JfrEvent;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.atlassian.troubleshooting.jfr.util.JfrRecordingUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordingWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(RecordingWrapper.class);
    private final Recording recording;

    public RecordingWrapper(Recording recording) {
        this.recording = Objects.requireNonNull(recording);
    }

    public Recording getRecording() {
        return this.recording;
    }

    public long getId() {
        return this.recording.getId();
    }

    public String getName() {
        return this.recording.getName();
    }

    public RecordingState getState() {
        return this.recording.getState();
    }

    public long getSize() {
        return this.recording.getSize();
    }

    public Duration getDuration() {
        return this.recording.getDuration();
    }

    public boolean isToDisk() {
        return this.recording.isToDisk();
    }

    public boolean getDumpOnExit() {
        return this.recording.getDumpOnExit();
    }

    public Instant getStartTime() {
        return this.recording.getStartTime();
    }

    public Instant getStopTime() {
        return this.recording.getStopTime();
    }

    public Path getDestination() {
        return this.recording.getDestination();
    }

    public Duration getMaxAge() {
        return this.recording.getMaxAge();
    }

    public long getMaxSize() {
        return this.recording.getMaxSize();
    }

    public Map<String, String> getSettings() {
        return this.recording.getSettings();
    }

    public static RecordingWrapper create(Configuration configuration, RecordingConfig overwriteConfig) {
        Recording recording = new Recording(Objects.requireNonNull(configuration));
        try {
            recording.setName(overwriteConfig.getName());
            recording.setDestination(Paths.get(overwriteConfig.getDestination(), new String[0]));
            Long maxAge = overwriteConfig.getMaxAge();
            recording.setMaxAge(maxAge != null ? Duration.ofMillis(maxAge) : null);
            recording.setMaxSize(overwriteConfig.getMaxSize());
            recording.setDumpOnExit(overwriteConfig.isDumpOnExit());
            recording.setToDisk(overwriteConfig.isToDisk());
            overwriteConfig.getThreadDumpInterval().ifPresent(threadDumpInterval -> {
                recording.enable(JfrEvent.THREAD_DUMP.getName()).withPeriod(Duration.ofMillis(threadDumpInterval));
                recording.enable(JfrEvent.THREAD_CPU_LOAD.getName()).withPeriod(Duration.ofMillis(threadDumpInterval));
            });
        }
        catch (IOException exc) {
            throw new JfrException("Error setting recording destination", exc);
        }
        return new RecordingWrapper(recording);
    }

    public void start() {
        this.recording.start();
        LOG.debug("JFR recording with name \"{}\" started.", (Object)this.recording.getName());
    }

    public void stop() {
        this.recording.stop();
        LOG.debug("JFR recording with name \"{}\" stopped.", (Object)this.recording.getName());
    }

    public void close() {
        this.recording.close();
        LOG.debug("JFR recording with name \"{}\" closed.", (Object)this.recording.getName());
    }

    public Path dump() {
        try {
            Path recordingPath = Objects.requireNonNull(this.recording.getDestination(), "The recording configured without destination").getParent();
            Path recordingDumpPath = recordingPath.resolve(JfrRecordingUtils.formatRecordingFileName(RecordingTemplate.DEFAULT.getRecordingName()));
            this.recording.dump(recordingDumpPath);
            LOG.debug("Snapshot of the ongoing recording \"{}\" has been created", (Object)this.recording.getName());
            return recordingDumpPath;
        }
        catch (IOException exc) {
            throw new JfrException("Error creating a snapshot of ongoing recording", exc);
        }
    }
}

