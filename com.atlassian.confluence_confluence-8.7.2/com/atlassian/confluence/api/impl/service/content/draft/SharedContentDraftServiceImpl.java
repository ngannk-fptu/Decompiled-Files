/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.validation.MergeValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleMergeValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.content.ContentDraftService
 *  com.atlassian.confluence.api.service.content.ContentDraftService$ConflictPolicy
 *  com.atlassian.confluence.api.service.content.ContentDraftService$DraftErrorCodes
 *  com.atlassian.confluence.api.service.content.ContentDraftService$DraftValidator
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.exceptions.ApiPreconditions
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.GoneException
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.relations.RelationService
 *  com.atlassian.confluence.api.service.relations.RelationService$RelatableFinder
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.api.impl.service.content.draft;

import com.atlassian.confluence.api.impl.service.relation.RelatableResolver;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.validation.MergeValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleMergeValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.content.ContentDraftService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.ApiPreconditions;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.GoneException;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.event.events.analytics.SharedDraftPublishedEvent;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SharedContentDraftServiceImpl
implements ContentDraftService {
    private final ContentService contentService;
    private final ContentEntityManagerInternal contentEntityManager;
    private final ContentPermissionManager contentPermissionManager;
    private final RelatableResolver relatableResolver;
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;
    private final RelationService relationService;
    private final RelationManager relationManager;
    private final AccessModeService accessModeService;
    private static final List<ContentType> supportedDeletionTypes = Arrays.asList(ContentType.PAGE, ContentType.BLOG_POST);

    public SharedContentDraftServiceImpl(ContentService contentService, ContentEntityManagerInternal contentEntityManager, ContentPermissionManager contentPermissionManager, RelatableResolver relatableResolver, PageManager pageManager, EventPublisher eventPublisher, RelationService relationService, RelationManager relationManager, AccessModeService accessModeService) {
        this.contentEntityManager = contentEntityManager;
        this.contentPermissionManager = contentPermissionManager;
        this.contentService = (ContentService)Preconditions.checkNotNull((Object)contentService);
        this.relatableResolver = relatableResolver;
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.relationService = relationService;
        this.relationManager = relationManager;
        this.accessModeService = accessModeService;
    }

    @Deprecated
    public Content publishNewDraft(Content content, Expansion ... expansions) throws ServiceException {
        throw new BadRequestException("Unsupported call to publishNewDraft");
    }

    public Content publishEditDraft(Content updatedContent, ContentDraftService.ConflictPolicy conflictPolicy) {
        Content publishedContent;
        Optional trashedContent;
        boolean newPage;
        Optional duplicateContent;
        this.checkMethodAllowedInReadOnlyMode();
        ApiPreconditions.checkRequestArgs((updatedContent.getId() != null && updatedContent.getId().isSet() ? 1 : 0) != 0, (String)"Could not publish draft. Id is required.");
        ApiPreconditions.checkPermission((!AuthenticatedUserThreadLocal.isAnonymousUser() ? 1 : 0) != 0, (String)"Anonymous is not permitted to publish drafts.");
        String updatedTitle = updatedContent.getTitle();
        updatedTitle = updatedTitle != null ? updatedTitle.trim() : null;
        ContentService.ContentFinder contentFinder = this.contentService.find(Expansions.of((String[])new String[]{"version", "history", "status", "space"}).toArray()).withSpace(new Space[]{updatedContent.getSpace()}).withTitle(updatedTitle).withType(new ContentType[]{updatedContent.getType()}).withStatus(new ContentStatus[]{ContentStatus.CURRENT});
        History contentHistory = updatedContent.getHistory();
        if (ContentType.BLOG_POST.equals((Object)updatedContent.getType()) && contentHistory != null && contentHistory.getCreatedAt() != null) {
            contentFinder = contentFinder.withCreatedDate(contentHistory.getCreatedAt().toLocalDate());
        }
        ApiPreconditions.checkRequestArgs((!(duplicateContent = contentFinder.fetch()).isPresent() || ((Content)duplicateContent.get()).getId().equals((Object)updatedContent.getId()) ? 1 : 0) != 0, (String)"A page with this title already exists");
        Optional existingCurrentContent = this.contentService.find(Expansions.of((String[])new String[]{"version", "status", "space"}).toArray()).withStatus(new ContentStatus[]{ContentStatus.CURRENT}).withId(updatedContent.getId()).fetch();
        boolean bl = newPage = !existingCurrentContent.isPresent();
        if (newPage && (trashedContent = this.contentService.find(Expansions.of((String[])new String[]{"version", "status", "space"}).toArray()).withStatus(new ContentStatus[]{ContentStatus.TRASHED}).withId(updatedContent.getId()).fetch()).isPresent()) {
            throw new GoneException("Could not publish content. Page was already trashed.");
        }
        Optional<Content> option = this.retrieveExistingDraft(updatedContent.getId());
        Content existingDraft = option.orElseThrow(() -> new BadRequestException("Could not find existing draft, perhaps you're trying to publish a personal draft?"));
        Content content = publishedContent = newPage ? this.createContent(updatedContent) : this.updateContent(existingDraft, updatedContent);
        if (publishedContent != null) {
            this.publishSharedDraftPublishedEvent(publishedContent, newPage);
        }
        return publishedContent;
    }

    public void deleteDraft(ContentId contentId) {
        this.checkMethodAllowedInReadOnlyMode();
        this.validator().validateDelete(contentId).throwIfNotSuccessful();
        Object draft = this.contentEntityManager.findDraftFor(contentId.asLong());
        if (!(draft instanceof AbstractPage)) {
            throw new NotImplementedServiceException("Deletion of drafts which aren't an AbstractPage is not supported.");
        }
        ((AbstractPage)draft).remove(this.pageManager);
    }

    private Content createContent(Content updatedContent) {
        Content persistedPage = this.contentService.update(updatedContent);
        ContentEntityObject ceo = this.contentEntityManager.getById(updatedContent.getId());
        if (ceo != null && ceo.hasPermissions("Share")) {
            ceo.getContentPermissionSet("Share").contentPermissionsCopy().forEach(this.contentPermissionManager::removeContentPermission);
        }
        return persistedPage;
    }

    private void checkMethodAllowedInReadOnlyMode() {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            throw new ReadOnlyException("The user is not allowed to publish or delete a draft when the read-only mode is enabled.");
        }
    }

    private void publishSharedDraftPublishedEvent(Content publishedContent, boolean newPage) {
        RelationService.RelatableFinder sources = this.relationService.findSources((Relatable)publishedContent, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR);
        this.eventPublisher.publish((Object)new SharedDraftPublishedEvent(sources.fetchCount(), newPage));
    }

    private Optional<Content> retrieveExistingDraft(ContentId id) {
        return this.contentService.find(Expansions.of((String[])new String[]{"version", "status", "space"}).toArray()).withStatus(new ContentStatus[]{ContentStatus.DRAFT}).withId(id).fetch();
    }

    private Content updateContent(Content existingDraft, Content updatedContent) {
        Content transientDraft = Content.builder((Content)updatedContent).version(existingDraft.getVersion()).status(ContentStatus.DRAFT).parent(null).build();
        Content persistentDraft = this.contentService.update(transientDraft);
        Content persistentPage = this.contentService.update(updatedContent);
        this.relationManager.moveRelationsToContent(this.relatableResolver.resolve((Relatable)persistentDraft), this.relatableResolver.resolve((Relatable)persistentPage), (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR);
        return persistentPage;
    }

    public DraftValidatorImpl validator() {
        return new DraftValidatorImpl();
    }

    private class DraftValidatorImpl
    implements ContentDraftService.DraftValidator {
        private DraftValidatorImpl() {
        }

        public MergeValidationResult validateContentForPageCreate(Content content) {
            return SimpleMergeValidationResult.builder((ValidationResult)SimpleValidationResult.builder().authorized(true).build()).build();
        }

        public MergeValidationResult validateContentForPageUpdate(Content content, ContentDraftService.ConflictPolicy conflictPolicy) {
            return SimpleMergeValidationResult.builder((ValidationResult)SimpleValidationResult.builder().authorized(true).build()).build();
        }

        public ValidationResult validateDelete(ContentId contentId) {
            Content draft = (Content)SharedContentDraftServiceImpl.this.contentService.find(ExpansionsParser.parse((String)"version")).withStatus(new ContentStatus[]{ContentStatus.DRAFT}).withId(contentId).fetchOrNull();
            if (draft == null) {
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.CONTENT_NOT_FOUND_TRANSLATION.apply(contentId.serialise())))).build();
            }
            if (!supportedDeletionTypes.contains(draft.getType())) {
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)String.format("Draft deletion is not supported for type: %s. It must be a PAGE or BLOGPOST.", draft.getType().serialise()))).build();
            }
            if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            Object draftEntity = SharedContentDraftServiceImpl.this.contentEntityManager.findDraftFor(contentId.asLong());
            Optional currentContent = SharedContentDraftServiceImpl.this.contentService.find(new Expansion[0]).withStatus(new ContentStatus[]{ContentStatus.CURRENT}).withId(contentId).fetch();
            if (currentContent.isPresent()) {
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withKeyAndArgs((String)"content.delete.published", (Object[])new Object[0])).build();
            }
            if (!SharedContentDraftServiceImpl.this.contentPermissionManager.hasContentLevelPermission(AuthenticatedUserThreadLocal.get(), "Edit", (ContentEntityObject)draftEntity)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }
    }
}

