/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.crowd.event.application.ApplicationReadyEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.crowd.event.application.ApplicationReadyEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;

public class CrowdLifecycle {
    private final EventPublisher eventPublisher;

    @Autowired
    public CrowdLifecycle(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onStart(ApplicationStartedEvent event) {
        this.eventPublisher.publish((Object)new ApplicationReadyEvent((Object)this));
    }
}

