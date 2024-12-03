/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.admin.AsyncExportFinishedEvent;
import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.event.api.EventListener;
import java.util.Optional;

public class ImportExportAuditListener
extends AbstractAuditListener {
    public static final String SPACE_IMPORT_SUMMARY = AuditHelper.buildSummaryTextKey("space.import");
    public static final String SITE_IMPORT_SUMMARY = AuditHelper.buildSummaryTextKey("site.import");
    public static final String SPACE_EXPORT_SUMMARY = AuditHelper.buildSummaryTextKey("space.export");
    public static final String SITE_EXPORT_SUMMARY = AuditHelper.buildSummaryTextKey("site.export");
    public static final String DESCRIPTION_EXTRA_ATTRIBUTE = AuditHelper.buildExtraAttribute("description");

    public ImportExportAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void importFinishedEvent(AsyncImportFinishedEvent event) {
        this.save(() -> {
            if (event.isSiteImport()) {
                return AuditEvent.fromI18nKeys((String)AuditCategories.IMPORT_EXPORT, (String)SITE_IMPORT_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).build();
            }
            String spaceKey = event.getImportContext().getSpaceKeyOfSpaceImport();
            return AuditEvent.fromI18nKeys((String)AuditCategories.IMPORT_EXPORT, (String)SPACE_IMPORT_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.LOCAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResource(this.auditHelper.fetchSpaceDisplayName(spaceKey), this.resourceTypes.space(), this.auditHelper.fetchSpaceId(spaceKey))).build();
        });
    }

    @EventListener
    public void exportFinishedEvent(AsyncExportFinishedEvent event) {
        this.saveIfPresent(() -> {
            ExportScope exportScope;
            try {
                exportScope = ExportScope.getScopeFromPropertyValue(event.getExportScope());
            }
            catch (ExportScope.IllegalExportScopeException e) {
                throw new RuntimeException(e);
            }
            if (exportScope == ExportScope.PAGE) {
                return Optional.empty();
            }
            if (exportScope == ExportScope.SPACE) {
                return Optional.of(AuditEvent.fromI18nKeys((String)AuditCategories.IMPORT_EXPORT, (String)SPACE_EXPORT_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.LOCAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResource(this.auditHelper.fetchSpaceDisplayName(event.getSpaceKey()), this.resourceTypes.space(), this.auditHelper.fetchSpaceId(event.getSpaceKey()))).extraAttribute(AuditAttribute.fromI18nKeys((String)DESCRIPTION_EXTRA_ATTRIBUTE, (String)this.calculateDescription(event)).build()).build());
            }
            if (exportScope == ExportScope.ALL || exportScope == ExportScope.SITE) {
                return Optional.of(AuditEvent.fromI18nKeys((String)AuditCategories.IMPORT_EXPORT, (String)SITE_EXPORT_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).extraAttribute(AuditAttribute.fromI18nKeys((String)DESCRIPTION_EXTRA_ATTRIBUTE, (String)this.calculateDescription(event)).build()).build());
            }
            throw new IllegalArgumentException("Received an exportFinished event without a valid export scope: " + exportScope);
        });
    }

    private String calculateDescription(AsyncExportFinishedEvent event) {
        switch (event.getExportType()) {
            case "TYPE_XML": {
                return this.auditHelper.translate(AuditHelper.buildDescriptionTextKey("export.format.xml"));
            }
            case "TYPE_HTML": {
                return this.auditHelper.translate(AuditHelper.buildDescriptionTextKey("export.format.html"));
            }
            case "TYPE_PDF": {
                return this.auditHelper.translate(AuditHelper.buildDescriptionTextKey("export.format.pdf"));
            }
            case "TYPE_MOINMOIN": {
                return this.auditHelper.translate(AuditHelper.buildDescriptionTextKey("export.format.moinmoin"));
            }
            case "TYPE_ALL_DATA": {
                return this.auditHelper.translate(AuditHelper.buildDescriptionTextKey("export.format.full"));
            }
        }
        return this.auditHelper.translate(AuditHelper.buildDescriptionTextKey("export.format.unknown"));
    }
}

