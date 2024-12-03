/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationEvent
 *  com.atlassian.confluence.event.events.content.mail.notification.SpaceNotificationEvent
 *  com.atlassian.confluence.search.ConfluenceIndexer
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.listeners;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationEvent;
import com.atlassian.confluence.event.events.content.mail.notification.SpaceNotificationEvent;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {
    private final ConfluenceIndexer indexer;
    private final EventPublisher eventPublisher;

    @Autowired
    public NotificationEventListener(@ComponentImport(value="indexer") ConfluenceIndexer indexer, @ComponentImport EventPublisher eventPublisher) {
        this.indexer = indexer;
        this.eventPublisher = eventPublisher;
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onEvent(ContentNotificationEvent event) {
        this.indexer.reIndexExcludingDependents((Searchable)event.getNotification().getContent());
    }

    @EventListener
    public void onEvent(SpaceNotificationEvent event) {
        this.indexer.reIndex((Searchable)event.getSpace());
    }
}

