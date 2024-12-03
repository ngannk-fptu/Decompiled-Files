/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.Edited;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.types.ConfluenceEntityUpdated;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlogPostUpdateEvent
extends BlogPostEvent
implements Edited,
ConfluenceEntityUpdated,
NotificationEnabledEvent {
    private static final long serialVersionUID = 6711843556260014796L;
    private final @Nullable BlogPost originalBlogPost;
    private final PageUpdateTrigger updateTrigger;

    @Deprecated
    public BlogPostUpdateEvent(Object src, BlogPost updatedBlogPost, @Nullable BlogPost originalBlogPost) {
        this(src, updatedBlogPost, originalBlogPost, false, PageUpdateTrigger.UNKNOWN);
    }

    @Deprecated
    public BlogPostUpdateEvent(Object src, BlogPost updatedBlogPost, @Nullable BlogPost originalBlogPost, boolean suppressNotifications) {
        this(src, updatedBlogPost, originalBlogPost, suppressNotifications, PageUpdateTrigger.UNKNOWN);
    }

    @Deprecated
    public BlogPostUpdateEvent(Object src, BlogPost updatedBlogPost, @Nullable BlogPost originalBlogPost, boolean suppressNotifications, PageUpdateTrigger updateTrigger) {
        super(src, updatedBlogPost, suppressNotifications);
        this.originalBlogPost = originalBlogPost;
        this.updateTrigger = Objects.requireNonNull(updateTrigger);
    }

    public BlogPostUpdateEvent(Object source, BlogPost updatedBlogPost, @Nullable BlogPost originalBlogPost, @Nullable OperationContext<PageUpdateTrigger> operationContext) {
        super(source, updatedBlogPost, operationContext);
        this.originalBlogPost = originalBlogPost;
        this.updateTrigger = operationContext != null ? operationContext.getUpdateTrigger() : PageUpdateTrigger.UNKNOWN;
    }

    public @Nullable BlogPost getOriginalBlogPost() {
        return this.originalBlogPost;
    }

    @Override
    public boolean isMinorEdit() {
        return super.isSuppressNotifications();
    }

    public @NonNull PageUpdateTrigger getUpdateTrigger() {
        return this.updateTrigger;
    }

    @Override
    public @Nullable ConfluenceEntityObject getOld() {
        return this.originalBlogPost;
    }

    @Override
    public @NonNull ConfluenceEntityObject getNew() {
        return this.getContent();
    }

    @EnsuresNonNullIf(expression={"getOriginalBlogPost()"}, result=true)
    public boolean isTitleChanged() {
        return this.originalBlogPost != null && !this.originalBlogPost.getTitle().equals(this.getContent().getTitle());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof BlogPostUpdateEvent)) {
            return false;
        }
        BlogPostUpdateEvent that = (BlogPostUpdateEvent)obj;
        if (!Objects.equals(this.originalBlogPost, that.originalBlogPost)) {
            return false;
        }
        return this.updateTrigger == that.updateTrigger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originalBlogPost, this.updateTrigger);
    }
}

