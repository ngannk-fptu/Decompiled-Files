/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.comment;

import com.atlassian.confluence.content.service.comment.DeleteCommentCommand;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;

public class DeleteCommentCommandImpl
extends AbstractServiceCommand
implements DeleteCommentCommand {
    private final long commentId;
    private final CommentManager commentManager;
    private final PermissionManager permissionManager;
    private Comment comment;
    private boolean commentNotFound;

    public DeleteCommentCommandImpl(PermissionManager permissionManager, CommentManager commentManager, long commentId) {
        this.permissionManager = permissionManager;
        this.commentManager = commentManager;
        this.commentId = commentId;
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.getComment() == null || this.permissionManager.hasPermission(this.getCurrentUser(), Permission.REMOVE, this.getComment());
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.getComment() == null) {
            validator.addFieldValidationError("commentId", "comment.doesnt.exist", this.commentId);
        }
    }

    @Override
    protected void executeInternal() {
        this.commentManager.removeCommentFromPage(this.commentId);
    }

    @Override
    public Comment getComment() {
        if (this.comment == null && !this.commentNotFound) {
            this.comment = this.commentManager.getComment(this.commentId);
            this.commentNotFound = this.comment == null;
        }
        return this.comment;
    }
}

