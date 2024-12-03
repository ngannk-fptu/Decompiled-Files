/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.pats.events.audit;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.pats.events.audit.AuditLogHandler;
import com.atlassian.pats.events.token.TokenCreatedEvent;
import com.atlassian.pats.events.token.TokenDeletedEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AuditEventListener
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final AuditLogHandler auditLogHandler;

    public AuditEventListener(EventPublisher eventPublisher, AuditLogHandler auditLogHandler) {
        this.eventPublisher = eventPublisher;
        this.auditLogHandler = auditLogHandler;
    }

    @EventListener
    public void onTokenCreatedEvent(TokenCreatedEvent event) {
        this.auditLogHandler.logTokenCreated(event);
    }

    @EventListener
    public void onTokenDeletedEvent(TokenDeletedEvent event) {
        this.auditLogHandler.logTokenDeleted(event);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

