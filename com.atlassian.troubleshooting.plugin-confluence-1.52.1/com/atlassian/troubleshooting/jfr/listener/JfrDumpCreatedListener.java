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
import com.atlassian.troubleshooting.jfr.event.JfrDumpCreatedEvent;
import com.atlassian.troubleshooting.jfr.service.JfrRecordingCleanUpService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrDumpCreatedListener
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final JfrRecordingCleanUpService jfrRecordingCleanUpService;

    @Autowired
    public JfrDumpCreatedListener(EventPublisher eventPublisher, JfrRecordingCleanUpService jfrRecordingCleanUpService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.jfrRecordingCleanUpService = Objects.requireNonNull(jfrRecordingCleanUpService);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onJfrDumpCreatedEvent(JfrDumpCreatedEvent jfrDumpCreatedEvent) {
        this.jfrRecordingCleanUpService.cleanUpStaleRecordings();
    }
}

