/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.event;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.server.MutableApplicationStatusService;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationStatusStartupListener {
    private static final Logger log = LoggerFactory.getLogger((String)"com.atlassian.confluence.impl.event.ApplicationStatusStartupListener");
    private final MutableApplicationStatusService applicationStatusService;

    public ApplicationStatusStartupListener(MutableApplicationStatusService applicationStatusService) {
        this.applicationStatusService = applicationStatusService;
    }

    @EventListener
    public void onStart(ApplicationStartedEvent event) {
        log.debug("Received ApplicationStartedEvent", (Object)event);
        this.applicationStatusService.notifyApplicationStarted();
    }
}

