/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.auditing.listeners;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractEventListener
implements InitializingBean,
DisposableBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AuditService auditService;
    protected final EventListenerRegistrar eventListenerRegistrar;
    private final I18nResolver i18nResolver;
    private final LocaleResolver localeResolver;
    private final AuditingContext auditingContext;

    protected AbstractEventListener(AuditService auditService, EventListenerRegistrar eventListenerRegistrar, I18nResolver i18nResolver, LocaleResolver localeResolver, AuditingContext auditingContext) {
        this.auditService = auditService;
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.i18nResolver = i18nResolver;
        this.localeResolver = localeResolver;
        this.auditingContext = auditingContext;
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

    String translate(String messageKey) {
        return this.i18nResolver.getText(this.localeResolver.getLocale((UserKey)null), messageKey);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventListenerRegistrar.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    protected Logger getLogger() {
        return this.log;
    }
}

