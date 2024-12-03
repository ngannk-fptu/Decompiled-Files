/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.content.ContentDraftService
 *  com.atlassian.confluence.api.service.content.ContentDraftService$ConflictPolicy
 *  com.atlassian.confluence.api.service.content.ContentDraftService$DraftValidator
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 */
package com.atlassian.confluence.api.impl.service.content.draft;

import com.atlassian.confluence.api.impl.service.content.draft.LegacyDraftValidator;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.content.ContentDraftService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.ContentDraftManagerInternal;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Objects;

public class LegacyContentDraftServiceImpl
implements ContentDraftService {
    private final DraftManager draftManager;
    private final ContentService contentService;
    private final ContentDraftService.DraftValidator draftValidator;
    private final AccessModeService accessModeService;
    private final CustomContentManager customContentManager;
    private final ContentDraftManagerInternal contentDraftManager;
    public static final String INTERNAL_DRAFT = "draft";

    public LegacyContentDraftServiceImpl(PermissionManager permissionManager, ContentService contentService, DraftManager draftManager, PermissionCheckExemptions permissionCheckExemptions, AccessModeService accessModeService, CustomContentManager customContentManager, ContentDraftManagerInternal contentDraftManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate) {
        this.contentService = Objects.requireNonNull(contentService);
        this.draftManager = Objects.requireNonNull(draftManager);
        this.accessModeService = accessModeService;
        this.customContentManager = Objects.requireNonNull(customContentManager);
        this.contentDraftManager = Objects.requireNonNull(contentDraftManager);
        this.draftValidator = new LegacyDraftValidator(contentService, draftManager, permissionManager, permissionCheckExemptions, contentDraftManager, scopesRequestCacheDelegate);
    }

    public Content publishNewDraft(Content content, Expansion ... expansions) throws ServiceException {
        this.checkMethodAllowedInReadOnlyMode();
        this.validator().validateContentForPageCreate(content).throwIfNotSuccessful();
        Draft draft = this.draftManager.getDraft(content.getId().asLong());
        Content.ContentBuilder transientContent = Content.builder((Content)content).extension(INTERNAL_DRAFT, (Object)draft);
        Content persistedContent = this.contentService.create(transientContent.build(), expansions);
        this.draftManager.removeDraft(draft);
        return persistedContent;
    }

    public Content publishEditDraft(Content content, ContentDraftService.ConflictPolicy conflictPolicy) {
        this.checkMethodAllowedInReadOnlyMode();
        this.validator().validateContentForPageUpdate(content, conflictPolicy).throwIfNotSuccessful();
        Content persistedContent = this.contentService.update(content);
        Draft draft = this.draftManager.findDraft(content.getId().asLong(), AuthenticatedUserThreadLocal.getUsername(), content.getType().getValue(), content.getSpace().getKey());
        this.draftManager.removeDraft(draft);
        return persistedContent;
    }

    public void deleteDraft(ContentId draftId) {
        this.checkMethodAllowedInReadOnlyMode();
        this.validator().validateDelete(draftId).throwIfNotSuccessful();
        Draft draft = this.draftManager.getDraft(draftId.asLong());
        if (draft != null) {
            this.draftManager.removeDraft(draft);
        } else {
            CustomContentEntityObject draftEntity = (CustomContentEntityObject)this.contentDraftManager.findDraftFor(draftId.asLong());
            this.customContentManager.removeContentEntity(draftEntity);
        }
    }

    public ContentDraftService.DraftValidator validator() {
        return this.draftValidator;
    }

    private void checkMethodAllowedInReadOnlyMode() {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            throw new ReadOnlyException("The user is not allowed to publish or delete a draft when the read-only mode is enabled.");
        }
    }
}

