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
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.event.JfrFeatureFlagStateChangedEvent;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.jfr.service.JfrSettingsService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrFeatureFlagStateListener
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final JfrSettingsService jfrSettingsService;
    private final JfrRecordingManager jfrRecordingManager;

    @Autowired
    public JfrFeatureFlagStateListener(EventPublisher eventPublisher, JfrSettingsService jfrSettingsService, JfrRecordingManager jfrRecordingManager) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.jfrSettingsService = Objects.requireNonNull(jfrSettingsService);
        this.jfrRecordingManager = Objects.requireNonNull(jfrRecordingManager);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onJfrFeatureFlagStateChanged(JfrFeatureFlagStateChangedEvent event) {
        JfrSettings settings = new JfrSettings(event.isEnabled());
        JfrSettings activeSettings = this.jfrSettingsService.getSettings();
        if (!activeSettings.equals(settings)) {
            this.jfrRecordingManager.handleFeatureFlagStateChanged(settings);
        }
    }
}

