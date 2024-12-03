/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.pages.BlogPost;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class BlogPostEvent
extends ContentEvent {
    private static final long serialVersionUID = 2215331954777148512L;
    private final BlogPost blogPost;

    @Deprecated
    public BlogPostEvent(Object source, BlogPost blogPost) {
        this(source, blogPost, false);
    }

    @Deprecated
    public BlogPostEvent(Object source, BlogPost blogPost, boolean suppressNotifications) {
        super(source, suppressNotifications);
        this.blogPost = Objects.requireNonNull(blogPost);
    }

    public BlogPostEvent(Object source, BlogPost blogPost, @Nullable OperationContext<?> operationContext) {
        super(source, operationContext);
        this.blogPost = Objects.requireNonNull(blogPost);
    }

    public @NonNull BlogPost getBlogPost() {
        return this.blogPost;
    }

    @Override
    public @NonNull ContentEntityObject getContent() {
        return this.getBlogPost();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof BlogPostEvent)) {
            return false;
        }
        BlogPostEvent other = (BlogPostEvent)obj;
        return this.blogPost.equals(other.blogPost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.blogPost);
    }
}

