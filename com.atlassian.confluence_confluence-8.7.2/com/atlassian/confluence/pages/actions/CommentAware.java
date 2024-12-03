/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.Comment;

public interface CommentAware {
    public Comment getComment();

    public void setComment(Comment var1);
}

