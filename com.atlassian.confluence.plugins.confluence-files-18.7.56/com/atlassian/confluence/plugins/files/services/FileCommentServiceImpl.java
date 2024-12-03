/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.UnknownUser
 *  com.atlassian.confluence.api.service.content.ChildContentService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.Contained
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.files.services;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.UnknownUser;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.plugins.files.api.CommentAnchor;
import com.atlassian.confluence.plugins.files.api.CommentAnchorPin;
import com.atlassian.confluence.plugins.files.api.FileComment;
import com.atlassian.confluence.plugins.files.api.services.FileCommentService;
import com.atlassian.confluence.plugins.files.entities.FileCommentInput;
import com.atlassian.confluence.plugins.files.event.FileCommentCreateEvent;
import com.atlassian.confluence.plugins.files.event.FileCommentDeleteEvent;
import com.atlassian.confluence.plugins.files.event.FileCommentUpdateEvent;
import com.atlassian.confluence.plugins.files.manager.FilePermissionHelper;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@ExportAsService(value={FileCommentService.class})
@Component
public class FileCommentServiceImpl
implements FileCommentService {
    private final ContentService contentService;
    private final ChildContentService childContentService;
    private final ContentEntityManager contentEntityManager;
    private final TransactionTemplate transactionTemplate;
    private final PersonService personService;
    private final CommentManager commentManager;
    private final EventPublisher eventPublisher;
    private final FilePermissionHelper filePermissionHelper;
    private final Validator validator;
    private final Builder builder = new Builder();
    public static final String PROPERTY_COMMENT_ANCHOR = "ANCHOR";
    public static final String PROPERTY_COMMENT_STATUS = "status";
    public static final String PROPERTY_COMMENT_RESOLVED_AUTHOR = "status-lastmodifier";
    public static final String PROPERTY_COMMENT_LAST_MODIFIED = "status-lastmoddate";
    public static final String RESOLVED = "resolved";
    public static final String UNRESOLVED = "open";
    private static final Expansion[] FILE_COMMENT_EXPANSION_FOR_VIEW_AND_EDITOR = ExpansionsParser.parse((String)("version,history,body." + ContentRepresentation.VIEW + ",body." + ContentRepresentation.EDITOR));

    @Autowired
    public FileCommentServiceImpl(@ComponentImport ContentService contentService, @ComponentImport ChildContentService childContentService, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport PermissionManager permissionManager, @ComponentImport(value="contentEntityManager") @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport PersonService personService, @ComponentImport CommentManager commentManager, @ComponentImport EventPublisher eventPublisher, FilePermissionHelper filePermissionHelper) {
        this.contentService = contentService;
        this.childContentService = childContentService;
        this.transactionTemplate = transactionTemplate;
        this.contentEntityManager = contentEntityManager;
        this.personService = personService;
        this.commentManager = commentManager;
        this.validator = new Validator(permissionManager);
        this.eventPublisher = eventPublisher;
        this.filePermissionHelper = filePermissionHelper;
    }

    @Override
    @Nonnull
    public FileComment createComment(long attachmentId, int attachmentVersion, @Nonnull FileCommentInput commentInput) {
        Attachment attachment = this.getContentEntityObjectOrThrow(attachmentId, attachmentVersion, Attachment.class);
        if (commentInput.getParentId() > 0L) {
            Comment parentComment = this.getContentEntityObjectOrThrow(commentInput.getParentId(), Comment.class);
            this.validator.checkIsFileComment(attachment, parentComment);
            this.validator.checkCanCreateReplyOn(parentComment);
        }
        if (commentInput.getAnchor() != null) {
            this.validator.checkValidAnchor(commentInput.getAnchor());
        }
        this.validator.checkNotEmpty(commentInput.getCommentBody(), "Comment body");
        Content comment = this.builder.buildCommentFromInput(attachment.getId(), -1, commentInput);
        Content createdComment = (Content)this.transactionTemplate.execute(() -> {
            Content createdComment1 = this.contentService.create(comment);
            CommentAnchor anchor = commentInput.getAnchor();
            Comment commentCeo = this.getContentEntityObjectOrThrow(createdComment1.getId().asLong(), Comment.class);
            ContentProperties properties = commentCeo.getProperties();
            if (commentInput.getParentId() == 0L) {
                properties.setStringProperty(PROPERTY_COMMENT_STATUS, Boolean.TRUE.equals(commentInput.isResolved()) ? RESOLVED : UNRESOLVED);
                if (anchor != null) {
                    properties.setStringProperty(PROPERTY_COMMENT_ANCHOR, this.builder.serializeAnchorOrThrow(anchor));
                }
            }
            return createdComment1;
        });
        Option<FileComment> result = this.expandFileComment(createdComment);
        if (result.isEmpty()) {
            throw new NotFoundException("There was some problem creating the comment");
        }
        FileComment fileComment = (FileComment)result.get();
        this.eventPublisher.publish((Object)new FileCommentCreateEvent(this, attachment, fileComment, AuthenticatedUserThreadLocal.get()));
        return fileComment;
    }

    @Override
    @Nonnull
    public FileComment createComment(long attachmentId, @Nonnull FileCommentInput commentInput) {
        return this.createComment(attachmentId, 0, commentInput);
    }

    @Override
    @Nonnull
    public PageResponse<FileComment> getComments(long attachmentId, int attachmentVersion, @Nonnull PageRequest pageRequest) {
        this.getContentEntityObjectOrThrow(attachmentId, attachmentVersion, Attachment.class);
        PageResponse<Content> response = this.getChildComments(attachmentId, attachmentVersion, pageRequest, FILE_COMMENT_EXPANSION_FOR_VIEW_AND_EDITOR);
        List<FileComment> result = this.builder.buildFileCommentsFromContents(response.getResults());
        return PageResponseImpl.from(result, (boolean)response.hasMore()).build();
    }

    @Override
    @Nonnull
    public PageResponse<FileComment> getComments(long attachmentId, @Nonnull PageRequest pageRequest) {
        return this.getComments(attachmentId, 0, pageRequest);
    }

    @Override
    public void deleteComment(long attachmentId, int attachmentVersion, long commentId) {
        Attachment attachment = this.getContentEntityObjectOrThrow(attachmentId, attachmentVersion, Attachment.class);
        Comment commentCeo = this.getContentEntityObjectOrThrow(commentId, Comment.class);
        this.validator.checkIsFileComment(attachment, commentCeo);
        this.validator.checkCanDeleteComment(commentCeo);
        Content comment = Content.builder((ContentType)ContentType.COMMENT, (long)commentId).build();
        FileComment fileComment = (FileComment)this.expandFileComment(comment).get();
        this.transactionTemplate.execute(() -> {
            PageResponse<Content> childComments;
            int start = 0;
            int limit = 100;
            do {
                childComments = this.getChildComments(commentId, 0, (PageRequest)new SimplePageRequest(start, limit), new Expansion[0]);
                childComments.getResults().forEach(arg_0 -> ((ContentService)this.contentService).delete(arg_0));
                start += limit;
            } while (childComments.hasMore());
            this.contentService.delete(comment);
            return null;
        });
        this.eventPublisher.publish((Object)new FileCommentDeleteEvent(this, attachment, fileComment, AuthenticatedUserThreadLocal.get()));
    }

    @Override
    public void deleteComment(long attachmentId, long commentId) {
        this.deleteComment(attachmentId, 0, commentId);
    }

    @Override
    @Nonnull
    public FileComment updateComment(long attachmentId, int attachmentVersion, long commentId, @Nonnull FileCommentInput commentInput) {
        ContentProperties properties;
        Content comment;
        Attachment attachment = this.getContentEntityObjectOrThrow(attachmentId, attachmentVersion, Attachment.class);
        Comment commentCeo = this.getContentEntityObjectOrThrow(commentId, Comment.class);
        this.validator.checkIsFileComment(attachment, commentCeo);
        Content updatedComment = comment = this.builder.buildCommentFromInput(attachment.getId(), commentId, commentCeo.getVersion() + 1, commentInput);
        Boolean resolved = commentInput.isResolved();
        boolean modifiedProperties = false;
        if (Boolean.TRUE.equals(resolved)) {
            this.validator.checkCanResolveComment(commentCeo);
            properties = commentCeo.getProperties();
            properties.setStringProperty(PROPERTY_COMMENT_STATUS, RESOLVED);
            properties.setStringProperty(PROPERTY_COMMENT_RESOLVED_AUTHOR, AuthenticatedUserThreadLocal.get().getKey().getStringValue());
            properties.setLongProperty(PROPERTY_COMMENT_LAST_MODIFIED, commentCeo.getLastModificationDate().getTime());
            modifiedProperties = true;
        } else if (Boolean.FALSE.equals(resolved)) {
            this.validator.checkCanReopenComment(commentCeo);
            properties = commentCeo.getProperties();
            properties.setStringProperty(PROPERTY_COMMENT_STATUS, UNRESOLVED);
            properties.setStringProperty(PROPERTY_COMMENT_RESOLVED_AUTHOR, AuthenticatedUserThreadLocal.get().getKey().getStringValue());
            properties.setLongProperty(PROPERTY_COMMENT_LAST_MODIFIED, commentCeo.getLastModificationDate().getTime());
            modifiedProperties = true;
        }
        if (commentInput.getAnchor() != null) {
            CommentAnchor anchor = commentInput.getAnchor();
            this.validator.checkValidAnchor(anchor);
            this.validator.checkCanUpdateCommentAnchor(commentCeo);
            commentCeo.getProperties().setStringProperty(PROPERTY_COMMENT_ANCHOR, this.builder.serializeAnchorOrThrow(anchor));
            modifiedProperties = true;
        }
        if (!Strings.isNullOrEmpty((String)commentInput.getCommentBody())) {
            this.validator.checkCanUpdateComment(commentCeo);
            updatedComment = this.contentService.update(comment);
        } else if (modifiedProperties) {
            this.commentManager.saveContentEntity((ContentEntityObject)commentCeo, DefaultSaveContext.REFACTORING);
        }
        Option<FileComment> result = this.expandFileComment(updatedComment);
        if (result.isEmpty()) {
            throw new NotFoundException("There was some problem creating the comment");
        }
        FileComment fileComment = (FileComment)result.get();
        this.eventPublisher.publish((Object)new FileCommentUpdateEvent(this, attachment, commentInput, fileComment, AuthenticatedUserThreadLocal.get()));
        return fileComment;
    }

    @Override
    @Nonnull
    public FileComment updateComment(long attachmentId, long commentId, @Nonnull FileCommentInput commentInput) {
        return this.updateComment(attachmentId, 0, commentId, commentInput);
    }

    @Override
    @Nonnull
    public FileComment getCommentById(long attachmentId, int attachmentVersion, long commentId) {
        Attachment attachment = this.getContentEntityObjectOrThrow(attachmentId, attachmentVersion, Attachment.class);
        Comment commentCeo = this.getContentEntityObjectOrThrow(commentId, Comment.class);
        this.validator.checkIsFileComment(attachment, commentCeo);
        Option result = this.contentService.find(FILE_COMMENT_EXPANSION_FOR_VIEW_AND_EDITOR).withId(ContentId.deserialise((String)String.valueOf(commentId))).fetchOne();
        if (result.isEmpty()) {
            throw new NotFoundException(String.format("Not found comment with id: %d", commentId));
        }
        PageResponse<Content> childComments = this.getChildComments(commentId, 0, (PageRequest)new SimplePageRequest(0, 100), FILE_COMMENT_EXPANSION_FOR_VIEW_AND_EDITOR);
        HashMap<ContentType, PageResponse<Content>> childMap = new HashMap<ContentType, PageResponse<Content>>();
        childMap.put(ContentType.COMMENT, childComments);
        Content comment = Content.builder((Content)((Content)result.get())).children(childMap).build();
        return this.builder.buildFileCommentFromContent(comment);
    }

    @Override
    @Nonnull
    public FileComment getCommentById(long attachmentId, long commentId) {
        return this.getCommentById(attachmentId, 0, commentId);
    }

    @Nonnull
    private PageResponse<Content> getChildComments(long parentId, int parentVersion, @Nonnull PageRequest pageRequest, Expansion ... expansions) {
        return this.childContentService.findContent(ContentId.deserialise((String)String.valueOf(parentId)), expansions).withParentVersion(parentVersion).fetchMany(ContentType.COMMENT, pageRequest);
    }

    @Nonnull
    private Option<FileComment> expandFileComment(Content collapsedComment) {
        Option result = this.contentService.find(FILE_COMMENT_EXPANSION_FOR_VIEW_AND_EDITOR).withId(collapsedComment.getId()).fetchOne();
        if (result.isEmpty()) {
            return Option.none();
        }
        Content comment = (Content)result.get();
        return Option.some((Object)this.builder.buildFileCommentFromContent(comment));
    }

    @Nonnull
    private <T extends ContentEntityObject> T getContentEntityObjectOrThrow(long contentId, Class<T> clazz) {
        return this.getContentEntityObjectOrThrow(contentId, 0, clazz);
    }

    @Nonnull
    private <T extends ContentEntityObject> T getContentEntityObjectOrThrow(long contentId, int version, Class<T> clazz) {
        ContentEntityObject ceo = this.contentEntityManager.getById(contentId);
        if (ceo != null && version > 0) {
            ceo = this.contentEntityManager.getOtherVersion(ceo, version);
        }
        if (ceo == null) {
            throw new NotFoundException(String.format("No content found with id: %d", contentId));
        }
        try {
            return (T)((ContentEntityObject)clazz.cast(ceo));
        }
        catch (ClassCastException ignored) {
            throw new IllegalArgumentException("The specified ID doesn't point to the right object");
        }
    }

    private class Builder {
        private final ObjectMapper mapper = new ObjectMapper();

        private Builder() {
        }

        private Content buildCommentFromInput(long attachmentId, int version, FileCommentInput commentInput) {
            return this.buildCommentFromInput(attachmentId, 0L, version, commentInput);
        }

        private Content buildCommentFromInput(long attachmentId, long commentId, int version, FileCommentInput commentInput) {
            Content.ContentBuilder builder = Content.builder((ContentType)ContentType.COMMENT);
            if (commentId > 0L) {
                builder.id(ContentId.of((ContentType)ContentType.COMMENT, (long)commentId));
            }
            if (commentInput.getParentId() > 0L) {
                builder.parent(Content.builder((ContentType)ContentType.COMMENT, (long)commentInput.getParentId()).build());
            }
            if (commentInput.getCommentBody() != null) {
                builder.body(commentInput.getCommentBody(), ContentRepresentation.EDITOR);
            }
            if (version > 0) {
                builder.version(Version.builder().number(version).minorEdit(false).build());
            }
            if (attachmentId > 0L) {
                builder.container((Container)Content.builder().id(ContentId.of((ContentType)ContentType.ATTACHMENT, (long)attachmentId)).build());
            }
            return builder.build();
        }

        private FileComment buildFileCommentFromContent(Content comment) {
            Comment commentCeo = FileCommentServiceImpl.this.getContentEntityObjectOrThrow(comment.getId().asLong(), Comment.class);
            ContentProperties properties = commentCeo.getProperties();
            String anchorString = properties.getStringProperty(FileCommentServiceImpl.PROPERTY_COMMENT_ANCHOR);
            CommentAnchor anchor = anchorString == null ? null : this.deserializeAnchorOrThrow(anchorString);
            boolean resolved = FileCommentServiceImpl.RESOLVED.equals(properties.getStringProperty(FileCommentServiceImpl.PROPERTY_COMMENT_STATUS));
            String strUserKey = properties.getStringProperty(FileCommentServiceImpl.PROPERTY_COMMENT_RESOLVED_AUTHOR);
            Person person = this.getPersonFromUserKey(strUserKey);
            List<FileComment> replies = this.buildRepliesFromComment(comment);
            boolean hasEditPermission = FileCommentServiceImpl.this.filePermissionHelper.hasCommentEditPermission(commentCeo);
            boolean hasDeletePermission = FileCommentServiceImpl.this.filePermissionHelper.hasCommentDeletePermission(commentCeo);
            boolean hasCreatePermission = FileCommentServiceImpl.this.filePermissionHelper.hasCreateAnnotationPermission((Contained)commentCeo);
            return new FileComment(comment, anchor, resolved, person, replies, hasEditPermission, hasDeletePermission, hasCreatePermission, hasCreatePermission);
        }

        private List<FileComment> buildRepliesFromComment(Content comment) {
            Map children = comment.getChildren();
            if (children.isEmpty() || children.get(ContentType.COMMENT) == null || ((PageResponse)children.get(ContentType.COMMENT)).size() == 0) {
                return Collections.emptyList();
            }
            List<FileComment> replies = ((PageResponse)children.get(ContentType.COMMENT)).getResults().stream().map(child -> {
                Comment commentCeo = FileCommentServiceImpl.this.getContentEntityObjectOrThrow(child.getId().asLong(), Comment.class);
                boolean hasEditPermission = FileCommentServiceImpl.this.filePermissionHelper.hasCommentEditPermission(commentCeo);
                boolean hasDeletePermission = FileCommentServiceImpl.this.filePermissionHelper.hasCommentDeletePermission(commentCeo);
                boolean hasCreatePermission = FileCommentServiceImpl.this.filePermissionHelper.hasCreateAnnotationPermission((Contained)commentCeo);
                return new FileComment((Content)child, null, false, (Person)new UnknownUser(null, "unknown", null), Collections.emptyList(), hasEditPermission, hasDeletePermission, hasCreatePermission, hasCreatePermission);
            }).collect(Collectors.toList());
            return replies;
        }

        private Person getPersonFromUserKey(String strUserKey) {
            if (strUserKey == null || !FileCommentServiceImpl.this.validator.canViewUserProfile()) {
                return new UnknownUser(null, "unknown", strUserKey);
            }
            Option personOpt = FileCommentServiceImpl.this.personService.find(new Expansion[0]).withUserKey(new UserKey(strUserKey)).fetchOne();
            return personOpt.isEmpty() ? new UnknownUser(null, "unknown", strUserKey) : (Person)personOpt.get();
        }

        private List<FileComment> buildFileCommentsFromContents(List<Content> comments) {
            return comments.stream().map(this::buildFileCommentFromContent).collect(Collectors.toList());
        }

        private CommentAnchor deserializeAnchorOrThrow(String anchorString) throws InternalServerException {
            try {
                return (CommentAnchor)this.mapper.readValue(anchorString, CommentAnchor.class);
            }
            catch (IOException exp) {
                throw new InternalServerException(String.format("Cannot parse comment anchor from String: %s", anchorString), (Throwable)exp);
            }
        }

        private String serializeAnchorOrThrow(CommentAnchor anchor) throws InternalServerException {
            try {
                return this.mapper.writeValueAsString((Object)anchor);
            }
            catch (IOException exception) {
                throw new InternalServerException(String.format("Cannot serialize comment anchor to String: %s", anchor), (Throwable)exception);
            }
        }
    }

    private static class Validator {
        private final PermissionManager permissionManager;

        private Validator(PermissionManager permissionManager) {
            this.permissionManager = permissionManager;
        }

        private void checkIsFileComment(Attachment attachment, Comment comment) {
            ContentEntityObject container = comment.getContainer();
            if (container.getId() != attachment.getId()) {
                throw new BadRequestException(String.format("Comment: %d not belonging to attachment: %d", comment.getId(), attachment.getId()));
            }
        }

        private void checkNotEmpty(String value, String name) {
            if (Strings.isNullOrEmpty((String)value)) {
                throw new BadRequestException(String.format("%s must not be empty", name));
            }
        }

        private void checkGreaterThanOrEquals(int value, int limit, String name) {
            if (value < limit) {
                throw new BadRequestException(String.format("%s must be equal or greater than %d", name, limit));
            }
        }

        private void checkRange(double value, double min, double max, String name) {
            if (value < min || value >= max) {
                throw new BadRequestException(String.format("%s name must be inside [%f, %f)", name, min, max));
            }
        }

        private void checkValidAnchor(CommentAnchor anchor) {
            if (anchor instanceof CommentAnchorPin) {
                CommentAnchorPin pin = (CommentAnchorPin)anchor;
                this.checkGreaterThanOrEquals(pin.getPage(), 1, "Page of annotation");
                this.checkRange(pin.getX(), 0.0, 1.0, "Coordinate x of annotation");
                this.checkRange(pin.getY(), 0.0, 1.0, "Coordinate y of annotation");
            }
        }

        private void checkCanCreateReplyOn(Comment parent) {
            if (this.isCommentResolved(parent)) {
                throw new BadRequestException(String.format("Cannot reply on a resolved comment: %d", parent.getId()));
            }
            if (parent.getParent() != null) {
                throw new BadRequestException(String.format("Cannot reply on a 2nd level comment: %d", parent.getId()));
            }
        }

        private boolean isCommentResolved(Comment comment) {
            ContentProperties properties = comment.getProperties();
            return FileCommentServiceImpl.RESOLVED.equals(properties.getStringProperty(FileCommentServiceImpl.PROPERTY_COMMENT_STATUS));
        }

        private void checkCanDeleteComment(Comment comment) {
            if (comment.getParent() == null && this.isCommentResolved(comment)) {
                throw new BadRequestException(String.format("Cannot delete a resolved comment: %d", comment.getId()));
            }
            if (comment.getParent() != null && this.isCommentResolved(comment.getParent())) {
                throw new BadRequestException(String.format("Cannot delete the reply of a resolved comment: %d", comment.getParent().getId()));
            }
        }

        private boolean hasResolveCommentPermission(Comment comment) {
            ContentEntityObject fileCeo = comment.getContainer();
            return this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)fileCeo, Comment.class);
        }

        private void checkCanResolveComment(Comment comment) {
            if (!this.hasResolveCommentPermission(comment)) {
                throw new PermissionException(String.format("User not permitted to resolve comment: %d", comment.getId()));
            }
            if (comment.getParent() != null) {
                throw new BadRequestException("Cannot resolve child comment");
            }
            if (this.isCommentResolved(comment)) {
                throw new ConflictException("Comment was already resolved");
            }
        }

        private boolean hasReopenCommentPermission(Comment comment) {
            ContentEntityObject fileCeo = comment.getContainer();
            return this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)fileCeo, Comment.class);
        }

        private void checkCanReopenComment(Comment comment) {
            if (!this.hasReopenCommentPermission(comment)) {
                throw new PermissionException(String.format("User not permitted to reopen comment: %d", comment.getId()));
            }
            if (comment.getParent() != null) {
                throw new BadRequestException(String.format("Cannot reopen child comment: %d", comment.getId()));
            }
            if (!this.isCommentResolved(comment)) {
                throw new ConflictException(String.format("Comment was not resolved before: %d", comment.getId()));
            }
        }

        private void checkCanUpdateComment(Comment comment) {
            if (this.isCommentResolved(comment)) {
                throw new BadRequestException(String.format("Cannot update a resolved comment: %d", comment.getId()));
            }
        }

        private void checkCanUpdateCommentAnchor(Comment comment) {
            this.checkCanUpdateComment(comment);
            if (comment.getParent() != null) {
                throw new BadRequestException("Cannot update anchor of child comment");
            }
        }

        public boolean canViewUserProfile() {
            return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, User.class);
        }
    }
}

