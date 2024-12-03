/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.pages;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPermissionUtils;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Internal
public class DefaultDraftsTransitionHelper
implements DraftsTransitionHelper {
    private PageManagerInternal pageManager;
    private DraftManager draftManager;
    private ContentEntityManager contentEntityManager;
    private AttachmentManager attachmentManager;
    private ContentPermissionManager contentPermissionManager;
    private ContentPropertyManager contentPropertyManager;
    private LabelManager labelManager;
    private CollaborativeEditingHelper collaborativeEditingHelper;

    public DefaultDraftsTransitionHelper(PageManagerInternal pageManager, DraftManager draftManager, ContentEntityManager contentEntityManager, AttachmentManager attachmentManager, ContentPermissionManager contentPermissionManager, ContentPropertyManager contentPropertyManager, LabelManager labelManager, CollaborativeEditingHelper collaborativeEditingHelper) {
        this.pageManager = pageManager;
        this.draftManager = draftManager;
        this.contentEntityManager = contentEntityManager;
        this.attachmentManager = attachmentManager;
        this.contentPermissionManager = contentPermissionManager;
        this.contentPropertyManager = contentPropertyManager;
        this.labelManager = labelManager;
        this.collaborativeEditingHelper = collaborativeEditingHelper;
    }

    @Override
    public ContentEntityObject getDraftForPage(AbstractPage abstractPage) {
        if (abstractPage != null) {
            if (this.isSharedDraftsFeatureEnabled(abstractPage.getSpaceKey())) {
                return this.pageManager.createOrFindDraftFor(abstractPage);
            }
            return this.draftManager.findDraft(abstractPage.getId(), AuthenticatedUserThreadLocal.getUsername(), abstractPage.getType(), abstractPage.getSpaceKey());
        }
        Draft draft = AuthenticatedUserThreadLocal.isAnonymousUser() ? this.draftManager.findDraft(0L, null, null, null) : null;
        return draft != null && !this.isSharedDraftsFeatureEnabled(DraftsTransitionHelper.getSpaceKey(draft)) ? draft : null;
    }

    @Override
    public ContentEntityObject createDraft(String contentType, String spaceKey) {
        return this.createDraft(contentType, spaceKey, 0L);
    }

    @Override
    public ContentEntityObject createDraft(String contentType, String spaceKey, long parentPageId) {
        if (this.isSharedDraftsFeatureEnabled(spaceKey)) {
            return this.pageManager.createDraft(contentType, spaceKey, parentPageId);
        }
        return this.draftManager.create(AuthenticatedUserThreadLocal.getUsername(), DraftService.DraftType.getByRepresentation(contentType), spaceKey, parentPageId);
    }

    @Override
    public ContentEntityObject getDraft(long draftId) {
        boolean isSharedContent;
        ContentEntityObject content = this.contentEntityManager.getById(draftId);
        if (content == null) {
            return null;
        }
        String spaceKey = DraftsTransitionHelper.getSpaceKey(content);
        boolean isSharedDraftsEnabled = this.isSharedDraftsFeatureEnabled(spaceKey);
        boolean bl = isSharedContent = !DraftsTransitionHelper.isLegacyDraft(content);
        content = isSharedDraftsEnabled ? (isSharedContent ? this.pageManager.createOrFindDraftFor((AbstractPage)content) : null) : (!isSharedContent ? content : null);
        return content;
    }

    @Override
    public void transitionContentObjects(ContentEntityObject from, ContentEntityObject to) {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>(this.attachmentManager.getLatestVersionsOfAttachmentsWithAnyStatus(from));
        for (Attachment attachment : attachments) {
            this.attachmentManager.moveAttachment(attachment, attachment.getFileName(), to);
        }
        this.contentPropertyManager.transferProperties(from, to);
        to.setContentPropertiesFrom(from);
        for (Label label : from.getLabels()) {
            this.labelManager.addLabel(to, label);
        }
        this.labelManager.removeAllLabels(from);
        this.contentPermissionManager.setContentPermissions((Map<String, Collection<ContentPermission>>)ImmutableMap.of((Object)"View", ContentPermissionUtils.createContentPermissionsFromSet(from.getContentPermissionSet("View")), (Object)"Edit", ContentPermissionUtils.createContentPermissionsFromSet(from.getContentPermissionSet("Edit"))), to);
    }

    @Override
    public String getEditMode(String spaceKey) {
        return this.collaborativeEditingHelper.getEditMode(spaceKey);
    }

    @Override
    public boolean isSharedDraftsFeatureEnabled(String spaceKey) {
        return this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(spaceKey);
    }

    @Override
    @Deprecated
    public boolean isLimitedModeEnabled(String spaceKey) {
        return this.collaborativeEditingHelper.isLimitedModeEnabled(spaceKey);
    }

    @Override
    @Deprecated
    public boolean isFallbackModeEnabled(String spaceKey) {
        return this.collaborativeEditingHelper.isLimitedModeEnabled(spaceKey);
    }
}

