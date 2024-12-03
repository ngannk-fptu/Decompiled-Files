/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.mentions.api;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;

public class ConfluenceMentionEvent
extends ConfluenceEvent
implements NotificationEnabledEvent {
    private final ContentEntityObject content;
    private final UserProfile mentionedUserProfile;
    private final ConfluenceUser mentioningUser;
    private final String mentionHtml;

    public ConfluenceMentionEvent(Object source, ContentEntityObject content, UserProfile mentionedUserProfile, ConfluenceUser mentioningUser, String mentionHtml) {
        super(source);
        this.content = content;
        this.mentionedUserProfile = mentionedUserProfile;
        this.mentioningUser = mentioningUser;
        this.mentionHtml = mentionHtml;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public UserProfile getMentionedUserProfile() {
        return this.mentionedUserProfile;
    }

    public User getMentioningUser() {
        return this.mentioningUser;
    }

    public ConfluenceUser getMentionAuthor() {
        return this.mentioningUser;
    }

    public String getMentionHtml() {
        return this.mentionHtml;
    }

    public boolean isSuppressNotifications() {
        return false;
    }
}

