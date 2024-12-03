/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.user.UserRemoveEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.plugins.createcontent.services.UserStorageService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoveUserListener {
    private static final Logger log = LoggerFactory.getLogger(RemoveUserListener.class);
    private final EventPublisher eventPublisher;
    private final UserStorageService userStorageService;

    @Autowired
    public RemoveUserListener(EventPublisher eventPublisher, UserStorageService userStorageService) {
        this.eventPublisher = eventPublisher;
        this.userStorageService = userStorageService;
    }

    @EventListener
    public void userDeleted(UserRemoveEvent event) {
        this.userStorageService.removeKeyForUser("quick-create", event.getUser());
        log.debug("Removed quick-create discovery record for deleted user");
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }
}

