/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.api.impl.service.content.typebinding;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.BaseContentTypeApiSupport;
import com.atlassian.confluence.content.apisupport.ContentCreator;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class AttachmentContentTypeApiSupport
extends BaseContentTypeApiSupport<Attachment> {
    private final ContentEntityManager contentEntityManager;
    private final ContentFactory contentFactory;
    private final AttachmentManagerInternal attachmentManager;
    private final PaginationService paginationService;
    private final PermissionManager permissionManager;
    private final ContentCreator contentCreator;
    private final ThumbnailManager thumbnailManager;

    public AttachmentContentTypeApiSupport(ContentEntityManager contentEntityManager, ContentFactory contentFactory, AttachmentManagerInternal attachmentManager, PaginationService paginationService, PermissionManager permissionManager, ApiSupportProvider apiSupportProvider, ContentCreator contentCreator, ThumbnailManager thumbnailManager) {
        super(apiSupportProvider);
        this.contentEntityManager = contentEntityManager;
        this.contentFactory = contentFactory;
        this.attachmentManager = attachmentManager;
        this.paginationService = paginationService;
        this.permissionManager = permissionManager;
        this.contentCreator = contentCreator;
        this.thumbnailManager = thumbnailManager;
    }

    @Override
    public PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible parent, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        try {
            return this.getAttachments(parent.getContentId(), limitedRequest, (Predicate<? super Attachment>)Predicates.alwaysTrue(), expansions);
        }
        catch (ServiceException e) {
            return PageResponseImpl.empty((boolean)false);
        }
    }

    public PageResponse<Content> getAttachments(ContentId containerId, LimitedRequest limitedRequest, Predicate<? super Attachment> predicate, Expansions contentExpansions) throws ServiceException {
        ContentEntityObject ceo = this.contentEntityManager.getById(containerId.asLong());
        if (ceo == null) {
            throw new NotFoundException("No content found with id: " + containerId);
        }
        if (this.cannotViewAttachments(ceo)) {
            throw new PermissionException("User not permitted to view attachments on content: " + containerId);
        }
        PaginationBatch fetchPage = nextRequest -> this.attachmentManager.getFilteredAttachments(ceo, (LimitedRequest)nextRequest, arg_0 -> ((Predicate)predicate).apply(arg_0));
        return this.paginationService.performPaginationListRequest(limitedRequest, fetchPage, items -> this.contentFactory.buildFrom(items, contentExpansions));
    }

    private boolean cannotViewAttachments(ContentEntityObject ceo) {
        return !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, ceo);
    }

    public Optional<Content> getById(ContentId attachmentId, List<ContentStatus> statuses, Expansions expansions) {
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId.asLong());
        if (attachment == null || this.cannotViewAttachments(attachment.getContainer())) {
            return Optional.empty();
        }
        if (!statuses.isEmpty() && attachment.isDeleted() && !statuses.contains(ContentStatus.TRASHED)) {
            return Optional.empty();
        }
        return Optional.of(this.contentFactory.buildFrom(attachment, expansions));
    }

    @Override
    public ContentType getHandledType() {
        return ContentType.ATTACHMENT;
    }

    @Override
    protected PageResponse<Content> getChildrenForThisType(Attachment content, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return PageResponseImpl.empty((boolean)false);
    }

    @Override
    public boolean supportsChildrenOfType(ContentType otherType) {
        return otherType.equals((Object)ContentType.COMMENT);
    }

    @Override
    public boolean supportsChildrenForParentType(ContentType parentType) {
        return !parentType.equals((Object)ContentType.ATTACHMENT) && !parentType.equals((Object)ContentType.COMMENT);
    }

    @Override
    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<Attachment> attachments, Expansions expansions) {
        return StreamSupport.stream(attachments.spliterator(), false).collect(Collectors.toMap(Attachment::getContentId, attachment -> ModelMapBuilder.newInstance().put((Object)"mediaType", (Object)attachment.getMediaType()).put((Object)"fileSize", (Object)attachment.getFileSize()).put((Object)"comment", (Object)attachment.getVersionComment()).build()));
    }

    @Override
    public List<Link> getLinks(Attachment attachment) {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)new Link(LinkType.DOWNLOAD, attachment.getDownloadPath()));
        if (this.thumbnailManager.isThumbnailable(attachment)) {
            String attachmentUrl = attachment.getDownloadPathWithoutVersion();
            builder.add((Object)new Link(LinkType.THUMBNAIL, ThumbnailInfo.createThumbnailUrlPathFromAttachmentUrl(attachmentUrl)));
        }
        return builder.build();
    }

    @Override
    public Class<Attachment> getEntityClass() {
        return Attachment.class;
    }

    @Override
    public ValidationResult validateCreate(Content newContent) {
        return SimpleValidationResult.VALID;
    }

    @Override
    public ValidationResult validateUpdate(Content updatedContent, Attachment existingEntity) {
        boolean passedVersionIsNotSameAsStored;
        int realCurrentVersion;
        ContentEntityObject existingCeo;
        ContentId attachmentId = updatedContent.getId();
        if (existingEntity == null || attachmentId == null) {
            return SimpleValidationResult.builder().authorized(false).addError("No attachment found with id: " + attachmentId, new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
        }
        SimpleValidationResult.Builder resultBuilder = SimpleValidationResult.builder().authorized(this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, existingEntity));
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId.asLong());
        if (attachment == null) {
            return SimpleValidationResult.builder().authorized(false).addError("No attachment found with id: " + attachmentId, new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
        }
        ContentEntityObject newCeo = existingCeo = attachment.getContainer();
        if (updatedContent.getContainer() instanceof Content) {
            ContentId newContainerId = ((Content)updatedContent.getContainer()).getId();
            if (!existingCeo.getContentId().equals((Object)newContainerId)) {
                newCeo = this.contentEntityManager.getById(newContainerId.asLong());
                if (!this.canRemoveAttachment(attachment) || !this.canCreateAttachments(newCeo)) {
                    return resultBuilder.authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"You do not have the permissions to move attachments")).withExceptionSupplier(ServiceExceptionSupplier.permissionExceptionExceptionSupplier()).build();
                }
            }
        }
        if (newCeo == existingCeo && !this.canCreateAttachments(existingCeo)) {
            return resultBuilder.authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"You do not have permission update attachments on this entity")).build();
        }
        Reference versionRef = updatedContent.getVersionRef();
        if (!versionRef.isExpanded() || !versionRef.exists()) {
            return resultBuilder.addError("Updated attachment must include a Version property" + attachmentId, new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier()).build();
        }
        Version newVersion = (Version)versionRef.get();
        int versionPassedWithUpdateRequest = newVersion.getNumber();
        boolean passedVersionIsNotGreaterByOne = versionPassedWithUpdateRequest - (realCurrentVersion = attachment.getVersion()) != 1;
        boolean bl = passedVersionIsNotSameAsStored = versionPassedWithUpdateRequest != realCurrentVersion;
        if (passedVersionIsNotSameAsStored && passedVersionIsNotGreaterByOne) {
            return resultBuilder.addError("You're trying to edit the wrong version of that Attachment. Latest version is " + realCurrentVersion + attachmentId, new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.conflictExceptionSupplier()).build();
        }
        return resultBuilder.build();
    }

    @Override
    public Attachment update(Content content, Attachment entity) {
        ContentEntityObject existingCeo;
        ContentId attachmentId = content.getId();
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId.asLong());
        Reference versionRef = content.getVersionRef();
        Version newVersion = (Version)versionRef.get();
        attachment.setMinorEdit(newVersion.isMinorEdit());
        attachment.setHidden(newVersion.isHidden());
        String newFilename = content.getTitle();
        if (StringUtils.isNotBlank((CharSequence)newFilename)) {
            attachment.setFileName(newFilename);
        }
        this.contentCreator.setCommonMetadata(content, entity);
        Map metadata = content.getMetadata();
        if (metadata != null && !(metadata instanceof Collapsed)) {
            String newMediaType;
            String oldComment = entity.getVersionComment();
            String newComment = (String)metadata.get("comment");
            if (oldComment == null || newComment != null && !oldComment.equals(newComment)) {
                entity.setVersionComment(newComment);
            }
            if (StringUtils.isNotBlank((CharSequence)(newMediaType = (String)metadata.get("mediaType")))) {
                entity.setMediaType(newMediaType);
            }
        }
        ContentEntityObject newCeo = existingCeo = attachment.getContainer();
        if (content.getContainer() instanceof Content) {
            ContentId newContainerId = ((Content)content.getContainer()).getId();
            if (!existingCeo.getContentId().equals((Object)newContainerId)) {
                newCeo = this.contentEntityManager.getById(newContainerId.asLong());
            }
        }
        this.attachmentManager.moveAttachment(entity, newCeo);
        return this.attachmentManager.getAttachment(entity.getId());
    }

    public boolean canCreateAttachments(ContentEntityObject ceo) {
        return ceo.isLatestVersion() && this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)ceo, Attachment.class);
    }

    public boolean canRemoveAttachment(Attachment attachment) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, attachment);
    }
}

