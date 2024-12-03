/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.internal.security;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermission;
import java.util.Objects;

@Internal
public class ContentPermissionRemovedEvent {
    private final ContentEntityObject content;
    private final ContentPermission permission;

    public ContentPermissionRemovedEvent(ContentEntityObject content, ContentPermission permission) {
        this.content = content;
        this.permission = permission;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public ContentPermission getPermission() {
        return this.permission;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentPermissionRemovedEvent that = (ContentPermissionRemovedEvent)o;
        return Objects.equals(this.content, that.content) && Objects.equals(this.permission, that.permission);
    }

    public int hashCode() {
        return Objects.hash(this.content, this.permission);
    }
}

