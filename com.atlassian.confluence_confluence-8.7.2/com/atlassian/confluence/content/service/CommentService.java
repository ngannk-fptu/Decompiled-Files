/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.content.service.comment.DeleteCommentCommand;
import com.atlassian.confluence.content.service.comment.EditCommentCommand;
import java.util.UUID;

public interface CommentService {
    public DeleteCommentCommand newDeleteCommentCommand(long var1);

    public CreateCommentCommand newCreateCommentCommand(long var1, String var3, UUID var4);

    public CreateCommentCommand newCreateCommentCommand(long var1, long var3, String var5, UUID var6);

    public CreateCommentCommand newCreateCommentFromEditorCommand(long var1, long var3, String var5, UUID var6);

    public EditCommentCommand newEditCommentFromEditorCommand(long var1, String var3);

    public EditCommentCommand newEditCommentCommand(long var1, String var3);
}

