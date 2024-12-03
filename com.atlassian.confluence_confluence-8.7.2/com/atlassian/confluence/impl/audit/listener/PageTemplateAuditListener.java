/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.template.TemplateRemoveEvent;
import com.atlassian.confluence.event.events.template.TemplateUpdateEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.event.api.EventListener;
import java.util.List;
import java.util.Optional;

public class PageTemplateAuditListener
extends AbstractAuditListener {
    private static final String TEMPLATE_UPDATED_SUMMARY = AuditHelper.buildSummaryTextKey("page.template.updated");
    private static final String TEMPLATE_ADDED_SUMMARY = AuditHelper.buildSummaryTextKey("page.template.added");
    private static final String TEMPLATE_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("page.template.deleted");

    public PageTemplateAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void handleTemplateUpdateEvent(TemplateUpdateEvent event) {
        this.save(() -> {
            List<ChangedValue> changedValues;
            String summary;
            if (event.getOldTemplate() != null) {
                summary = TEMPLATE_UPDATED_SUMMARY;
                changedValues = this.getAuditHandlerService().handle(Optional.of(event.getOldTemplate()), Optional.of(event.getNewTemplate()));
            } else {
                summary = TEMPLATE_ADDED_SUMMARY;
                changedValues = this.getAuditHandlerService().handle(event.getNewTemplate(), AuditAction.ADD);
            }
            AuditEvent.Builder builder = AuditEvent.fromI18nKeys((String)AuditCategories.PAGE_TEMPLATES, (String)summary, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(event.getNewTemplate().getName(), this.resourceTypes.pageTemplate())).changedValues(changedValues);
            Optional.ofNullable(event.getNewTemplate().getSpace()).map(space -> this.buildResource(space.getName(), this.resourceTypes.space(), space.getId())).ifPresent(arg_0 -> ((AuditEvent.Builder)builder).affectedObject(arg_0));
            return builder.build();
        });
    }

    @EventListener
    public void handleTemplateRemoveEvent(TemplateRemoveEvent event) {
        this.save(() -> {
            AuditEvent.Builder builder = AuditEvent.fromI18nKeys((String)AuditCategories.PAGE_TEMPLATES, (String)TEMPLATE_REMOVED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(event.getTemplate().getName(), this.resourceTypes.pageTemplate())).changedValues(this.getAuditHandlerService().handle(event.getTemplate(), AuditAction.REMOVE));
            Optional.ofNullable(event.getTemplate().getSpace()).map(space -> this.buildResource(space.getName(), this.resourceTypes.space(), space.getId())).ifPresent(arg_0 -> ((AuditEvent.Builder)builder).affectedObject(arg_0));
            return builder.build();
        });
    }
}

