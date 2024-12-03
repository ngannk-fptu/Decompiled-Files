/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.event.application.ApplicationReadyEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.embedded.directory;

import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.event.application.ApplicationReadyEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

@Deprecated
public class LdapConnectionPoolInitialisationListener {
    private final ApplicationFactory applicationFactory;

    public LdapConnectionPoolInitialisationListener(ApplicationFactory applicationFactory, EventPublisher eventPublisher) {
        this.applicationFactory = applicationFactory;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void handleEvent(ApplicationReadyEvent event) {
    }
}

