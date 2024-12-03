/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.monitoring.AbstractMonitoringAuditEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.event.api.EventListener;

public class MonitoringAuditListener
extends AbstractAuditListener {
    public static final String MONITORING_DISABLED_ATTRIBUTE = AuditHelper.buildExtraAttribute("disabled.monitoring");
    public static final String MONITORING_ENABLED_ATTRIBUTE = AuditHelper.buildExtraAttribute("enabled.monitoring");
    public static final String MONITORING_CHANGED_SUMMARY = AuditHelper.buildSummaryTextKey("monitoring.changed");

    public MonitoringAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void ipdToggleChanged(AbstractMonitoringAuditEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.buildAuditType()).extraAttribute(this.buildExtraAttribute(event)).build());
    }

    private AuditAttribute buildExtraAttribute(AbstractMonitoringAuditEvent event) {
        return AuditAttribute.fromI18nKeys((String)(event.isMonitoringBeingEnabled() ? MONITORING_ENABLED_ATTRIBUTE : MONITORING_DISABLED_ATTRIBUTE), (String)String.join((CharSequence)", ", event.getChangedMonitoringNames())).build();
    }

    private AuditType buildAuditType() {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.SYSTEM, (String)MONITORING_CHANGED_SUMMARY).build();
    }
}

