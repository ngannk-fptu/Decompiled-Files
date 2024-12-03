/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.zip;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.confluence.zip.CancelSupportZipEvent;
import com.atlassian.troubleshooting.confluence.zip.CreateSupportZipEvent;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import com.atlassian.troubleshooting.stp.zip.SupportZipService;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class SupportZipEventListener
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final SupportZipService supportZipService;

    @Autowired
    public SupportZipEventListener(EventPublisher eventPublisher, SupportZipService supportZipService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.supportZipService = Objects.requireNonNull(supportZipService);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handle(ClusterEventWrapper clusterEventWrapper) {
        Event wrappedEvent = clusterEventWrapper.getEvent();
        if (wrappedEvent instanceof CancelSupportZipEvent) {
            this.cancelSupportZip((CancelSupportZipEvent)wrappedEvent);
        } else if (wrappedEvent instanceof CreateSupportZipEvent) {
            this.createSupportZip((CreateSupportZipEvent)wrappedEvent);
        }
    }

    private void cancelSupportZip(CancelSupportZipEvent cancelEvent) {
        Optional<CreateSupportZipMonitor> maybeMonitor = this.supportZipService.getMonitorWithoutPermissionCheck(cancelEvent.getTaskId());
        maybeMonitor.ifPresent(this.supportZipService::cancelSupportZipTaskOnThisNode);
    }

    private void createSupportZip(CreateSupportZipEvent createSupportZipEvent) {
        SupportZipRequest supportZipRequest = createSupportZipEvent.getSupportZipRequest();
        this.supportZipService.createLocalSupportZipWithoutPermissionCheck(supportZipRequest);
    }
}

