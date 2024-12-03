/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SpringLdapPooledContextSourceProvider
 *  com.atlassian.crowd.directory.SpringLdapPooledContextSourceProvider$LdapPoolDestroyedReason
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.user.crowd.ConfluenceLdapDirectoryClearingClusterEvent;
import com.atlassian.crowd.directory.SpringLdapPooledContextSourceProvider;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSpringLdapPoolClearingClusterMessageListener {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSpringLdapPoolClearingClusterMessageListener.class);
    private final SpringLdapPooledContextSourceProvider springLdapPooledContextSourceProvider;

    public ConfluenceSpringLdapPoolClearingClusterMessageListener(SpringLdapPooledContextSourceProvider springLdapPooledContextSourceProvider) {
        this.springLdapPooledContextSourceProvider = springLdapPooledContextSourceProvider;
    }

    @EventListener
    public void handleDirectoryClearingClusterEvent(ConfluenceLdapDirectoryClearingClusterEvent event) {
        log.info("Received LdapDirectoryClearing cluster event. Removing context source for directory with id: {}", (Object)event.getDirectoryId());
        this.springLdapPooledContextSourceProvider.removeContextSource(Long.valueOf(event.getDirectoryId()), SpringLdapPooledContextSourceProvider.LdapPoolDestroyedReason.REQUESTED_BY_ANOTHER_NODE, false);
    }

    @EventListener
    public void handleDirectoryClearingClusterEvent(ClusterEventWrapper eventWrapper) {
        if (eventWrapper.getEvent() instanceof ConfluenceLdapDirectoryClearingClusterEvent) {
            this.handleDirectoryClearingClusterEvent((ConfluenceLdapDirectoryClearingClusterEvent)eventWrapper.getEvent());
        }
    }
}

