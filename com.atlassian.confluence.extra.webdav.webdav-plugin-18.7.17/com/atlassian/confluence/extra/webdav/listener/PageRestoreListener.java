/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.page.PageRestoreEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.extra.webdav.listener;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionStore;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionTask;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;

public final class PageRestoreListener {
    private final EventPublisher eventPublisher;
    private final ConfluenceDavSessionStore confluenceDavSessionStore;

    @Autowired
    public PageRestoreListener(EventPublisher eventPublisher, ConfluenceDavSessionStore confluenceDavSessionStore) {
        this.eventPublisher = eventPublisher;
        this.confluenceDavSessionStore = confluenceDavSessionStore;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleEvent(PageRestoreEvent event) {
        this.confluenceDavSessionStore.executeTaskOnSessions(new ContentEntityAttributesResetDavSessionTask(event.getContent()));
    }

    private static class ContentEntityAttributesResetDavSessionTask
    implements ConfluenceDavSessionTask {
        private final ContentEntityObject ceo;

        private ContentEntityAttributesResetDavSessionTask(ContentEntityObject ceo) {
            this.ceo = ceo;
        }

        @Override
        public void execute(ConfluenceDavSession confluenceDavSession) {
            confluenceDavSession.getResourceStates().resetContentAttributes(this.ceo);
        }
    }
}

