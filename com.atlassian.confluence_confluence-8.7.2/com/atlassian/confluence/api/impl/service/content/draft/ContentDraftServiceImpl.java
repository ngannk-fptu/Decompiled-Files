/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentDraftService
 *  com.atlassian.confluence.api.service.content.ContentDraftService$ConflictPolicy
 *  com.atlassian.confluence.api.service.content.ContentDraftService$DraftValidator
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.api.impl.service.content.draft;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentDraftService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import org.apache.commons.lang3.StringUtils;

public class ContentDraftServiceImpl
implements ContentDraftService {
    private final ContentDraftService legacyContentDraftServiceImpl;
    private final ContentDraftService sharedContentDraftServiceImpl;
    private final CollaborativeEditingHelper collaborativeEditingHelper;
    private final ContentEntityManager contentEntityManager;

    public ContentDraftServiceImpl(ContentDraftService legacyContentDraftServiceImpl, ContentDraftService sharedContentDraftServiceImpl, CollaborativeEditingHelper collaborativeEditingHelper, ContentEntityManager contentEntityManager) {
        this.legacyContentDraftServiceImpl = legacyContentDraftServiceImpl;
        this.sharedContentDraftServiceImpl = sharedContentDraftServiceImpl;
        this.collaborativeEditingHelper = collaborativeEditingHelper;
        this.contentEntityManager = contentEntityManager;
    }

    public Content publishNewDraft(Content content, Expansion ... expansions) {
        return this.getDelegate(content).publishNewDraft(content, expansions);
    }

    public Content publishEditDraft(Content content, ContentDraftService.ConflictPolicy conflictPolicy) {
        return this.getDelegate(content).publishEditDraft(content, conflictPolicy);
    }

    public void deleteDraft(ContentId contentId) {
        ContentEntityObject ceo = this.contentEntityManager.getById(contentId.asLong());
        if (ceo == null) {
            throw new NotFoundException("No content found with id: " + contentId.toString());
        }
        try {
            this.getDelegateForDeletion(ceo).deleteDraft(contentId);
        }
        catch (IllegalArgumentException e) {
            throw new NotImplementedServiceException("Could not delete a draft of type: " + ceo.getClass());
        }
    }

    public ContentDraftService.DraftValidator validator() {
        return this.legacyContentDraftServiceImpl.validator();
    }

    private ContentDraftService getDelegate(Content content) {
        if (content.getSpace() == null || StringUtils.isBlank((CharSequence)content.getSpace().getKey())) {
            throw new BadRequestException("Space key is required field");
        }
        if (content.getType() == null) {
            throw new BadRequestException("Type is required field");
        }
        if (!ContentType.BUILT_IN.contains(content.getType())) {
            return this.legacyContentDraftServiceImpl;
        }
        return this.getDelegate(content.getSpace().getKey());
    }

    private ContentDraftService getDelegate(String spaceKey) {
        return !StringUtils.isBlank((CharSequence)spaceKey) && this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(spaceKey) ? this.sharedContentDraftServiceImpl : this.legacyContentDraftServiceImpl;
    }

    private ContentDraftService getDelegateForDeletion(ContentEntityObject ceo) {
        ContentType contentType = ContentType.valueOf((String)ceo.getType());
        if (DraftsTransitionHelper.isLegacyDraft(ceo) || DraftsTransitionHelper.isSharedDraft(ceo)) {
            return DraftsTransitionHelper.isLegacyDraft(ceo) ? this.legacyContentDraftServiceImpl : this.sharedContentDraftServiceImpl;
        }
        if (ContentType.BUILT_IN.contains(contentType) && !DraftsTransitionHelper.isLegacyDraft(ceo)) {
            String spaceKey = DraftsTransitionHelper.getSpaceKey(ceo);
            return this.getDelegate(spaceKey);
        }
        return this.legacyContentDraftServiceImpl;
    }
}

