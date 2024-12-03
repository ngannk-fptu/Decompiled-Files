/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.jfr;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import java.io.Serializable;
import java.util.Objects;
import jdk.jfr.RecordingState;
import org.springframework.beans.factory.annotation.Autowired;

public class JavaFlightRecorderHealthCheck
implements SupportHealthCheck {
    private final JfrRecordingManager jfrRecordingManager;
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;

    @Autowired
    public JavaFlightRecorderHealthCheck(JfrRecordingManager jfrRecordingManager, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        this.jfrRecordingManager = Objects.requireNonNull(jfrRecordingManager);
        this.supportHealthStatusBuilder = Objects.requireNonNull(supportHealthStatusBuilder);
    }

    @Override
    public SupportHealthStatus check() {
        if (this.jfrRecordingManager.getSettings().isEnabled()) {
            return this.isRecordingRunning() ? this.supportHealthStatusBuilder.ok(this, "healthcheck.jfr.ok", new Serializable[0]) : this.supportHealthStatusBuilder.warning(this, "healthcheck.jfr.fail", new Serializable[0]);
        }
        return this.supportHealthStatusBuilder.ok(this, "healthcheck.jfr.disabled", new Serializable[0]);
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    private boolean isRecordingRunning() {
        return this.jfrRecordingManager.getRecordingDetails().stream().filter(recordingDetails -> RecordingState.RUNNING.name().equals(recordingDetails.getState())).anyMatch(recordingDetails -> RecordingTemplate.DEFAULT.getRecordingName().equals(recordingDetails.getName()));
    }
}

