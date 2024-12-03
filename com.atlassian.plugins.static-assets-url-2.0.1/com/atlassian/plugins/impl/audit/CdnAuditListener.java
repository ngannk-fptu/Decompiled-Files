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
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.impl.audit;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.impl.CdnConfigurationChangedEvent;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class CdnAuditListener {
    public static final String AUDIT_LOGGING_PREFIX = "audit.logging.static.assets.";
    public static final String CATEGORY_KEY = "audit.logging.static.assets.admin.category";
    public static final String SUMMARY_KEY = "audit.logging.static.assets.config.summary";
    public static final String CHANGED_VALUE_ENABLED = "audit.logging.static.assets.value.enabled";
    public static final String CHANGED_VALUE_PROVIDER = "audit.logging.static.assets.value.provider";
    private final EventPublisher eventPublisher;
    private final AuditService auditService;

    public CdnAuditListener(@ComponentImport EventPublisher eventPublisher, @ComponentImport AuditService auditService) {
        this.eventPublisher = eventPublisher;
        this.auditService = auditService;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onCdnConfigurationChanged(CdnConfigurationChangedEvent event) {
        AuditType auditType = AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)CATEGORY_KEY, (String)SUMMARY_KEY).build();
        this.auditService.audit(AuditEvent.builder((AuditType)auditType).changedValue(ChangedValue.fromI18nKeys((String)CHANGED_VALUE_ENABLED).to(Boolean.toString(event.isEnabled())).build()).changedValue(ChangedValue.fromI18nKeys((String)CHANGED_VALUE_PROVIDER).to(event.getProvider()).build()).build());
    }
}

