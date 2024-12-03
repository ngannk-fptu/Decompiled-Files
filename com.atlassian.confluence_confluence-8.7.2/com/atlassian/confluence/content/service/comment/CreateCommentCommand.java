/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.comment;

import com.atlassian.confluence.content.service.comment.CommentCommand;
import com.atlassian.confluence.pages.Comment;

public interface CreateCommentCommand
extends CommentCommand {
    @Override
    public Comment getComment();
}

