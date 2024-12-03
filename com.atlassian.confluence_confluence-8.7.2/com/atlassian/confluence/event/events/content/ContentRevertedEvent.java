/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@Internal
public class ContentRevertedEvent {
    private final ContentEntityObject entity;
    private final int version;
    private final String revertComment;

    public ContentRevertedEvent(ContentEntityObject entity, int version, @Nullable String revertComment) {
        this.entity = entity;
        this.version = version;
        this.revertComment = revertComment;
    }

    public ContentEntityObject getEntity() {
        return this.entity;
    }

    public int getVersion() {
        return this.version;
    }

    public String getRevertComment() {
        return this.revertComment;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentRevertedEvent that = (ContentRevertedEvent)o;
        return this.version == that.version && this.entity.equals(that.entity) && Objects.equals(this.revertComment, that.revertComment);
    }

    public int hashCode() {
        return Objects.hash(this.entity, this.version, this.revertComment);
    }
}

