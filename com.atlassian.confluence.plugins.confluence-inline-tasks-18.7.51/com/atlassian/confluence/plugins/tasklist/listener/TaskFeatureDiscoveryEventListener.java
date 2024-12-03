/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.listener;

import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2CreateEvent;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskFeatureDiscoveryEventListener {
    private final EventPublisher eventPublisher;

    @Autowired
    public TaskFeatureDiscoveryEventListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public final void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public final void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void tasksCreated(ConfluenceTaskV2CreateEvent event) {
        FlashScope.put((String)"create-task", (Object)true);
    }
}

