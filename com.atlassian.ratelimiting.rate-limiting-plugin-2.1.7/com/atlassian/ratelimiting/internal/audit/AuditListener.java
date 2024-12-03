/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.ratelimiting.internal.audit;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.audit.AuditService;
import com.atlassian.ratelimiting.events.RateLimitingDisabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingDryRunEnabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingEnabledEvent;
import com.atlassian.ratelimiting.events.SystemRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsCreatedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsDeletedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.internal.audit.AuditEntryFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AuditListener
implements InitializingBean,
DisposableBean {
    private final AuditService auditService;
    private final EventPublisher eventPublisher;
    private final AuditEntryFactory auditEntryFactory;

    public AuditListener(AuditService auditService, EventPublisher eventPublisher, AuditEntryFactory auditEntryFactory) {
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
        this.auditEntryFactory = auditEntryFactory;
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onRateLimitingDisabled(RateLimitingDisabledEvent event) {
        this.auditService.store(this.auditEntryFactory.create(event));
    }

    @EventListener
    public void onRateLimitingEnabled(RateLimitingEnabledEvent event) {
        this.auditService.store(this.auditEntryFactory.create(event));
    }

    @EventListener
    public void onRateLimitingDryRunEnabled(RateLimitingDryRunEnabledEvent event) {
        this.auditService.store(this.auditEntryFactory.create(event));
    }

    @EventListener
    public void onSystemSettingsModified(SystemRateLimitSettingsModifiedEvent event) {
        this.auditService.store(this.auditEntryFactory.create(event));
    }

    @EventListener
    public void onUserSettingsCreated(UserRateLimitSettingsCreatedEvent event) {
        this.auditService.store(this.auditEntryFactory.create(event));
    }

    @EventListener
    public void onUserSettingsDeleted(UserRateLimitSettingsDeletedEvent event) {
        this.auditService.store(this.auditEntryFactory.create(event));
    }

    @EventListener
    public void onUserSettingsModified(UserRateLimitSettingsModifiedEvent event) {
        this.auditService.store(this.auditEntryFactory.create(event));
    }
}

