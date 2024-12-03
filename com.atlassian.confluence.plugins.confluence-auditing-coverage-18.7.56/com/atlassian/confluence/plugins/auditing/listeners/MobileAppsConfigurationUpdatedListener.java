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
 *  com.atlassian.confluence.plugins.mobile.analytic.MobileSimpleAnalyticEvent
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
import com.atlassian.confluence.plugins.mobile.analytic.MobileSimpleAnalyticEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="mobileAppsConfigurationUpdatedListener")
public class MobileAppsConfigurationUpdatedListener
extends AbstractEventListener {
    private static final String DISABLED_EVENT_NAME = "confluence.mobile.native.server.push.status.disabled";
    private static final String ENABLED_EVENT_NAME = "confluence.mobile.native.server.push.status.enabled";

    @Autowired
    public MobileAppsConfigurationUpdatedListener(@ComponentImport AuditService auditBroker, @ComponentImport(value="eventPublisher") EventListenerRegistrar eventListenerRegistrar, @ComponentImport I18nResolver i18nResolver, @ComponentImport LocaleResolver localeResolver, @ComponentImport AuditingContext auditingContext) {
        super(auditBroker, eventListenerRegistrar, i18nResolver, localeResolver, auditingContext);
    }

    @EventListener
    public void onConfigUpdatedEvent(MobileSimpleAnalyticEvent event) {
        String eventName = event.getEventName();
        if (this.isAuditable(eventName)) {
            AuditType auditType = AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN_CATEGORY, (String)MessageKeyBuilder.buildSummaryTextKey("mobile.apps")).build();
            this.save(() -> AuditEvent.builder((AuditType)auditType).changedValue(this.getChangedValue(eventName)).build());
        }
    }

    private boolean isAuditable(String eventName) {
        return DISABLED_EVENT_NAME.equals(eventName) || ENABLED_EVENT_NAME.equals(eventName);
    }

    private ChangedValue getChangedValue(String eventName) {
        return ChangedValue.fromI18nKeys((String)MessageKeyBuilder.buildChangedValueTextKey("mobile.apps.mode")).to(this.translate(ENABLED_EVENT_NAME.equals(eventName) ? MessageKeyBuilder.buildChangedValueTextKey("turned.on") : MessageKeyBuilder.buildChangedValueTextKey("turned.off"))).build();
    }
}

