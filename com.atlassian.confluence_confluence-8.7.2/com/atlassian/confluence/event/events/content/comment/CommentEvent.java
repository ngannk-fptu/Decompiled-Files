/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.comment;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.pages.Comment;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class CommentEvent
extends ContentEvent {
    private static final long serialVersionUID = 4329456239774014156L;
    protected Comment comment;

    @Deprecated
    public CommentEvent(Object src, Comment comment) {
        super(src, false);
        this.comment = comment;
    }

    public CommentEvent(Object source, Comment comment, @Nullable OperationContext<?> context) {
        super(source, context);
        this.comment = comment;
    }

    public Comment getComment() {
        return this.comment;
    }

    @Override
    public ContentEntityObject getContent() {
        return this.getComment();
    }
}

