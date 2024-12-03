/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.event.events.like;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.like.LikeEvent;
import com.atlassian.user.User;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AbstractLikeEvent
extends ContentEvent
implements LikeEvent {
    private final User user;
    private ContentEntityObject content;

    public AbstractLikeEvent(Object src, User user, ContentEntityObject content) {
        super(src, false);
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        this.user = user;
        if (content == null) {
            throw new IllegalArgumentException("content cannot be null");
        }
        this.content = content;
    }

    @Override
    public ContentEntityObject getContent() {
        return this.content;
    }

    @Override
    public User getOriginatingUser() {
        return this.user;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractLikeEvent)) {
            return false;
        }
        AbstractLikeEvent that = (AbstractLikeEvent)o;
        if (!Objects.equals(this.user, that.user)) {
            return false;
        }
        return this.getContent() != null ? this.getContent().equals(that.getContent()) : that.getContent() == null;
    }

    @Override
    public int hashCode() {
        int result = this.user != null ? this.user.hashCode() : 0;
        result = 31 * result + (this.getContent() != null ? this.getContent().hashCode() : 0);
        return result;
    }
}

