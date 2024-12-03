/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.auding;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportScope;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PdfExportAuditListener {
    private static final Logger log = LoggerFactory.getLogger(PdfExportAuditListener.class);
    public static final String AUDIT_CATEGORY_IMPORT_EXPORT = "audit.logging.category.import.export";
    public static final String AUDIT_SUMMARY_SPACE_KEY = "audit.logging.summary.space.pdf.export";
    public static final String AUDIT_SUMMARY_PAGE_KEY = "audit.logging.summary.page.pdf.export";
    public static final String AUDIT_SUMMARY_BLOG_KEY = "audit.logging.summary.blog.pdf.export";
    private final EventPublisher eventPublisher;
    private final AuditService auditService;
    private final StandardAuditResourceTypes resourceTypes;

    public PdfExportAuditListener(@ComponentImport EventPublisher eventPublisher, @ComponentImport AuditService auditService, @ComponentImport StandardAuditResourceTypes resourceTypes) {
        this.eventPublisher = eventPublisher;
        this.auditService = auditService;
        this.resourceTypes = resourceTypes;
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
    public void onPDFExport(PdfExportEvent event) {
        if (event.getExportScope() == ExportScope.SPACE) {
            log.info("Auditing PDF export of space");
            this.performSpaceAudit(event);
            return;
        }
        if (event.getExportScope() == ExportScope.PAGE && "page".equals(event.getPageType())) {
            log.info("Auditing PDF export of page");
            this.performPageAudit(event);
            return;
        }
        if (event.getExportScope() == ExportScope.PAGE && "blogpost".equals(event.getPageType())) {
            log.info("Auditing PDF export of blog post");
            this.performBlogAudit(event);
            return;
        }
        log.warn("Unknown PDF export type for scope '{}' and page type '{}'. No audit is performed on this action", (Object)event.getExportScope(), (Object)event.getPageType());
    }

    private void performSpaceAudit(PdfExportEvent event) {
        AuditResource affectedSpace = AuditResource.builder((String)event.getSpaceName(), (String)this.resourceTypes.space()).id(String.valueOf(event.getSpaceId())).build();
        this.performAudit(AUDIT_CATEGORY_IMPORT_EXPORT, AUDIT_SUMMARY_SPACE_KEY, CoverageArea.LOCAL_CONFIG_AND_ADMINISTRATION, affectedSpace);
    }

    private void performPageAudit(PdfExportEvent event) {
        AuditResource affectedSpace = AuditResource.builder((String)event.getSpaceName(), (String)this.resourceTypes.space()).id(String.valueOf(event.getSpaceId())).build();
        AuditResource affectedPage = AuditResource.builder((String)event.getPageTitle(), (String)this.resourceTypes.page()).id(String.valueOf(event.getPageId())).build();
        this.performAudit(AUDIT_CATEGORY_IMPORT_EXPORT, AUDIT_SUMMARY_PAGE_KEY, CoverageArea.END_USER_ACTIVITY, affectedSpace, affectedPage);
    }

    private void performBlogAudit(PdfExportEvent event) {
        AuditResource affectedSpace = AuditResource.builder((String)event.getSpaceName(), (String)this.resourceTypes.space()).id(String.valueOf(event.getSpaceId())).build();
        AuditResource affectedBlog = AuditResource.builder((String)event.getPageTitle(), (String)this.resourceTypes.page()).id(String.valueOf(event.getPageId())).build();
        this.performAudit(AUDIT_CATEGORY_IMPORT_EXPORT, AUDIT_SUMMARY_BLOG_KEY, CoverageArea.END_USER_ACTIVITY, affectedSpace, affectedBlog);
    }

    private void performAudit(String categoryKey, String summaryKey, CoverageArea area, AuditResource ... affectedObjects) {
        List<AuditResource> affectedObjectsList = Arrays.asList(affectedObjects);
        AuditType auditType = AuditType.fromI18nKeys((CoverageArea)area, (CoverageLevel)CoverageLevel.ADVANCED, (String)categoryKey, (String)summaryKey).build();
        this.auditService.audit(AuditEvent.builder((AuditType)auditType).appendAffectedObjects(affectedObjectsList).build());
        affectedObjectsList.forEach(auditResource -> log.info("Audit - affected object [{}] id: {}", (Object)auditResource.getType(), (Object)auditResource.getId()));
    }
}

