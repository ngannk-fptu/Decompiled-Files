/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent
 *  com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.synchrony.events.exported.SynchronyRequestEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service;

import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.ReconciliationHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.SynchronyRequestsHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.SettingsManager;
import com.atlassian.confluence.plugins.collaborative.content.feedback.service.event.CollaborativeEditingAsyncEvent;
import com.atlassian.confluence.plugins.synchrony.events.exported.SynchronyRequestEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataCollectionService
implements DisposableBean,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(DataCollectionService.class);
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;
    private final SettingsManager settingsManager;
    private final ReconciliationHistoryDao reconciliationHistoryDao;
    private final SynchronyRequestsHistoryDao synchronyRequestsHistoryDao;

    @Autowired
    public DataCollectionService(@ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, SettingsManager settingsManager, ReconciliationHistoryDao reconciliationHistoryDao, SynchronyRequestsHistoryDao synchronyRequestsHistoryDao) {
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.settingsManager = settingsManager;
        this.reconciliationHistoryDao = reconciliationHistoryDao;
        this.synchronyRequestsHistoryDao = synchronyRequestsHistoryDao;
    }

    @EventListener
    public void onSynchronyRequestEvent(SynchronyRequestEvent synchronyRequestEvent) {
        if (this.settingsManager.collaborativeEditingEnabled()) {
            this.executeSwallowingAllExceptions(() -> {
                AbstractPage content = this.pageManager.getAbstractPage(synchronyRequestEvent.getContentId());
                if (content != null) {
                    this.eventPublisher.publish((Object)CollaborativeEditingAsyncEvent.from(synchronyRequestEvent.getContentId(), synchronyRequestEvent));
                }
                return null;
            });
        }
    }

    @EventListener
    public void onSynchronyRecoveryEvent(SynchronyRecoveryEvent synchronyRecoveryEvent) {
        if (this.settingsManager.collaborativeEditingEnabled()) {
            this.executeSwallowingAllExceptions(() -> {
                AbstractPage content = this.pageManager.getAbstractPage(synchronyRecoveryEvent.getCurrentContentId().asLong());
                if (content != null) {
                    this.eventPublisher.publish((Object)CollaborativeEditingAsyncEvent.from(synchronyRecoveryEvent.getCurrentContentId().asLong(), synchronyRecoveryEvent, content.getSynchronyRevision(), content.getConfluenceRevision()));
                }
                return null;
            });
        }
    }

    @EventListener
    public void onContentUpdatedEvent(ContentUpdatedEvent contentUpdatedEvent) {
        if (this.settingsManager.collaborativeEditingEnabled()) {
            this.executeSwallowingAllExceptions(() -> {
                AbstractPage content = this.pageManager.getAbstractPage(contentUpdatedEvent.getContentId().asLong());
                if (content != null) {
                    this.eventPublisher.publish((Object)CollaborativeEditingAsyncEvent.from(contentUpdatedEvent.getContentId().asLong(), contentUpdatedEvent, content.getConfluenceRevision()));
                }
                return null;
            });
        }
    }

    @EventListener
    public void onAttachmentCreate(AttachmentCreateEvent attachmentCreateEvent) {
        if (this.settingsManager.collaborativeEditingEnabled()) {
            this.executeSwallowingAllExceptions(() -> {
                AbstractPage abstractPage;
                if (attachmentCreateEvent.getAttachedTo() instanceof AbstractPage && (abstractPage = (AbstractPage)attachmentCreateEvent.getAttachedTo()).isCurrent()) {
                    this.eventPublisher.publish((Object)CollaborativeEditingAsyncEvent.from(abstractPage.getContentId().asLong(), attachmentCreateEvent, abstractPage.getSynchronyRevision(), abstractPage.getConfluenceRevision()));
                }
                return null;
            });
        }
    }

    @EventListener
    public void onAttachmentUpdate(AttachmentUpdateEvent attachmentUpdateEvent) {
        if (this.settingsManager.collaborativeEditingEnabled()) {
            this.executeSwallowingAllExceptions(() -> {
                AbstractPage abstractPage;
                if (attachmentUpdateEvent.getAttachedTo() instanceof AbstractPage && (abstractPage = (AbstractPage)attachmentUpdateEvent.getAttachedTo()).isCurrent()) {
                    this.eventPublisher.publish((Object)CollaborativeEditingAsyncEvent.from(abstractPage.getContentId().asLong(), attachmentUpdateEvent, abstractPage.getSynchronyRevision(), abstractPage.getConfluenceRevision()));
                }
                return null;
            });
        }
    }

    @EventListener
    public void onAsyncEvent(CollaborativeEditingAsyncEvent event) {
        this.executeSwallowingAllExceptions(() -> {
            if (event.getEventType() == CollaborativeEditingAsyncEvent.EventType.SYNCHRONY_REQUEST) {
                this.synchronyRequestsHistoryDao.add(event.getContentId(), (String)event.getProperty(CollaborativeEditingAsyncEvent.Key.TYPE), (String)event.getProperty(CollaborativeEditingAsyncEvent.Key.URL), (Map)event.getProperty(CollaborativeEditingAsyncEvent.Key.PROPERTIES), (Boolean)event.getProperty(CollaborativeEditingAsyncEvent.Key.SUCCESS_FLAG));
            } else if (event.getEventType() == CollaborativeEditingAsyncEvent.EventType.SYNCHRONY_RECOVERY || event.getEventType() == CollaborativeEditingAsyncEvent.EventType.CONTENT_UPDATED || event.getEventType() == CollaborativeEditingAsyncEvent.EventType.ATTACHMENT_CREATE || event.getEventType() == CollaborativeEditingAsyncEvent.EventType.ATTACHMENT_UPDATE) {
                this.reconciliationHistoryDao.add(event.getContentId(), Optional.ofNullable((String)event.getProperty(CollaborativeEditingAsyncEvent.Key.TYPE)).orElse(null), Optional.ofNullable((String)event.getProperty(CollaborativeEditingAsyncEvent.Key.SYNC_REV)).orElse(null), Optional.ofNullable((String)event.getProperty(CollaborativeEditingAsyncEvent.Key.CONF_REV)).orElse(null), Optional.ofNullable((String)event.getProperty(CollaborativeEditingAsyncEvent.Key.TRIGGER)).orElse(null));
            } else {
                log.warn("Unexpected event type: {}", (Object)event.getEventType());
            }
            return null;
        });
    }

    private void executeSwallowingAllExceptions(Callable<Void> action) {
        try {
            action.call();
        }
        catch (Exception swallowed) {
            log.error("Error handling event", (Throwable)swallowed);
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }
}

