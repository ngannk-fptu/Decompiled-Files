/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.locator.ContentLocator
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.ContentTrashService
 *  com.atlassian.confluence.api.service.content.ContentTrashService$Validator
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.model.validation.CoreValidationResultFactory;
import com.atlassian.confluence.api.impl.service.content.DeleteAttachmentCommandImpl;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.locator.ContentLocator;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.ContentTrashService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.content.service.BlogPostService;
import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import java.time.LocalDate;
import java.util.Objects;

public class ContentTrashServiceImpl
implements ContentTrashService {
    static final ImmutableSet<ContentType> TRASHABLE_CONTENT_TYPES = ImmutableSet.of((Object)ContentType.PAGE, (Object)ContentType.BLOG_POST, (Object)ContentType.ATTACHMENT);
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final PageService pageService;
    private final BlogPostService blogPostService;
    private final CommentService commentService;
    private final Supplier<ContentService> contentServiceSupplier;
    private final SpaceManager spaceManager;
    private final AttachmentManager attachmentManager;

    public ContentTrashServiceImpl(PageManager pageManager, PermissionManager permissionManager, PageService pageService, BlogPostService blogPostService, CommentService commentService, Supplier<ContentService> contentServiceSupplier, SpaceManager spaceManager, AttachmentManager attachmentManager) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.pageService = pageService;
        this.blogPostService = blogPostService;
        this.commentService = commentService;
        this.contentServiceSupplier = contentServiceSupplier;
        this.spaceManager = spaceManager;
        this.attachmentManager = attachmentManager;
    }

    public void trash(Content content) {
        Content existing = this.getCurrentContent(content);
        ServiceCommand command = this.getDeleteCommand(content);
        this.validator().validateTrashExisting(existing, command).throwIfNotSuccessful("Unable to trash content with id: " + content.getId());
        this.execute(command);
    }

    public Content restore(Content content) {
        this.validator().validateRestore(content).throwIfNotSuccessful("Unable to restore content with id: " + content.getId());
        long contentId = content.getId().asLong();
        if (content.getType() == ContentType.ATTACHMENT) {
            Attachment attachment = this.attachmentManager.getAttachment(contentId);
            this.attachmentManager.restore(attachment);
        } else {
            AbstractPage abstractPage = Objects.requireNonNull(this.pageManager.getAbstractPage(contentId));
            this.pageManager.restorePage(abstractPage);
        }
        return (Content)((ContentService)this.contentServiceSupplier.get()).find(new Expansion[0]).withId(content.getId()).fetchOrNull();
    }

    public void purge(Content content) {
        Content existing = this.getContentAnyStatus(content);
        this.validateSameVersion(content, existing);
        this.validator().validatePurgeInternal(existing).throwIfNotSuccessful("Unable to purge content with id: " + content.getId());
        ContentType type = existing.getType();
        if (type.equals((Object)ContentType.PAGE) || type.equals((Object)ContentType.BLOG_POST)) {
            AbstractPage object = (AbstractPage)this.pageManager.getById(content.getId().asLong());
            if (object != null) {
                object.remove(this.pageManager);
            }
        } else if (type.equals((Object)ContentType.ATTACHMENT)) {
            Attachment attachment = this.attachmentManager.getAttachment(content.getId().asLong());
            this.attachmentManager.removeAttachmentFromServer(attachment);
        } else if (type.equals((Object)ContentType.COMMENT)) {
            this.execute(this.commentService.newDeleteCommentCommand(content.getId().asLong()));
        } else {
            throw new NotImplementedServiceException("Purge is not currently implemented for type: " + type);
        }
    }

    public ValidatorImpl validator() {
        return new ValidatorImpl();
    }

    private Space getCoreSpace(Content content) {
        String spaceKey = this.getSpaceKey(content);
        return this.spaceManager.getSpace(spaceKey);
    }

    private String getSpaceKey(Content content) {
        return com.atlassian.confluence.api.model.content.Space.getSpaceKey((Reference)content.getSpaceRef());
    }

    private Content getCurrentContent(Content content) {
        Content found = this.getContentAnyStatus(content);
        this.validateSameVersion(content, found);
        if (!found.getStatus().equals((Object)ContentStatus.CURRENT)) {
            throw new NotFoundException(String.format("Cannot find current content with id '%s'", content.getId().serialise()));
        }
        return found;
    }

    private Content getContentAnyStatus(Content content) {
        ContentId contentId = content.getId();
        Content existing = (Content)((ContentService)this.contentServiceSupplier.get()).find(new Expansion[]{new Expansion("version"), new Expansion("history")}).withAnyStatus().withId(contentId).fetchOrNull();
        if (existing == null) {
            throw new NotFoundException(String.format("Cannot find content with id '%s'", contentId.serialise()));
        }
        return existing;
    }

    private void validateSameVersion(Content content, Content existing) {
        Reference versionRef = content.getVersionRef();
        if (!versionRef.isExpanded() || !versionRef.exists()) {
            return;
        }
        Version version = (Version)versionRef.get();
        int existingVersion = existing.getVersion().getNumber();
        if (existingVersion != version.getNumber()) {
            throw new ConflictException(String.format("Cannot operate on content - expected version is %d but current version is %d", version.getNumber(), existingVersion));
        }
    }

    private ServiceCommand getDeleteCommand(Content content) {
        ServiceCommand command;
        ContentType type = content.getType();
        long id = content.getId().asLong();
        if (type == ContentType.PAGE) {
            command = this.pageService.newDeletePageCommand(this.pageService.getIdPageLocator(id));
        } else if (type == ContentType.BLOG_POST) {
            command = this.blogPostService.newDeleteBlogPostCommand(this.blogPostService.getIdBlogPostLocator(id));
        } else if (type == ContentType.COMMENT) {
            command = this.commentService.newDeleteCommentCommand(id);
        } else if (type == ContentType.ATTACHMENT) {
            Attachment attachment = this.attachmentManager.getAttachment(id);
            command = new DeleteAttachmentCommandImpl(this.attachmentManager, this.permissionManager, attachment);
        } else {
            throw new UnsupportedOperationException("Can only delete Pages, BlogPosts, Comments and Attachments so far");
        }
        return command;
    }

    private void execute(ServiceCommand command) throws PermissionException, BadRequestException {
        try {
            command.execute();
        }
        catch (NotAuthorizedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (NotValidException e) {
            throw new BadRequestException((Throwable)e);
        }
    }

    private class ValidatorImpl
    implements ContentTrashService.Validator {
        private ValidatorImpl() {
        }

        public ValidationResult validateTrash(Content content) {
            ServiceCommand command = ContentTrashServiceImpl.this.getDeleteCommand(content);
            return this.validateTrashExisting(ContentTrashServiceImpl.this.getCurrentContent(content), command);
        }

        public ValidationResult validatePurge(Content content) {
            Content existing = ContentTrashServiceImpl.this.getContentAnyStatus(content);
            ContentTrashServiceImpl.this.validateSameVersion(content, existing);
            return this.validatePurgeInternal(existing);
        }

        private ValidationResult validateTrashExisting(Content content, ServiceCommand command) {
            if (content == null || content.getStatus() == null) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Cannot trash incomplete Content object")).build();
            }
            if (!TRASHABLE_CONTENT_TYPES.contains((Object)content.getType())) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Only pages, blogs and attachments can be trashed. Other content must be purged.")).build();
            }
            return this.validateCommand(command);
        }

        public ValidationResult validateRestore(Content updatedContent) {
            SimpleValidationResult.Builder resultBuilder = new SimpleValidationResult.Builder();
            Content currentContent = ContentTrashServiceImpl.this.getContentAnyStatus(updatedContent);
            Space coreSpace = ContentTrashServiceImpl.this.getCoreSpace(currentContent);
            if (!ContentTrashServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, coreSpace)) {
                return resultBuilder.authorized(false).addMessage((Message)SimpleMessage.withKeyAndArgs((String)"content.trash.restore.notpermitted", (Object[])new Object[]{coreSpace.getKey()})).build();
            }
            resultBuilder.authorized(true);
            if (!ContentStatus.TRASHED.equals((Object)currentContent.getStatus())) {
                return resultBuilder.addMessage((Message)SimpleMessage.withKeyAndArgs((String)"content.trash.restore.status.incorrect", (Object[])new Object[0])).build();
            }
            if (!ContentStatus.CURRENT.equals((Object)updatedContent.getStatus())) {
                return resultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Trashed content cannot be updated, you need to restore it first by setting its status to current.")).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
            }
            Reference newVersionRef = updatedContent.getVersionRef();
            Version currentVersion = currentContent.getVersion();
            if (!newVersionRef.isExpanded() || !newVersionRef.exists() || currentVersion == null) {
                return resultBuilder.addMessage((Message)SimpleMessage.withKeyAndArgs((String)"content.trash.restore.version.missing", (Object[])new Object[0])).build();
            }
            Version newVersion = (Version)newVersionRef.get();
            if (!this.isVersionIncremented(newVersion, currentVersion)) {
                return resultBuilder.addMessage((Message)SimpleMessage.withKeyAndArgs((String)"content.trash.restore.version.incorrect", (Object[])new Object[]{currentContent.getVersion().getNumber()})).build();
            }
            Content byLocator = this.findConflictingContent(currentContent);
            if (byLocator != null) {
                return new SimpleValidationResult.Builder().addMessage((Message)SimpleMessage.withTranslation((String)("Cannot restore content with id " + currentContent.getId() + ": newer Content exists in space " + coreSpace.getKey() + " with title " + currentContent.getTitle() + ". You must either remove that new content, or update the title of the trashed content before restoring it."))).withExceptionSupplier(ServiceExceptionSupplier.conflictExceptionSupplier()).build();
            }
            return SimpleValidationResult.VALID;
        }

        private Content findConflictingContent(Content content) {
            String spaceKey = ContentTrashServiceImpl.this.getSpaceKey(content);
            String title = content.getTitle();
            if (content.getType().equals((Object)ContentType.PAGE)) {
                ContentLocator locator = ContentLocator.builder().forPage().bySpaceKeyAndTitle(spaceKey, title);
                return (Content)((ContentService)ContentTrashServiceImpl.this.contentServiceSupplier.get()).find(new Expansion[0]).withLocator(locator).fetchOrNull();
            }
            if (content.getType().equals((Object)ContentType.BLOG_POST)) {
                LocalDate publishDate = content.getHistory().getCreatedAt().toLocalDate();
                ContentLocator locator = ContentLocator.builder().forBlog().bySpaceKeyTitleAndPostingDay(spaceKey, title, publishDate);
                return (Content)((ContentService)ContentTrashServiceImpl.this.contentServiceSupplier.get()).find(new Expansion[0]).withLocator(locator).fetchOrNull();
            }
            if (content.getType().equals((Object)ContentType.ATTACHMENT)) {
                ContentEntityObject attachmentContainer = ContentTrashServiceImpl.this.attachmentManager.getAttachment(content.getId().asLong()).getContainer();
                Attachment conflictingAttachment = ContentTrashServiceImpl.this.attachmentManager.getAttachment(attachmentContainer, content.getTitle());
                if (conflictingAttachment == null) {
                    return null;
                }
                return (Content)((ContentService)ContentTrashServiceImpl.this.contentServiceSupplier.get()).find(new Expansion[0]).withId(ContentId.of((long)conflictingAttachment.getId())).fetchOrNull();
            }
            return null;
        }

        private ValidationResult validatePurgeInternal(Content content) {
            if (TRASHABLE_CONTENT_TYPES.contains((Object)content.getType())) {
                Space coreSpace = ContentTrashServiceImpl.this.getCoreSpace(content);
                if (!ContentTrashServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, coreSpace)) {
                    return new SimpleValidationResult.Builder().addMessage((Message)SimpleMessage.withTranslation((String)("You do not have permission to purge content from trash in space: " + coreSpace.getKey()))).build();
                }
                if (!content.getStatus().equals((Object)ContentStatus.TRASHED)) {
                    return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Trashable Content must be trashed before it can be purged")).build();
                }
                return SimpleValidationResult.VALID;
            }
            ServiceCommand command = ContentTrashServiceImpl.this.getDeleteCommand(content);
            return this.validateCommand(command);
        }

        private ValidationResult validateCommand(ServiceCommand command) {
            boolean isAuthorized = command.isAuthorized();
            if (isAuthorized) {
                command.isValid();
            }
            return CoreValidationResultFactory.create(isAuthorized, command.getValidationErrors());
        }

        private boolean isVersionIncremented(Version newVersion, Version currentVersion) {
            return newVersion.getNumber() == currentVersion.getNumber() + 1;
        }
    }
}

