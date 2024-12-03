/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.attachment.AttachmentTrashedEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentVersionRemoveEvent;
import com.atlassian.confluence.event.events.internal.attachment.AttachmentCreatedAuditingEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractContentAuditListener;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AttachmentAuditListener
extends AbstractContentAuditListener {
    static final String ATTACHMENT_DOWNLOADED_EVENT_SUMMARY = AuditHelper.buildSummaryTextKey("attachment.downloaded");
    static final String ATTACHMENT_UPLOADED_EVENT_SUMMARY = AuditHelper.buildSummaryTextKey("attachment.uploaded");
    static final String ATTACHMENT_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("attachment.delete");
    static final String ATTACHMENT_VERSION_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("attachment.version.delete");
    static final String ATTACHMENT_TRASHED_SUMMARY = AuditHelper.buildSummaryTextKey("attachment.trashed");

    public AttachmentAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, PageManager pageManager, SpaceManager spaceManager, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, pageManager, spaceManager, auditingContext);
    }

    @EventListener
    public void onAttachmentDownloadedEvent(AttachmentViewEvent event) {
        if (this.isAuditable(event)) {
            for (Attachment attachment : event.getAttachments()) {
                this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.PAGES, (String)ATTACHMENT_DOWNLOADED_EVENT_SUMMARY, (CoverageLevel)CoverageLevel.FULL, (CoverageArea)CoverageArea.END_USER_ACTIVITY).affectedObjects(this.buildAffectedObjects(attachment)).build());
            }
        }
    }

    @EventListener
    public void onAttachmentUploadedEvent(AttachmentCreatedAuditingEvent event) {
        if (this.isAuditable(event)) {
            this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.PAGES, (String)ATTACHMENT_UPLOADED_EVENT_SUMMARY, (CoverageLevel)CoverageLevel.FULL, (CoverageArea)CoverageArea.END_USER_ACTIVITY).affectedObjects(this.buildAffectedObjects(event.getAttachment())).build());
        }
    }

    @EventListener
    public void onAttachmentTrashedEvent(AttachmentTrashedEvent event) {
        this.save(() -> {
            Attachment attachment = event.getAttachment();
            return AuditEvent.builder((AuditType)this.buildAuditType(ATTACHMENT_TRASHED_SUMMARY)).changedValues(this.buildChangedValues(attachment, null)).affectedObjects(this.buildAffectedObjects(attachment)).build();
        });
    }

    @EventListener
    public void onAttachmentRemove(GeneralAttachmentRemoveEvent event) {
        this.save(() -> {
            Attachment attachment = event.getAttachment();
            return AuditEvent.builder((AuditType)this.buildAuditType(ATTACHMENT_DELETE_SUMMARY)).changedValues(this.buildChangedValues(attachment, null)).affectedObjects(this.buildAffectedObjects(attachment)).build();
        });
    }

    @EventListener
    public void onAttachmentVersionRemoved(GeneralAttachmentVersionRemoveEvent event) {
        this.save(() -> {
            Attachment attachment = event.getAttachment();
            return AuditEvent.builder((AuditType)this.buildAuditType(ATTACHMENT_VERSION_DELETE_SUMMARY)).changedValues(this.buildChangedValues(attachment, null)).affectedObjects(this.buildAffectedObjects(attachment)).build();
        });
    }

    private List<AuditResource> buildAffectedObjects(Attachment attachment) {
        ContentEntityObject container;
        ArrayList<AuditResource> auditResources = new ArrayList<AuditResource>();
        Space space = Optional.ofNullable(attachment.getSpace()).orElse(this.getSpace(attachment.getContainer()));
        if (space != null) {
            auditResources.add(this.buildResource(space.getName(), this.resourceTypes.space(), space.getId()));
        }
        if ((container = (ContentEntityObject)Optional.ofNullable(this.getContainerPageOrBlog(attachment)).orElse((AbstractPage)attachment.getContainer())) != null) {
            auditResources.add(this.buildResource(this.getTitle(container), this.getPageOrBlogResourceType(container, this.resourceTypes.page()), container.getId()));
        }
        auditResources.add(this.buildResource(attachment.getFileName(), this.resourceTypes.attachment(), attachment.getId()));
        return auditResources;
    }

    private boolean isAuditable(AttachmentViewEvent event) {
        return event.isDownload();
    }

    private boolean isAuditable(AttachmentCreatedAuditingEvent event) {
        Optional<String> containerType = Optional.ofNullable(event.getAttachment()).map(Attachment::getContainer).map(ContentEntityObject::getType);
        boolean typeHasCustomAuditEvent = containerType.map(type -> type.equals("spacedesc") || type.equals("globaldescription")).orElse(false);
        if (typeHasCustomAuditEvent) {
            return false;
        }
        return event.getSaveContext() == null || event.getSaveContext().getUpdateTrigger() != PageUpdateTrigger.LINK_REFACTORING;
    }

    private AuditType buildAuditType(String summary) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.END_USER_ACTIVITY, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.PAGES, (String)summary).build();
    }

    private List<ChangedValue> buildChangedValues(@Nullable Attachment oldChangedObject, @Nullable Attachment newChangedObject) {
        return this.calculateChangedValues(oldChangedObject, newChangedObject);
    }
}

