/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;

public class HistoryPageInlineComment {
    private Comment comment;
    private int diffVersion;
    private AbstractPage abstractPage;

    public HistoryPageInlineComment(Comment comment, AbstractPage abstractPage, int diffVersion) {
        this.comment = comment;
        this.abstractPage = abstractPage;
        this.diffVersion = diffVersion;
    }

    public Comment getComment() {
        return this.comment;
    }

    public int getDiffVersion() {
        return this.diffVersion;
    }

    public AbstractPage getAbstractPage() {
        return this.abstractPage;
    }
}

