/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.user.UserManager
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.user.UserManager;
import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractAuditListener
implements InitializingBean,
DisposableBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AuditService auditService;
    private final EventPublisher eventPublisher;
    private final I18nResolver i18nResolver;
    private final LocaleResolver localeResolver;
    protected final StandardAuditResourceTypes resourceTypes;
    private final AuditingContext auditingContext;
    protected final UserManager userManager;

    protected AbstractAuditListener(AuditService auditService, EventPublisher eventPublisher, I18nResolver i18nResolver, LocaleResolver localeResolver, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext, UserManager userManager) {
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
        this.i18nResolver = i18nResolver;
        this.localeResolver = localeResolver;
        this.resourceTypes = resourceTypes;
        this.auditingContext = auditingContext;
        this.userManager = userManager;
    }

    protected void save(Supplier<AuditEvent> eventSupplier) {
        this.saveIfPresent(() -> Optional.of((AuditEvent)eventSupplier.get()));
    }

    protected void saveIfPresent(Supplier<Optional<AuditEvent>> eventSupplier) {
        String summaryKey = eventSupplier.get().map(AuditEvent::getActionI18nKey).orElse(null);
        if (!this.auditingContext.skipAuditing(summaryKey)) {
            try {
                eventSupplier.get().ifPresent(arg_0 -> ((AuditService)this.auditService).audit(arg_0));
            }
            catch (Exception e) {
                this.log.error("Error processing auditing event", (Throwable)e);
            }
        }
    }

    protected AuditResource buildResourceWithoutId(String name, String type) {
        return this.buildResource(name, type, null);
    }

    protected AuditResource buildResource(String name, String type, long id) {
        return this.buildResource(name, type, String.valueOf(id));
    }

    protected AuditResource buildResource(String name, String type, @Nullable String id) {
        return AuditResource.builder((String)Optional.ofNullable(name).orElse("Undefined"), (String)type).id(id).build();
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

