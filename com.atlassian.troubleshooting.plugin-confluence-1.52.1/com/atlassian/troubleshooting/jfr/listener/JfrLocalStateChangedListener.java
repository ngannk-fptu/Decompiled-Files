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
import com.atlassian.troubleshooting.jfr.event.JfrLocalStateChangedEvent;
import com.atlassian.troubleshooting.jfr.service.JfrAlwaysOnRecordingService;
import com.atlassian.troubleshooting.jfr.service.JfrRecordingCleanUpService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrLocalStateChangedListener
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService;
    private final JfrRecordingCleanUpService jfrRecordingCleanUpService;

    @Autowired
    public JfrLocalStateChangedListener(EventPublisher eventPublisher, JfrAlwaysOnRecordingService jfrAlwaysOnRecordingService, JfrRecordingCleanUpService jfrRecordingCleanUpService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.jfrAlwaysOnRecordingService = Objects.requireNonNull(jfrAlwaysOnRecordingService);
        this.jfrRecordingCleanUpService = Objects.requireNonNull(jfrRecordingCleanUpService);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onJfrLocalStateChangedEvent(JfrLocalStateChangedEvent jfrLocalStateChangedEvent) {
        boolean jfrEnabled = jfrLocalStateChangedEvent.isEnabled();
        if (jfrEnabled) {
            this.jfrRecordingCleanUpService.cleanUpDumpOnExitStaleRecordings();
            this.jfrAlwaysOnRecordingService.startDefaultRecording(jfrLocalStateChangedEvent.isOnStart());
        } else {
            this.jfrAlwaysOnRecordingService.stopDefaultRecording();
        }
    }
}

