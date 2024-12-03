/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.validation.MergeValidationResult
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleMergeValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleMergeValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentDraftService$ConflictPolicy
 *  com.atlassian.confluence.api.service.content.ContentDraftService$DraftErrorCodes
 *  com.atlassian.confluence.api.service.content.ContentDraftService$DraftValidator
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.api.impl.service.content.draft;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.validation.MergeValidationResult;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleMergeValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentDraftService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.ContentDraftManagerInternal;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public class LegacyDraftValidator
implements ContentDraftService.DraftValidator {
    private final ContentService contentService;
    private final DraftManager draftManager;
    private final PermissionManager permissionManager;
    private final PermissionCheckExemptions permissionCheckExemptions;
    private final ContentDraftManagerInternal contentDraftManager;
    private final ScopesRequestCacheDelegate scopesRequestCacheDelegate;

    public LegacyDraftValidator(ContentService contentService, DraftManager draftManager, PermissionManager permissionManager, PermissionCheckExemptions permissionCheckExemptions, ContentDraftManagerInternal contentDraftManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate) {
        this.contentService = contentService;
        this.draftManager = draftManager;
        this.permissionManager = permissionManager;
        this.permissionCheckExemptions = permissionCheckExemptions;
        this.contentDraftManager = contentDraftManager;
        this.scopesRequestCacheDelegate = scopesRequestCacheDelegate;
    }

    public MergeValidationResult validateContentForPageCreate(Content content) {
        MergeValidationResult idValidation = this.validateIdOnContent(content);
        if (!idValidation.isValid()) {
            return idValidation;
        }
        MergeValidationResult contentReferenceValidation = this.validateContentReference(content);
        if (!contentReferenceValidation.isValid()) {
            return contentReferenceValidation;
        }
        return this.validateExistingDraftForPageCreate(content.getId());
    }

    public MergeValidationResult validateContentForPageUpdate(Content content, ContentDraftService.ConflictPolicy conflictPolicy) {
        MergeValidationResult validate = this.validateIdOnContent(content);
        if (!validate.isValid()) {
            return validate;
        }
        MergeValidationResult validateContent = this.validateContentForPageUpdate(content);
        if (!validateContent.isValid()) {
            return validateContent;
        }
        return this.validateExistingDraftForPageUpdate(content, conflictPolicy);
    }

    public ValidationResult validateDelete(ContentId draftId) {
        Draft draft = this.draftManager.getDraft(draftId.asLong());
        ConfluenceEntityObject draftEntity = null;
        if (draft == null) {
            draftEntity = (CustomContentEntityObject)this.contentDraftManager.findDraftFor(draftId.asLong());
        }
        if (draft == null && draftEntity == null) {
            return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.CONTENT_NOT_FOUND_TRANSLATION.apply(draftId.serialise())))).build();
        }
        UserKey creatorKey = draft != null ? draft.getCreator().getKey() : draftEntity.getCreator().getKey();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.scopesRequestCacheDelegate.hasPermission("REMOVEPAGE", null) || !currentUser.getKey().equals((Object)creatorKey) && !this.permissionCheckExemptions.isExempt(currentUser)) {
            return SimpleValidationResult.FORBIDDEN;
        }
        return SimpleValidationResult.VALID;
    }

    private MergeValidationResult validateExistingDraftForPageCreate(ContentId draftId) {
        Draft draft = this.draftManager.getDraft(draftId.asLong());
        SimpleValidationResult.Builder simpleBuilder = SimpleValidationResult.builder().authorized(true);
        if (draft == null) {
            simpleBuilder.authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.CONTENT_NOT_FOUND_TRANSLATION.apply(draftId.serialise())))).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier());
        } else if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, draft)) {
            simpleBuilder.authorized(false).addMessage((Message)SimpleMessage.builder().key("not.permitted.description").translation("You don't have permission to view or edit this draft").build());
        }
        return SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.build()).build();
    }

    private MergeValidationResult validateIdOnContent(Content content) {
        SimpleValidationResult.Builder simpleBuilder = SimpleValidationResult.builder().authorized(true);
        if (content.getId() == null || !content.getId().isSet()) {
            simpleBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Could not publish content without content id"));
        }
        return SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.build()).build();
    }

    private MergeValidationResult validateContentForPageUpdate(Content content) {
        Content currentContent;
        SimpleValidationResult.Builder simpleBuilder = SimpleValidationResult.builder().authorized(true);
        if (!ContentStatus.CURRENT.equals((Object)content.getStatus())) {
            simpleBuilder.addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.INVALID_CONTENT_STATUS_TRANSLATION.apply(content.getStatus())))).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
        }
        if (content.getSpace() == null || StringUtils.isBlank((CharSequence)content.getSpace().getKey())) {
            simpleBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Space key is required.")).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
        }
        if (content.getType() == null) {
            simpleBuilder.addError("Content type is required.", new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
        }
        if ((currentContent = this.getContentWithSpecificStatus(content, ContentStatus.CURRENT)) == null) {
            Content trashedContent = this.getContentWithSpecificStatus(content, ContentStatus.TRASHED);
            if (trashedContent == null) {
                return SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.CONTENT_NOT_FOUND_TRANSLATION.apply(content.getId().serialise())))).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build()).build();
            }
            return SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.CONTENT_WAS_TRASHED_TRANSLATION.apply(content.getId().serialise())))).withExceptionSupplier(ServiceExceptionSupplier.goneExceptionSupplier()).build()).build();
        }
        SimpleMergeValidationResult.Builder simpleMergeBuilder = SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.build());
        return simpleMergeBuilder.build();
    }

    private MergeValidationResult validateExistingDraftForPageUpdate(Content content, ContentDraftService.ConflictPolicy conflictPolicy) {
        Draft draft = this.draftManager.findDraft(content.getId().asLong(), AuthenticatedUserThreadLocal.getUsername(), content.getType().getValue(), content.getSpace().getKey());
        SimpleValidationResult.Builder simpleBuilder = SimpleValidationResult.builder().authorized(true);
        if (draft == null) {
            simpleBuilder.addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.CONTENT_NOT_FOUND_TRANSLATION.apply(content.getId().serialise())))).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier());
        }
        if (!ContentDraftService.ConflictPolicy.ABORT.equals((Object)conflictPolicy)) {
            return SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.addMessage((Message)SimpleMessage.withTranslation((String)((String)ContentDraftService.DraftErrorCodes.INVALID_POLICY_TRANSLATION.apply(conflictPolicy)))).withExceptionSupplier(ServiceExceptionSupplier.notImplementedSupplier()).build()).build();
        }
        SimpleMergeValidationResult.Builder simpleMergeBuilder = SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.build());
        return simpleMergeBuilder.build();
    }

    private MergeValidationResult validateContentReference(Content content) {
        SimpleValidationResult.Builder simpleBuilder = SimpleValidationResult.builder().authorized(true);
        if (!content.getBody().values().stream().allMatch(b -> b.getContentRef().exists())) {
            simpleBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Reference to content in the content body needs to be set for publishing draft")).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
        }
        SimpleMergeValidationResult.Builder simpleMergeBuilder = SimpleMergeValidationResult.builder((ValidationResult)simpleBuilder.build());
        return simpleMergeBuilder.build();
    }

    private Content getContentWithSpecificStatus(Content content, ContentStatus status) {
        return (Content)this.contentService.find(new Expansion[0]).withStatus(new ContentStatus[]{status}).withId(content.getId()).fetchOrNull();
    }
}

