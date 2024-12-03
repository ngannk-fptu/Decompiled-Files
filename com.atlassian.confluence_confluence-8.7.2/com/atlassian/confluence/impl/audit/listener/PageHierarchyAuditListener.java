/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
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
import com.atlassian.confluence.event.events.content.pagehierarchy.AbstractCopyPageHierarchyEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.AbstractPageHierarchyEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.CopyPageHierarchyFinishEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.CopyPageHierarchyStartEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.DeletePageHierarchyFinishEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.DeletePageHierarchyStartEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractContentAuditListener;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PageHierarchyAuditListener
extends AbstractContentAuditListener {
    public static final String PAGE_HIERARCHY_COPY = AuditHelper.buildSummaryTextKey("page.hierarchy.start.copy");
    public static final String PAGE_HIERARCHY_COPY_FINISH = AuditHelper.buildSummaryTextKey("page.hierarchy.finish.copy");
    public static final String PAGE_HIERARCHY_COPY_ATTRIBUTE_ATTACHMENTS_COPIED = AuditHelper.buildExtraAttribute("page.hierarchy.copy.attachments.copied");
    public static final String PAGE_HIERARCHY_COPY_ATTRIBUTE_LABELS_COPIED = AuditHelper.buildExtraAttribute("page.hierarchy.copy.labels.copied");
    public static final String PAGE_HIERARCHY_COPY_ATTRIBUTE_RESTRICTIONS_COPIED = AuditHelper.buildExtraAttribute("page.hierarchy.copy.restrictions.copied");
    public static final String PAGE_HIERARCHY_COPY_ATTRIBUTE_SOURCE_PAGE = AuditHelper.buildExtraAttribute("page.hierarchy.copy.source.page");
    public static final String PAGE_HIERARCHY_COPY_ATTRIBUTE_SOURCE_SPACE = AuditHelper.buildExtraAttribute("page.hierarchy.copy.source.space");
    public static final String PAGE_HIERARCHY_COPY_ATTRIBUTE_TARGET_PARENT_PAGE = AuditHelper.buildExtraAttribute("page.hierarchy.copy.target.parent.page");
    public static final String PAGE_HIERARCHY_COPY_ATTRIBUTE_TARGET_SPACE = AuditHelper.buildExtraAttribute("page.hierarchy.copy.target.space");
    public static final String PAGE_HIERARCHY_DELETE_START = AuditHelper.buildSummaryTextKey("page.hierarchy.start.delete");
    public static final String PAGE_HIERARCHY_DELETE_FINISH = AuditHelper.buildSummaryTextKey("page.hierarchy.finish.delete");

    public PageHierarchyAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, PageManager pageManager, SpaceManager spaceManager, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, pageManager, spaceManager, auditingContext);
    }

    @EventListener
    public void onDeletePageHierarchyStartEvent(DeletePageHierarchyStartEvent event) {
        this.saveIfPresent(() -> event.getHierarchySize() > 1 ? Optional.of(this.buildPageRecord(event, PAGE_HIERARCHY_DELETE_START, Collections.emptyList())) : Optional.empty());
    }

    @EventListener
    public void onDeletePageHierarchyFinishEvent(DeletePageHierarchyFinishEvent event) {
        this.saveIfPresent(() -> event.getHierarchySize() > 1 ? Optional.of(this.buildPageRecord(event, PAGE_HIERARCHY_DELETE_FINISH, Collections.emptyList())) : Optional.empty());
    }

    @EventListener
    public void onCopyPageHierarchyStartEvent(CopyPageHierarchyStartEvent event) {
        this.save(() -> this.buildPageRecord(event, PAGE_HIERARCHY_COPY, this.buildAttributesForCopyPageHierarchyEvent(event)));
    }

    @EventListener
    public void onCopyPageHierarchyFinishEvent(CopyPageHierarchyFinishEvent event) {
        this.save(() -> this.buildPageRecord(event, PAGE_HIERARCHY_COPY_FINISH, this.buildAttributesForCopyPageHierarchyEvent(event)));
    }

    private List<AuditAttribute> buildAttributesForCopyPageHierarchyEvent(AbstractCopyPageHierarchyEvent event) {
        ArrayList<AuditAttribute> attributes = new ArrayList<AuditAttribute>();
        attributes.add(AuditAttribute.fromI18nKeys((String)PAGE_HIERARCHY_COPY_ATTRIBUTE_ATTACHMENTS_COPIED, (String)this.getTranslatedYesNoString(event.isIncludeAttachments())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)PAGE_HIERARCHY_COPY_ATTRIBUTE_RESTRICTIONS_COPIED, (String)this.getTranslatedYesNoString(event.isIncludeRestrictions())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)PAGE_HIERARCHY_COPY_ATTRIBUTE_LABELS_COPIED, (String)this.getTranslatedYesNoString(event.isIncludeLabels())).build());
        Page sourcePage = event.getPage();
        attributes.add(AuditAttribute.fromI18nKeys((String)PAGE_HIERARCHY_COPY_ATTRIBUTE_SOURCE_PAGE, (String)sourcePage.getDisplayTitle()).build());
        this.getSpaceName(sourcePage).ifPresent(spaceName -> attributes.add(AuditAttribute.fromI18nKeys((String)PAGE_HIERARCHY_COPY_ATTRIBUTE_SOURCE_SPACE, (String)spaceName).build()));
        Page targetParentPage = event.getTargetPage();
        attributes.add(AuditAttribute.fromI18nKeys((String)PAGE_HIERARCHY_COPY_ATTRIBUTE_TARGET_PARENT_PAGE, (String)targetParentPage.getDisplayTitle()).build());
        this.getSpaceName(targetParentPage).ifPresent(spaceName -> attributes.add(AuditAttribute.fromI18nKeys((String)PAGE_HIERARCHY_COPY_ATTRIBUTE_TARGET_SPACE, (String)spaceName).build()));
        return attributes;
    }

    private AuditEvent buildPageRecord(AbstractPageHierarchyEvent event, String action, List<AuditAttribute> attributes) {
        AuditEvent.Builder builder = AuditEvent.fromI18nKeys((String)AuditCategories.PAGES, (String)action, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.LOCAL_CONFIG_AND_ADMINISTRATION);
        Optional.ofNullable(event.getTargetPage().getSpace()).map(space -> this.buildResource(space.getName(), this.resourceTypes.space(), space.getId())).ifPresent(arg_0 -> ((AuditEvent.Builder)builder).affectedObject(arg_0));
        builder.affectedObject(this.buildResource(event.getTargetPage().getDisplayTitle(), this.resourceTypes.page(), event.getTargetPage().getId()));
        builder.extraAttributes(attributes);
        return builder.build();
    }
}

