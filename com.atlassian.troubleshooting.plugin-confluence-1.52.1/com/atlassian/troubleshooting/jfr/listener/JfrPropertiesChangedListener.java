/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.listener;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.ClusterMessagingService;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.event.JfrPropertiesChangedEvent;
import com.atlassian.troubleshooting.jfr.service.JfrAlwaysOnRecordingService;
import com.atlassian.troubleshooting.jfr.service.JfrSettingsService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrPropertiesChangedListener
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final ClusterMessagingService clusterMessagingService;
    private final JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService;
    private final JfrSettingsService jfrSettingsService;

    @Autowired
    public JfrPropertiesChangedListener(EventPublisher eventPublisher, ClusterMessagingService clusterMessagingService, JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService, JfrSettingsService jfrSettingsService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clusterMessagingService = Objects.requireNonNull(clusterMessagingService);
        this.jfrAlwaysOnRecordingService = Objects.requireNonNull(jfrAlwaysOnRecordingService);
        this.jfrSettingsService = Objects.requireNonNull(jfrSettingsService);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onJfrPropertiesChanged(JfrPropertiesChangedEvent jfrPropertiesChangedEvent) {
        boolean isJfrRecordingEnabled = this.jfrSettingsService.getSettings().isEnabled();
        if (isJfrRecordingEnabled) {
            this.jfrAlwaysOnRecordingService.restartDefaultRecording();
            this.setJfrRecordingEnabled();
            this.notifyOtherNodes();
        }
    }

    private void setJfrRecordingEnabled() {
        JfrSettings jfrEnabledSettings = new JfrSettings(true);
        this.jfrSettingsService.storeSettings(jfrEnabledSettings);
    }

    private void notifyOtherNodes() {
        this.clusterMessagingService.sendMessage("jfr_restart", "restart");
    }
}

