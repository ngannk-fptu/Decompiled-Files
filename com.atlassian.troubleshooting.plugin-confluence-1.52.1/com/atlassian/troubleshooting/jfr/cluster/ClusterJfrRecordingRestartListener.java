/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.cluster;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.ClusterMessagingService;
import com.atlassian.troubleshooting.api.ListenerRegistration;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.service.JfrAlwaysOnRecordingService;
import com.atlassian.troubleshooting.jfr.service.JfrSettingsService;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class ClusterJfrRecordingRestartListener
implements LifecycleAware {
    public static final String JFR_RESTART_RECORDING_CHANNEL_NAME = "jfr_restart";
    private ListenerRegistration listenerRegistration;
    private final ClusterMessagingService clusterMessagingService;
    private final JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService;
    private final JfrSettingsService jfrSettingsService;

    @Autowired
    public ClusterJfrRecordingRestartListener(ClusterMessagingService clusterMessagingService, JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService, JfrSettingsService jfrSettingsService) {
        this.clusterMessagingService = Objects.requireNonNull(clusterMessagingService);
        this.jfrAlwaysOnRecordingService = Objects.requireNonNull(jfrAlwaysOnRecordingService);
        this.jfrSettingsService = Objects.requireNonNull(jfrSettingsService);
    }

    public void onStart() {
        this.listenerRegistration = this.registerListener();
    }

    public void onStop() {
        Optional.ofNullable(this.listenerRegistration).ifPresent(ListenerRegistration::unregister);
    }

    private ListenerRegistration registerListener() {
        return this.clusterMessagingService.registerListener(JFR_RESTART_RECORDING_CHANNEL_NAME, message -> {
            this.jfrAlwaysOnRecordingService.restartDefaultRecording();
            this.setJfrRecordingEnabled();
        });
    }

    private void setJfrRecordingEnabled() {
        JfrSettings jfrEnabledSettings = new JfrSettings(true);
        this.jfrSettingsService.storeSettings(jfrEnabledSettings);
    }
}

