/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.security.ContentPermission;
import com.google.common.base.Preconditions;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class ContentPermissionEvent
extends ContentEvent {
    private static final long serialVersionUID = -4581616361651179535L;
    private @Nullable ContentPermission contentPermission;
    private ContentEntityObject content;
    private final boolean reindexNeeded;

    @Internal
    public ContentPermissionEvent(Object src, ContentEntityObject content, @Nullable ContentPermission contentPermission, boolean reindexNeeded) {
        super(src, false);
        this.content = (ContentEntityObject)Preconditions.checkNotNull((Object)content);
        this.contentPermission = contentPermission;
        this.reindexNeeded = reindexNeeded;
    }

    public ContentPermissionEvent(Object src, ContentEntityObject content, @Nullable ContentPermission contentPermission) {
        this(src, content, contentPermission, true);
    }

    public @Nullable ContentPermission getContentPermission() {
        return this.contentPermission;
    }

    @Override
    public @NonNull ContentEntityObject getContent() {
        return this.content;
    }

    public boolean isReindexNeeded() {
        return this.reindexNeeded;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ContentPermissionEvent)) {
            return false;
        }
        ContentPermissionEvent other = (ContentPermissionEvent)obj;
        if (!this.content.equals(other.content)) {
            return false;
        }
        if (this.contentPermission != null ? !this.contentPermission.equals(other.contentPermission) : other.contentPermission != null) {
            return false;
        }
        return this.reindexNeeded == other.isReindexNeeded();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.content, this.contentPermission, this.reindexNeeded);
    }
}

