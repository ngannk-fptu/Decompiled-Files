/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.web.context.HttpContext
 */
package com.atlassian.audit.csv;

import com.atlassian.audit.analytics.ExportEvent;
import com.atlassian.audit.ao.service.AuditedSearchService;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.csv.AuditCsvExporter;
import com.atlassian.audit.csv.LicenseException;
import com.atlassian.audit.csv.SelectiveExportLicenseChecker;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.rest.v1.DelegatedViewTypeProvider;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.web.context.HttpContext;

public class AuditCsvExportService {
    private static final AuditType AUDIT_LOG_EXPORTED = AuditType.fromI18nKeys((CoverageArea)CoverageArea.AUDIT_LOG, (CoverageLevel)CoverageLevel.BASE, (String)"atlassian.audit.event.category.audit", (String)"atlassian.audit.event.action.audit.exported").build();
    private final SelectiveExportLicenseChecker licenseChecker;
    private final AuditSearchService searchService;
    private final I18nResolver resolver;
    private final HttpContext httpContext;
    private final EventPublisher eventPublisher;
    private final AuditPluginInfo auditPluginInfo;
    private final AuditService auditService;
    private final DelegatedViewTypeProvider delegatedViewTypeProvider = new DelegatedViewTypeProvider();

    public AuditCsvExportService(SelectiveExportLicenseChecker licenseChecker, AuditSearchService searchService, I18nResolver resolver, HttpContext httpContext, EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, AuditService auditService) {
        this.licenseChecker = licenseChecker;
        this.searchService = searchService;
        this.resolver = resolver;
        this.httpContext = httpContext;
        this.eventPublisher = eventPublisher;
        this.auditPluginInfo = auditPluginInfo;
        this.auditService = auditService;
    }

    public AuditCsvExporter createExporter(AuditQuery query) throws LicenseException {
        if (this.isSelectiveExport(query) && !this.licenseChecker.allowSelectiveExport()) {
            throw new LicenseException("Attempted to selectively export without correct license to do so");
        }
        this.eventPublisher.publish((Object)new ExportEvent(this.isSelectiveExport(query), this.delegatedViewTypeProvider.getDelegatedViewType(this.httpContext), this.auditPluginInfo.getPluginVersion()));
        this.auditService.audit(AuditEvent.builder((AuditType)AUDIT_LOG_EXPORTED).extraAttribute(AuditAttribute.fromI18nKeys((String)"atlassian.audit.event.attribute.query", (String)AuditedSearchService.auditQueryToString(query, Integer.MAX_VALUE)).build()).build());
        return new AuditCsvExporter(this.searchService, query, this.resolver);
    }

    private boolean isSelectiveExport(AuditQuery query) {
        return !query.getActions().isEmpty() || !query.getCategories().isEmpty() || query.getFrom().isPresent() || query.getTo().isPresent() || !query.getUserIds().isEmpty() || query.getSearchText().isPresent();
    }
}

