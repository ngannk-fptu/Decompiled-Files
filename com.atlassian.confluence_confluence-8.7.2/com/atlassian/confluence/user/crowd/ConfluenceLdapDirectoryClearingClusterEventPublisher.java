/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.LdapDirectoryClearingClusterEventPublisher
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.user.crowd.ConfluenceLdapDirectoryClearingClusterEvent;
import com.atlassian.crowd.directory.LdapDirectoryClearingClusterEventPublisher;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceLdapDirectoryClearingClusterEventPublisher
implements LdapDirectoryClearingClusterEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceLdapDirectoryClearingClusterEventPublisher.class);
    private final EventPublisher eventPublisher;

    public ConfluenceLdapDirectoryClearingClusterEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(Long directoryId) {
        log.info("Publishing LdapDirectoryClearing cluster event for directory {}", (Object)directoryId);
        this.eventPublisher.publish((Object)new ConfluenceLdapDirectoryClearingClusterEvent(this, directoryId));
    }
}

