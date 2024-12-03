/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.render.xhtml.editor.EditorConverter;
import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.content.service.comment.CreateCommentCommandImpl;
import com.atlassian.confluence.content.service.comment.CreateCommentFromEditorCommand;
import com.atlassian.confluence.content.service.comment.DeleteCommentCommand;
import com.atlassian.confluence.content.service.comment.DeleteCommentCommandImpl;
import com.atlassian.confluence.content.service.comment.EditCommentCommand;
import com.atlassian.confluence.content.service.comment.EditCommentCommandImpl;
import com.atlassian.confluence.content.service.comment.EditCommentFromEditorCommand;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.SubmissionTokenCommentDeduplicator;
import com.atlassian.confluence.security.PermissionManager;
import java.util.UUID;

public class DefaultCommentService
implements CommentService {
    private final CommentManager commentManager;
    private PermissionManager permissionManager;
    private final ContentEntityManager contentManager;
    private final EditorConverter editorConverter;

    public DefaultCommentService(CommentManager commentManager, PermissionManager permissionManager, ContentEntityManager contentManager, EditorConverter editorConverter) {
        this.commentManager = commentManager;
        this.permissionManager = permissionManager;
        this.contentManager = contentManager;
        this.editorConverter = editorConverter;
    }

    @Override
    public DeleteCommentCommand newDeleteCommentCommand(long commentId) {
        return new DeleteCommentCommandImpl(this.permissionManager, this.commentManager, commentId);
    }

    @Override
    public CreateCommentCommand newCreateCommentCommand(long contentId, String content, UUID submissionToken) {
        return new CreateCommentCommandImpl(this.permissionManager, this.contentManager, this.commentManager, contentId, 0L, content, this.commentDeduplicator(submissionToken));
    }

    private SubmissionTokenCommentDeduplicator commentDeduplicator(UUID uuid) {
        return new SubmissionTokenCommentDeduplicator(uuid);
    }

    @Override
    public CreateCommentCommand newCreateCommentCommand(long contentId, long parentCommentId, String content, UUID submissionToken) {
        return new CreateCommentCommandImpl(this.permissionManager, this.contentManager, this.commentManager, contentId, parentCommentId, content, this.commentDeduplicator(submissionToken));
    }

    @Override
    public EditCommentCommand newEditCommentCommand(long commentId, String newContent) {
        return new EditCommentCommandImpl(this.permissionManager, this.commentManager, commentId, newContent);
    }

    @Override
    public CreateCommentCommand newCreateCommentFromEditorCommand(long contentId, long parentCommentId, String content, UUID submissionToken) {
        return new CreateCommentFromEditorCommand(this.permissionManager, this.contentManager, this.commentManager, this.editorConverter, contentId, parentCommentId, content, this.commentDeduplicator(submissionToken));
    }

    @Override
    public EditCommentCommand newEditCommentFromEditorCommand(long commentId, String newContent) {
        return new EditCommentFromEditorCommand(this.permissionManager, this.commentManager, this.editorConverter, commentId, newContent);
    }
}

