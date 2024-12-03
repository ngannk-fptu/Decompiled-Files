/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class LikeNotification {
    private final ContentEntityObject contentEntity;
    private final ConfluenceUser recipient;
    private final User liker;
    private final User author;
    private final UserRole role;

    public LikeNotification(ConfluenceUser recipient, User liker, User author, ContentEntityObject contentEntity, UserRole role) {
        this.role = role;
        if (this.isInvalidUser((User)recipient)) {
            throw new IllegalArgumentException("Invalid recipient: " + recipient);
        }
        if (this.isInvalidUser(liker)) {
            throw new IllegalArgumentException("Invalid liker: " + recipient);
        }
        if (author != null && StringUtils.isBlank((CharSequence)author.getName())) {
            throw new IllegalArgumentException("Author is invalid because it has no username: " + author);
        }
        if (contentEntity == null) {
            throw new IllegalArgumentException("content entity cannot be null");
        }
        this.recipient = recipient;
        this.liker = liker;
        this.author = author;
        this.contentEntity = contentEntity;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LikeNotification)) {
            return false;
        }
        LikeNotification that = (LikeNotification)o;
        if (this.contentEntity.getId() != that.contentEntity.getId()) {
            return false;
        }
        if (!this.liker.getName().equals(that.liker.getName())) {
            return false;
        }
        if (!this.recipient.getName().equals(that.recipient.getName())) {
            return false;
        }
        if (this.author == null && that.author != null || this.author != null && that.author == null) {
            return false;
        }
        return this.author != null ? this.author.getName().equals(that.author.getName()) : true;
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append((Object)this.recipient.getName()).append((Object)this.liker.getName()).append(this.contentEntity.getId());
        if (this.author != null) {
            builder.append((Object)this.author.getName());
        }
        return builder.toHashCode();
    }

    private boolean isInvalidUser(User user) {
        return user == null || StringUtils.isBlank((CharSequence)user.getName());
    }

    public ContentEntityObject getContent() {
        return this.contentEntity;
    }

    public ConfluenceUser getRecipient() {
        return this.recipient;
    }

    public User getLiker() {
        return this.liker;
    }

    public User getAuthor() {
        return this.author;
    }

    public UserRole getRole() {
        return this.role;
    }
}

