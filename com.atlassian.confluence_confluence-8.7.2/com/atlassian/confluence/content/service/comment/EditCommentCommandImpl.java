/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.comment;

import com.atlassian.confluence.content.service.comment.EditCommentCommand;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;

public class EditCommentCommandImpl
extends AbstractServiceCommand
implements EditCommentCommand {
    private final PermissionManager permissionManager;
    private final CommentManager commentManager;
    private final long commentId;
    protected String newCommentBody;
    private boolean commentNotFound;
    private Comment comment;

    public EditCommentCommandImpl(PermissionManager permissionManager, CommentManager commentManager, long commentId, String newCommentBody) {
        this.permissionManager = permissionManager;
        this.commentManager = commentManager;
        this.commentId = commentId;
        this.newCommentBody = newCommentBody;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.getComment() == null) {
            validator.addFieldValidationError("commentId", "comment.doesnt.exist", this.commentId);
        }
        if (this.newCommentBody == null || this.newCommentBody.matches("\\s*")) {
            validator.addFieldValidationError("content", "content.empty");
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.getComment() == null || this.permissionManager.hasPermission(this.getCurrentUser(), Permission.EDIT, this.getComment());
    }

    @Override
    protected void executeInternal() {
        if (!this.newCommentBody.equals(this.getComment().getBodyContent().getBody())) {
            this.commentManager.updateCommentContent(this.getComment(), this.newCommentBody);
        }
    }

    @Override
    public Comment getComment() {
        if (!this.commentNotFound && this.comment == null) {
            this.comment = this.commentManager.getComment(this.commentId);
            this.commentNotFound = this.comment != null;
        }
        return this.comment;
    }
}

