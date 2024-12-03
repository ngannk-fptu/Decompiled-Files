/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.blogpost;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.pages.BlogPost;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlogPostCreateEvent
extends BlogPostEvent
implements Created,
NotificationEnabledEvent {
    private static final long serialVersionUID = 3711105712672380972L;
    private final ImmutableMap<String, Serializable> context;

    @Deprecated
    public BlogPostCreateEvent(Object src, BlogPost blogPost) {
        this(src, blogPost, Collections.emptyMap(), false);
    }

    @Deprecated
    public BlogPostCreateEvent(Object src, BlogPost blogPost, Map<String, Serializable> context) {
        this(src, blogPost, context, false);
    }

    @Deprecated
    public BlogPostCreateEvent(Object source, BlogPost blogPost, Map<String, Serializable> context, boolean suppressNotifications) {
        super(source, blogPost, suppressNotifications);
        this.context = ImmutableMap.copyOf(context);
    }

    public BlogPostCreateEvent(Object source, BlogPost blogPost, Map<String, Serializable> context, @Nullable OperationContext<?> operationContext) {
        super(source, blogPost, operationContext);
        this.context = ImmutableMap.copyOf(context);
    }

    @Deprecated
    public @NonNull ImmutableMap<String, Serializable> getContext() {
        return this.context;
    }

    public @NonNull Map<String, Serializable> getContextMap() {
        return this.getContext();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof BlogPostCreateEvent)) {
            return false;
        }
        BlogPostCreateEvent that = (BlogPostCreateEvent)o;
        return this.context.equals(that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.context);
    }
}

