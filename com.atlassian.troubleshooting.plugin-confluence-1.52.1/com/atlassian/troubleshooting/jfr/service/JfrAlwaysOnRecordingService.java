/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.domain.RecordingConfig;
import com.atlassian.troubleshooting.jfr.domain.RecordingWrapper;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.service.JfrRecordingService;
import com.atlassian.troubleshooting.jfr.util.JfrRecordingUtils;
import com.atlassian.troubleshooting.stp.audit.Auditor;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrAlwaysOnRecordingService {
    private static final Logger LOG = LoggerFactory.getLogger(JfrAlwaysOnRecordingService.class);
    private final JfrRecordingService jfrRecordingService;
    private final SupportApplicationInfo applicationInfo;
    private final JfrProperties jfrProperties;
    private final Auditor auditor;
    private boolean isRestarting;

    @Autowired
    public JfrAlwaysOnRecordingService(JfrRecordingService jfrRecordingService, SupportApplicationInfo applicationInfo, JfrProperties jfrProperties, Auditor auditor) {
        this.jfrRecordingService = Objects.requireNonNull(jfrRecordingService);
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.jfrProperties = Objects.requireNonNull(jfrProperties);
        this.auditor = Objects.requireNonNull(auditor);
    }

    public synchronized void startDefaultRecording(boolean isOnStart) {
        this.startDefaultRecordingIfNotRunning(isOnStart);
        LOG.info("JFR recording started");
    }

    public synchronized void stopDefaultRecording() {
        this.jfrRecordingService.getRecordings().stream().filter(recordingWrapper -> RecordingTemplate.DEFAULT.getRecordingName().equals(recordingWrapper.getName())).findAny().ifPresent(recordingWrapper -> {
            recordingWrapper.stop();
            recordingWrapper.close();
        });
        this.auditor.audit("stp.jfr.audit.recording.stopped");
        LOG.info("JFR recording has been stopped");
    }

    private void startDefaultRecordingIfNotRunning(boolean isOnStart) {
        boolean isDefaultRecordingRunning = this.jfrRecordingService.getRecordings().stream().anyMatch(recordingWrapper -> RecordingTemplate.DEFAULT.getRecordingName().equals(recordingWrapper.getName()));
        if (!isDefaultRecordingRunning) {
            RecordingConfig recordingConfig = RecordingConfig.builder().withName(RecordingTemplate.DEFAULT.getRecordingName()).withToDisk(RecordingTemplate.DEFAULT.isToDisk()).withDumpOnExit(RecordingTemplate.DEFAULT.isDumpOnExit()).withMaxAge(this.jfrProperties.getMaxAge()).withMaxSize(this.jfrProperties.getMaxSize()).withDestination(this.getDumpOnExitRecordingName()).withThreadDumpInterval(this.jfrProperties.getThreadDumpInterval()).build();
            this.jfrRecordingService.createRecording(recordingConfig).ifPresent(RecordingWrapper::start);
            String configurationType = this.jfrProperties.isDefaultConfiguration() ? "Default" : "Custom";
            HashMap<String, String> extraAttributes = new HashMap<String, String>();
            extraAttributes.put("stp.jfr.audit.configuration", configurationType);
            extraAttributes.put("stp.jfr.audit.start.type", String.valueOf(isOnStart));
            this.auditor.audit("stp.jfr.audit.recording.started", extraAttributes);
        }
    }

    private String getDumpOnExitRecordingName() {
        Path recordingPath = Paths.get(this.applicationInfo.getLocalApplicationHome(), this.jfrProperties.getRecordingPath());
        String recordingFileName = JfrRecordingUtils.formatRecordingFileName(RecordingTemplate.DEFAULT.getRecordingName() + "_dump_on_exit");
        return recordingPath.resolve(recordingFileName).toString();
    }

    public synchronized void restartDefaultRecording() {
        this.isRestarting = true;
        this.stopDefaultRecording();
        this.startDefaultRecording(false);
        this.isRestarting = false;
    }

    public boolean isRestarting() {
        return this.isRestarting;
    }
}

