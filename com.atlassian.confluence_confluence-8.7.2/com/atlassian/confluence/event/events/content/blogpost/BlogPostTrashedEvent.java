/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlogPostTrashedEvent
extends BlogPostEvent
implements Trashed,
UserDriven {
    private static final long serialVersionUID = 6740788814729740280L;
    private final @Nullable User trasher;

    @Deprecated
    public BlogPostTrashedEvent(Object source, BlogPost blogPost, @Nullable User trasher) {
        this(source, blogPost, trasher, false);
    }

    public BlogPostTrashedEvent(Object source, BlogPost blogPost, @Nullable User trasher, boolean suppressNotifications) {
        super(source, blogPost, suppressNotifications);
        this.trasher = trasher;
    }

    @Override
    public @Nullable User getOriginatingUser() {
        return this.trasher;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof BlogPostTrashedEvent)) {
            return false;
        }
        BlogPostTrashedEvent that = (BlogPostTrashedEvent)obj;
        return Objects.equals(this.trasher, that.trasher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.trasher);
    }
}

