/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.comment;

import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.Comment;

public interface CommentCommand
extends ServiceCommand {
    public Comment getComment();
}

