/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.content.service.comment;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.FieldValidationError;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.NewCommentDeduplicator;
import com.atlassian.confluence.security.PermissionManager;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class CreateCommentCommandImpl
extends AbstractServiceCommand
implements CreateCommentCommand {
    private static final Set<ContentStatus> RESTRICTED_STATUSES = ImmutableSet.of((Object)ContentStatus.DRAFT, (Object)ContentStatus.TRASHED);
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentManager;
    private final CommentManager commentManager;
    private final NewCommentDeduplicator commentDeduplicator;
    private final long contentId;
    private final Long parentCommentId;
    protected String commentBody;
    private Comment parentComment;
    private Comment comment;

    @Deprecated
    public CreateCommentCommandImpl(PermissionManager permissionManager, ContentEntityManager contentManager, CommentManager commentManager, long contentId, long parentCommentId, String commentBody) {
        this(permissionManager, contentManager, commentManager, contentId, parentCommentId, commentBody, null);
    }

    public CreateCommentCommandImpl(PermissionManager permissionManager, ContentEntityManager contentManager, CommentManager commentManager, long contentId, long parentCommentId, String commentBody, NewCommentDeduplicator commentDeduplicator) {
        this.contentManager = contentManager;
        this.permissionManager = permissionManager;
        this.commentManager = commentManager;
        this.contentId = contentId;
        this.parentCommentId = parentCommentId;
        this.commentBody = commentBody;
        this.commentDeduplicator = commentDeduplicator;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        FieldValidationError error = this.validate();
        if (error != null) {
            validator.addFieldValidationError(error);
        }
    }

    private FieldValidationError validate() {
        ContentEntityObject content = this.getContent();
        if (content == null || RESTRICTED_STATUSES.contains(content.getContentStatusObject())) {
            return new FieldValidationError("contentId", "content.doesnt.exist", this.contentId);
        }
        if (this.commentBody == null || this.commentBody.matches("\\s*")) {
            return new FieldValidationError("content", "content.empty", new Object[0]);
        }
        if (this.parentCommentId > 0L) {
            Comment parentComment = this.getParentComment();
            if (parentComment == null) {
                return new FieldValidationError("parentCommentId", "parent.comment.does.not.exist", this.parentCommentId);
            }
            ContentEntityObject container = parentComment.getContainer();
            if (container == null || !container.equals(content)) {
                return new FieldValidationError("parentCommentId", "parent.in.other.container", content, parentComment);
            }
        }
        return null;
    }

    @Override
    protected boolean isAuthorizedInternal() {
        ContentEntityObject content = this.getContent();
        return content == null || this.permissionManager.hasCreatePermission(this.getCurrentUser(), (Object)content, Comment.class);
    }

    @Override
    protected void executeInternal() {
        this.comment = this.commentManager.addCommentToObject(this.getContent(), this.getParentComment(), this.commentBody, this.commentDeduplicator);
    }

    private Comment getParentComment() {
        if (this.parentComment == null && this.parentCommentId > 0L) {
            this.parentComment = this.commentManager.getComment(this.parentCommentId);
        }
        return this.parentComment;
    }

    protected ContentEntityObject getContent() {
        return this.contentManager.getById(this.contentId);
    }

    @Override
    public Comment getComment() {
        return this.comment;
    }
}

