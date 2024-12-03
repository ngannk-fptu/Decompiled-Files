/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.retention.listener;

import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyChangedEvent;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyCreatedEvent;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyDeletedEvent;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class SpaceRetentionPolicyIndexingListener {
    private final EventPublisher eventPublisher;
    private final ConfluenceIndexer confluenceIndexer;

    public SpaceRetentionPolicyIndexingListener(EventPublisher eventPublisher, ConfluenceIndexer confluenceIndexer) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.confluenceIndexer = Objects.requireNonNull(confluenceIndexer);
    }

    @PostConstruct
    public void register() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregister() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void policyCreated(SpaceRetentionPolicyCreatedEvent createdEvent) {
        this.reindex(createdEvent.getSpace());
    }

    @EventListener
    public void policyChanged(SpaceRetentionPolicyChangedEvent changedEvent) {
        this.reindex(changedEvent.getSpace());
    }

    @EventListener
    public void policyDeleted(SpaceRetentionPolicyDeletedEvent deletedEvent) {
        this.reindex(deletedEvent.getSpace());
    }

    private void reindex(Space space) {
        SpaceDescription spaceDescription = space.getDescription();
        this.confluenceIndexer.reIndex(spaceDescription);
    }
}

