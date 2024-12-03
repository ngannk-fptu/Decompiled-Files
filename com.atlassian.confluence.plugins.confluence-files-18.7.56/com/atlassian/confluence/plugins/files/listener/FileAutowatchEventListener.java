/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.files.listener;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugins.files.event.FileCommentCreateEvent;
import com.atlassian.confluence.plugins.files.event.FileCommentDeleteEvent;
import com.atlassian.confluence.plugins.files.event.FileCommentUpdateEvent;
import com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;

public class FileAutowatchEventListener {
    private final NotificationManager notificationManager;
    private final UserAccessor userAccessor;

    public FileAutowatchEventListener(@ComponentImport NotificationManager notificationManager, @ComponentImport UserAccessor userAccessor) {
        this.notificationManager = notificationManager;
        this.userAccessor = userAccessor;
    }

    @EventListener
    public void onFileCommentCreate(FileCommentCreateEvent event) {
        this.addUserToFileAutowatch((User)event.getOriginatingUser(), event.getParentFile());
    }

    @EventListener
    public void onFileCommentUpdate(FileCommentUpdateEvent event) {
        if (Boolean.TRUE.equals(event.getFileCommentInput().isResolved())) {
            this.addUserToFileAutowatch((User)event.getOriginatingUser(), event.getParentFile());
        }
    }

    @EventListener
    public void onFileCommentDelete(FileCommentDeleteEvent event) {
        this.addUserToFileAutowatch((User)event.getOriginatingUser(), event.getParentFile());
    }

    @EventListener
    public void onFileCommentMention(ConfluenceMentionEvent event) {
        Comment comment;
        ContentEntityObject commentContainer;
        ContentEntityObject content = event.getContent();
        if (content instanceof Comment && (commentContainer = (comment = (Comment)content).getContainer()) instanceof Attachment) {
            ConfluenceUser mentionedUser = this.userAccessor.getUserByKey(event.getMentionedUserProfile().getUserKey());
            this.addUserToFileAutowatch((User)mentionedUser, (Attachment)commentContainer, false);
        }
    }

    private void addUserToFileAutowatch(User user, Attachment file) {
        this.addUserToFileAutowatch(user, file, true);
    }

    private void addUserToFileAutowatch(User user, Attachment file, boolean isActionAuthor) {
        if ((!isActionAuthor || isActionAuthor && this.userAccessor.getConfluenceUserPreferences(user).isWatchingOwnContent()) && file != null && !(file.getContainer() instanceof Draft)) {
            this.notificationManager.addContentNotification(user, file.getContainer());
        }
    }
}

