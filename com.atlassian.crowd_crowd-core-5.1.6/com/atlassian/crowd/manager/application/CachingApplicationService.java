/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.application.ApplicationUpdatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.cache.UserAuthorisationCache;
import com.atlassian.crowd.event.application.ApplicationUpdatedEvent;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.AbstractDelegatingApplicationService;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.User;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CachingApplicationService
extends AbstractDelegatingApplicationService {
    private final UserAuthorisationCache userAuthorisationCache;
    private final EventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(CachingApplicationService.class);

    public CachingApplicationService(ApplicationService applicationService, UserAuthorisationCache userAuthorisationCache, EventPublisher eventPublisher) {
        super(applicationService);
        this.userAuthorisationCache = (UserAuthorisationCache)Preconditions.checkNotNull((Object)userAuthorisationCache);
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void registerListener() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregisterListener() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public boolean isUserAuthorised(Application application, String username) {
        try {
            User user = this.findUserByName(application, username);
            return this.isUserAuthorised(application, user);
        }
        catch (UserNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean isUserAuthorised(Application application, User user) {
        Boolean allowedToAuthenticate = this.userAuthorisationCache.isPermitted(user, application.getName());
        if (allowedToAuthenticate != null) {
            return allowedToAuthenticate;
        }
        boolean permitted = this.getApplicationService().isUserAuthorised(application, user);
        if (permitted) {
            this.userAuthorisationCache.setPermitted(user, application.getName(), permitted);
        }
        return permitted;
    }

    @EventListener
    public void onApplicationUpdated(ApplicationUpdatedEvent event) {
        this.clearCache(event);
    }

    @EventListener
    public void onDirectoryUpdated(DirectoryUpdatedEvent event) {
        this.clearCache(event);
    }

    @EventListener
    public void onDirectoryDeleted(DirectoryDeletedEvent event) {
        this.clearCache(event);
    }

    @EventListener
    public void onBackupRestored(XMLRestoreFinishedEvent event) {
        this.clearCache(event);
    }

    private void clearCache(Object event) {
        log.debug("Clearing userAuthorisationCache on {}", event);
        this.userAuthorisationCache.clear();
    }
}

