/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlogPostMovedEvent
extends BlogPostEvent
implements Updated,
UserDriven,
NotificationEnabledEvent {
    private static final long serialVersionUID = -2402714502588252013L;
    private final @Nullable ConfluenceUser user;
    private final Space originalSpace;
    private final Space currentSpace;

    public BlogPostMovedEvent(Object src, @Nullable ConfluenceUser user, BlogPost blogPost, Space originalSpace, Space currentSpace) {
        super(src, blogPost, false);
        this.user = user;
        this.originalSpace = Objects.requireNonNull(originalSpace);
        this.currentSpace = Objects.requireNonNull(currentSpace);
    }

    public @NonNull Space getCurrentSpace() {
        return this.currentSpace;
    }

    public @NonNull Space getOriginalSpace() {
        return this.originalSpace;
    }

    @Override
    public @Nullable User getOriginatingUser() {
        return this.user;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlogPostMovedEvent)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BlogPostMovedEvent that = (BlogPostMovedEvent)o;
        if (!this.currentSpace.equals(that.currentSpace)) {
            return false;
        }
        if (!this.originalSpace.equals(that.originalSpace)) {
            return false;
        }
        return Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.user, this.originalSpace, this.currentSpace);
    }
}

