/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.DirectoryEvent
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public final class ConfluenceDirectoryEventListener
implements ApplicationListener {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceDirectoryEventListener.class);
    private final ClusterManager clusterManager;
    private final EventPublisher eventPublisher;
    private final PlatformTransactionManager transactionManager;

    public ConfluenceDirectoryEventListener(ClusterManager clusterManager, EventPublisher eventPublisher, PlatformTransactionManager transactionManager) {
        this.clusterManager = clusterManager;
        this.eventPublisher = eventPublisher;
        this.transactionManager = transactionManager;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void handleEventOnThisNode(DirectoryDeletedEvent directoryDeletedEvent) {
        if (directoryDeletedEvent.getSource() != null) {
            log.debug("Received directory deleted event locally, republishing to other nodes: {}", (Object)directoryDeletedEvent);
            this.clusterManager.publishEvent(new ClusterDirectoryDeletedEvent((Object)this, directoryDeletedEvent));
        }
    }

    @EventListener
    public void handleEventOnThisNode(DirectoryUpdatedEvent directoryUpdatedEvent) {
        if (directoryUpdatedEvent.getSource() != null) {
            log.debug("Received directory updated event locally, republishing to other nodes: {}", (Object)directoryUpdatedEvent);
            this.clusterManager.publishEvent(new ClusterDirectoryUpdatedEvent((Object)this, directoryUpdatedEvent));
        }
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ClusterDirectoryEvent) {
            DirectoryEvent republishedEvent = ((ClusterDirectoryEvent)event).getRepublishedEvent();
            log.debug("Received clustered directory event from another cluster node, republishing it locally: {}", (Object)republishedEvent);
            new TransactionTemplate(this.transactionManager).execute(status -> {
                this.eventPublisher.publish((Object)republishedEvent);
                return null;
            });
        }
    }

    public static class ClusterDirectoryUpdatedEvent
    extends ClusterDirectoryEvent {
        private static final long serialVersionUID = 4657020285393732047L;

        public ClusterDirectoryUpdatedEvent(Object source, DirectoryUpdatedEvent wrappedEvent) {
            super(source, wrappedEvent.getDirectory());
        }

        public DirectoryUpdatedEvent getRepublishedEvent() {
            return new DirectoryUpdatedEvent(null, null, this.directory);
        }
    }

    public static class ClusterDirectoryDeletedEvent
    extends ClusterDirectoryEvent {
        private static final long serialVersionUID = -3408071357588836030L;

        public ClusterDirectoryDeletedEvent(Object source, DirectoryDeletedEvent wrappedEvent) {
            super(source, wrappedEvent.getDirectory());
        }

        public DirectoryDeletedEvent getRepublishedEvent() {
            return new DirectoryDeletedEvent(null, this.directory);
        }
    }

    protected static abstract class ClusterDirectoryEvent
    extends ConfluenceEvent
    implements ClusterEvent {
        private static final long serialVersionUID = 3358677247839113847L;
        protected final Directory directory;

        protected ClusterDirectoryEvent(Object src, Directory directory) {
            super(src);
            this.directory = directory;
        }

        public abstract DirectoryEvent getRepublishedEvent();
    }
}

