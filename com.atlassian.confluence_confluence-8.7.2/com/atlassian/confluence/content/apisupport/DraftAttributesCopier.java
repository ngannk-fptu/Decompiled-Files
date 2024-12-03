/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPermissionUtils;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.security.ContentPermission;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class DraftAttributesCopier {
    private final AttachmentManager attachmentManager;
    private final LabelManager labelManager;
    private final ContentPermissionManager contentPermissionManager;
    private final ContentPropertyManager contentPropertyManager;
    private final ContentPropertyService contentPropertyService;

    public DraftAttributesCopier(AttachmentManager attachmentManager, LabelManager labelManager, ContentPermissionManager contentPermissionManager, ContentPropertyManager contentPropertyManager, ContentPropertyService contentPropertyService) {
        this.attachmentManager = attachmentManager;
        this.labelManager = labelManager;
        this.contentPermissionManager = contentPermissionManager;
        this.contentPropertyManager = contentPropertyManager;
        this.contentPropertyService = contentPropertyService;
    }

    public <T extends ContentEntityObject> T copyDraftAttributes(T entity, Draft draft) {
        if (draft != null) {
            ArrayList<Attachment> attachments = new ArrayList<Attachment>(this.attachmentManager.getLatestVersionsOfAttachmentsWithAnyStatus(draft));
            for (Attachment attachment : attachments) {
                this.attachmentManager.moveAttachment(attachment, attachment.getFileName(), entity);
            }
            this.contentPropertyManager.transferProperties(draft, entity);
            ((AbstractPage)entity).setContentPropertiesFromDraft(draft);
            ContentSelector draftSelector = draft.getSelector();
            if (draftSelector.getId().asLong() == 0L) {
                draftSelector = ContentSelector.builder().id(ContentId.of((long)draft.getId())).status(draft.getContentStatusObject()).version(draft.getVersion()).build();
            }
            this.contentPropertyService.copyAllJsonContentProperties(draftSelector, entity.getSelector());
            this.contentPermissionManager.setContentPermissions((Map<String, Collection<ContentPermission>>)ImmutableMap.of((Object)"View", ContentPermissionUtils.createContentPermissionsFromSet(draft.getContentPermissionSet("View")), (Object)"Edit", ContentPermissionUtils.createContentPermissionsFromSet(draft.getContentPermissionSet("Edit"))), entity);
            for (Label label : draft.getLabels()) {
                this.labelManager.addLabel(entity, label);
            }
            this.labelManager.removeAllLabels(draft);
        }
        return entity;
    }
}

