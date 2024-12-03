/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.confluence.plugins.synchrony.api.events.SynchronyRestartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.auditing.listeners;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.plugins.auditing.listeners.AbstractEventListener;
import com.atlassian.confluence.plugins.auditing.utils.AuditCategories;
import com.atlassian.confluence.plugins.auditing.utils.MessageKeyBuilder;
import com.atlassian.confluence.plugins.synchrony.api.events.SynchronyRestartedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchronyRestartEventListener")
public class SynchronyRestartEventListener
extends AbstractEventListener {
    @Autowired
    public SynchronyRestartEventListener(@ComponentImport AuditService auditBroker, @ComponentImport(value="eventPublisher") EventListenerRegistrar eventListenerRegistrar, @ComponentImport I18nResolver i18nResolver, @ComponentImport LocaleResolver localeResolver, @ComponentImport AuditingContext auditingContext) {
        super(auditBroker, eventListenerRegistrar, i18nResolver, localeResolver, auditingContext);
    }

    @EventListener
    public void onSynchronyRestartedEvent(SynchronyRestartedEvent event) {
        AuditType auditType = AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN_CATEGORY, (String)MessageKeyBuilder.buildSummaryTextKey("synchrony.restart")).build();
        this.save(() -> AuditEvent.builder((AuditType)auditType).changedValue(this.getChangedValue(event.isSuccessful())).build());
    }

    private ChangedValue getChangedValue(boolean successful) {
        return ChangedValue.fromI18nKeys((String)MessageKeyBuilder.buildChangedValueTextKey("synchrony.restart.name")).to(successful ? this.translate(MessageKeyBuilder.buildChangedValueTextKey("completed")) : this.translate(MessageKeyBuilder.buildChangedValueTextKey("failed"))).build();
    }
}

