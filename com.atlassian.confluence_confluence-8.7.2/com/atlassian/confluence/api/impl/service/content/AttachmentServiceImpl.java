/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.AttachmentUpload
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.AttachmentService
 *  com.atlassian.confluence.api.service.content.AttachmentService$AttachmentFinder
 *  com.atlassian.confluence.api.service.content.AttachmentService$Validator
 *  com.atlassian.confluence.api.service.content.ContentTrashService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ObjectArrays
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.impl.service.content.finder.FinderPredicates;
import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.impl.service.content.typebinding.AttachmentContentTypeApiSupport;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.AttachmentUpload;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.AttachmentService;
import com.atlassian.confluence.api.service.content.ContentTrashService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.UploadedResource;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentServiceImpl
implements AttachmentService {
    private static final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;
    private final FileUploadManager fileUploadManager;
    private final AttachmentManagerInternal attachmentManager;
    private final ContentFactory contentFactory;
    private final AttachmentContentTypeApiSupport binding;
    private final FinderProxyFactory finderProxyFactory;
    private final ContentTrashService trashService;

    public AttachmentServiceImpl(PermissionManager permissionManager, ContentEntityManager contentEntityManager, FileUploadManager fileUploadManager, AttachmentManagerInternal attachmentManager, ContentFactory contentFactory, AttachmentContentTypeApiSupport binding, FinderProxyFactory finderProxyFactory, ContentTrashService trashService) {
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
        this.fileUploadManager = fileUploadManager;
        this.attachmentManager = attachmentManager;
        this.contentFactory = contentFactory;
        this.binding = binding;
        this.finderProxyFactory = finderProxyFactory;
        this.trashService = trashService;
    }

    public PageResponse<Content> addAttachments(ContentId containerId, Collection<AttachmentUpload> uploads) throws ServiceException {
        return this.addAttachments(containerId, ContentStatus.CURRENT, uploads);
    }

    public PageResponse<Content> addAttachments(ContentId containerId, ContentStatus containerStatus, Collection<AttachmentUpload> uploads) throws ServiceException {
        return this.addAttachments(containerId, containerStatus, uploads, false, Expansions.EMPTY);
    }

    public PageResponse<Content> addAttachments(ContentId containerId, ContentStatus containerStatus, Collection<AttachmentUpload> uploads, boolean allowDuplicated, Expansions expansions) throws ServiceException {
        ContentEntityObject content = this.getContentEntityObjectOrThrow(containerId, containerStatus);
        if (!this.binding.canCreateAttachments(content)) {
            throw new PermissionException("User not permitted to create attachments for content: " + containerId);
        }
        if (!allowDuplicated) {
            List<Attachment> existingAttachments = this.attachmentManager.getLatestVersionsOfAttachments(content);
            this.checkDuplicateFileNames(uploads, existingAttachments);
        }
        ArrayList<AttachmentResource> attachmentResources = new ArrayList<AttachmentResource>();
        for (AttachmentUpload upload : uploads) {
            log.debug("Uploaded file '{}' will be attached to '{}'", (Object)upload.getName(), (Object)content);
            attachmentResources.add(this.makeAttachmentResource(upload));
        }
        this.fileUploadManager.storeResources(attachmentResources, content);
        ArrayList<Content> attachmentsAdded = new ArrayList<Content>();
        for (AttachmentUpload upload : uploads) {
            attachmentsAdded.add(this.makeAttachmentContent(this.attachmentManager.getAttachment(content, upload.getName()), expansions));
        }
        return PageResponseImpl.from(attachmentsAdded, (boolean)false).build();
    }

    private void checkDuplicateFileNames(Collection<AttachmentUpload> uploads, List<Attachment> existingAttachments) throws BadRequestException {
        HashSet addedFileNames = Sets.newHashSet();
        for (AttachmentUpload upload : uploads) {
            String addedFileName = upload.getName();
            if (addedFileNames.contains(addedFileName)) {
                throw new BadRequestException("Cannot add new attachments with duplicate file name: " + addedFileName);
            }
            addedFileNames.add(upload.getName());
        }
        for (Attachment existingAttachment : existingAttachments) {
            String existingFilename = existingAttachment.getFileName();
            if (!addedFileNames.contains(existingFilename)) continue;
            throw new BadRequestException("Cannot add a new attachment with same file name as an existing attachment: " + existingFilename);
        }
    }

    public AttachmentService.AttachmentFinder find(Expansion ... expansions) {
        AttachmentFinderImpl finder = new AttachmentFinderImpl(expansions);
        return this.finderProxyFactory.createProxy(finder, AttachmentService.AttachmentFinder.class);
    }

    public Content update(Content updatedContent) throws ServiceException {
        ValidatorImpl validator = this.validator();
        ((ValidationResult)validator.validateIdOnContent(updatedContent).getOrElse((Object)SimpleValidationResult.VALID)).throwIfNotSuccessful();
        ContentId attachmentId = updatedContent.getId();
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId.asLong());
        validator.validateUpdate(updatedContent, attachment).throwIfNotSuccessful("Could not update attachment with ID : " + attachmentId);
        if (attachment.isDeleted()) {
            return this.trashService.restore(updatedContent);
        }
        ContentEntityObject container = Objects.requireNonNull(attachment.getContainer());
        return this.contentFactory.buildFrom(this.binding.update(updatedContent, attachment), this.defaultAttachmentExpand(!container.isDraft()));
    }

    public Content updateData(ContentId attachmentId, AttachmentUpload upload) throws ServiceException {
        if (attachmentId == null) {
            throw new BadRequestException("Can't update attachment without an id");
        }
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId.asLong());
        if (attachment == null) {
            throw new NotFoundException("Can't find attachment with id: " + attachmentId);
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasCreatePermission((User)user, (Object)attachment.getContainer(), Attachment.class)) {
            throw new PermissionException("You do not have permission to update this Attachment");
        }
        return this.updateAttachmentData(attachment, upload);
    }

    public void delete(Content attachmentContent) throws ServiceException {
        Attachment attachment = this.getAttachment(attachmentContent);
        if (attachment == null) {
            throw new NotFoundException("Can't find attachment with id: " + attachmentContent.getId());
        }
        if (!this.validator().validateDelete(attachment).isAuthorized()) {
            throw new PermissionException("You do not have the permissions to remove the attachment with id: " + attachment.getId());
        }
        this.attachmentManager.trash(attachment);
    }

    public ValidatorImpl validator() {
        return new ValidatorImpl();
    }

    private Attachment getAttachment(Content content) {
        long id = content.getId().asLong();
        return this.attachmentManager.getAttachment(id);
    }

    private Content updateAttachmentData(Attachment attachment, AttachmentUpload upload) {
        ContentEntityObject container = attachment.getContainer();
        String uploadFilename = upload.getName();
        String existingFilename = attachment.getFileName();
        AttachmentResource resource = this.makeAttachmentResource(upload.withName(existingFilename));
        log.debug("Uploaded file '{}' will be attached to '{}'", (Object)uploadFilename, (Object)container);
        this.fileUploadManager.storeResource(resource, container);
        if (!existingFilename.equals(uploadFilename)) {
            this.attachmentManager.moveAttachment(attachment, uploadFilename, container);
        }
        return this.makeAttachmentContent(attachment, Expansions.EMPTY);
    }

    private Content makeAttachmentContent(Attachment attachment, Expansions expansions) {
        if (expansions != null && !expansions.isEmpty()) {
            return this.contentFactory.buildFrom(attachment, expansions);
        }
        ContentEntityObject container = attachment.getContainer();
        if (container instanceof ContentConvertible) {
            Expansions expandContainer = this.defaultAttachmentExpand(true);
            return this.contentFactory.buildFrom(attachment, expandContainer);
        }
        if (container instanceof Draft) {
            Content.ContentBuilder builder = this.contentFactory.builderFrom(attachment, ContentType.valueOf((String)"draft"), this.defaultAttachmentExpand(false));
            Content draftContainer = this.createDraftContainer((Draft)container);
            builder.container((Container)draftContainer);
            return builder.build();
        }
        throw new IllegalArgumentException("Only status draft or current is accepted");
    }

    private Content createDraftContainer(Draft draft) {
        return Content.builder().type(ContentType.valueOf((String)"draft")).id(ContentId.deserialise((String)draft.getIdAsString())).title(draft.getTitle()).status(ContentStatus.DRAFT).build();
    }

    private Expansions defaultAttachmentExpand(boolean includeContainer) {
        Object attachmentExpand = "version,metadata.labels";
        if (includeContainer) {
            attachmentExpand = (String)attachmentExpand + ",container";
        }
        return new Expansions(ExpansionsParser.parse((String)attachmentExpand));
    }

    private Expansions parseAttachmentExpandForFetch(Expansion ... expansions) {
        return new Expansions((Expansion[])ObjectArrays.concat((Object[])expansions, (Object[])ExpansionsParser.parse((String)"metadata.labels"), Expansion.class));
    }

    private AttachmentResource makeAttachmentResource(AttachmentUpload upload) {
        return new UploadedResource(upload.getFile(), upload.getName(), upload.getMediaType(), upload.getComment(), upload.isMinorEdit(), upload.isHidden());
    }

    private ContentEntityObject getContentEntityObjectOrThrow(ContentId containerId, ContentStatus status) throws NotFoundException {
        ContentEntityObject ceo = this.contentEntityManager.getById(containerId.asLong());
        if (ceo == null) {
            throw new NotFoundException("No content found with id: " + containerId);
        }
        ContentStatus foundStatus = ceo.getContentStatusObject();
        if (!foundStatus.equals((Object)status)) {
            throw new NotFoundException(String.format("No content found with id : %s and status %s, there is a content object with status : %s", status, containerId, foundStatus));
        }
        return ceo;
    }

    class ValidatorImpl
    implements AttachmentService.Validator {
        ValidatorImpl() {
        }

        public ValidationResult validateUpdate(Content contentToUpdate) {
            return (ValidationResult)this.validateIdOnContent(contentToUpdate).getOrElse((Object)this.validateUpdate(contentToUpdate, AttachmentServiceImpl.this.attachmentManager.getAttachment(contentToUpdate.getId().asLong())));
        }

        ValidationResult validateUpdate(Content contentToUpdate, Attachment existingAttachment) {
            return (ValidationResult)this.validateIdOnContent(contentToUpdate).getOrElse((Object)AttachmentServiceImpl.this.binding.validateUpdate(contentToUpdate, existingAttachment));
        }

        private Option<ValidationResult> validateIdOnContent(Content content) {
            ContentId attachmentId = content.getId();
            if (attachmentId == null || attachmentId.asLong() == 0L) {
                return Option.some((Object)SimpleValidationResult.builder().addError("Updated attachment must include an id", new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier()).build());
            }
            return Option.none();
        }

        public ValidationResult validateDelete(Content attachmentContent) {
            Attachment attachment = AttachmentServiceImpl.this.getAttachment(attachmentContent);
            return this.validateDelete(attachment);
        }

        public boolean canCreateAttachments(ContentId containerId) throws NotFoundException {
            return this.canCreateAttachments(containerId, ContentStatus.CURRENT);
        }

        private ValidationResult validateDelete(Attachment attachment) {
            return AttachmentServiceImpl.this.binding.canRemoveAttachment(attachment) ? SimpleValidationResult.VALID : SimpleValidationResult.FORBIDDEN;
        }

        public boolean canCreateAttachments(ContentId containerId, ContentStatus status) throws NotFoundException {
            ContentEntityObject ceo = AttachmentServiceImpl.this.getContentEntityObjectOrThrow(containerId, status);
            return AttachmentServiceImpl.this.binding.canCreateAttachments(ceo);
        }
    }

    private class AttachmentFinderImpl
    extends AbstractFinder<Content>
    implements AttachmentService.AttachmentFinder {
        private ContentId containerId;
        private ContentId attachmentId;
        private String filename;
        private String mediaType;

        public AttachmentFinderImpl(Expansion[] expansions) {
            super(expansions);
        }

        public SingleFetcher<Content> withId(ContentId attachmentId) {
            this.attachmentId = attachmentId;
            return this;
        }

        public AttachmentService.AttachmentFinder withContainerId(ContentId parentId) {
            this.containerId = parentId;
            return this;
        }

        public AttachmentService.AttachmentFinder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public AttachmentService.AttachmentFinder withMediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public PageResponse<Content> fetchMany(PageRequest request) {
            Expansions parsedExpansions = AttachmentServiceImpl.this.parseAttachmentExpandForFetch(this.expansions);
            if (this.containerId != null) {
                LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.content((Expansions)parsedExpansions));
                return AttachmentServiceImpl.this.binding.getAttachments(this.containerId, limitedRequest, (com.google.common.base.Predicate<? super Attachment>)((com.google.common.base.Predicate)this.asPredicate()::test), parsedExpansions);
            }
            if (this.attachmentId != null) {
                return PageResponseImpl.fromSingle((Object)AttachmentServiceImpl.this.binding.getById(this.attachmentId, Collections.singletonList(ContentStatus.CURRENT), parsedExpansions).orElse(null), (boolean)false).pageRequest(request).build();
            }
            throw new NotImplementedServiceException("Must specify either an attachmentId or a contentId to fetch with");
        }

        private Predicate<? super ContentEntityObject> asPredicate() {
            ArrayList<Predicate<? super ContentEntityObject>> filterList = new ArrayList<Predicate<? super ContentEntityObject>>();
            filterList.add(Objects::nonNull);
            if (!StringUtils.isBlank((CharSequence)this.filename)) {
                filterList.add(FinderPredicates.createFileNamePredicate(this.filename));
            }
            if (!StringUtils.isBlank((CharSequence)this.mediaType)) {
                filterList.add(FinderPredicates.createMediaTypePredicate(this.mediaType));
            }
            if (this.containerId != null) {
                filterList.add(FinderPredicates.containerPredicate(this.containerId));
            }
            return arg -> filterList.stream().allMatch(p -> p.test(arg));
        }

        public Optional<Content> fetch() {
            PageResponse<Content> attachments = this.fetchMany(SimplePageRequest.ONE);
            if (attachments.size() > 0) {
                return Optional.ofNullable((Content)attachments.getResults().get(0));
            }
            return Optional.empty();
        }
    }
}

