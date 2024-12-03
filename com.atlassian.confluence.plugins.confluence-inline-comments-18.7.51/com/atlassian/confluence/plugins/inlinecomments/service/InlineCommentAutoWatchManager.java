/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import org.springframework.beans.factory.annotation.Qualifier;

public class InlineCommentAutoWatchManager {
    private final ContentEntityManager contentEntityManager;
    private final UserAccessor userAccessor;
    private final NotificationManager notificationManager;

    public InlineCommentAutoWatchManager(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, UserAccessor userAccessor, NotificationManager notificationManager) {
        this.contentEntityManager = contentEntityManager;
        this.userAccessor = userAccessor;
        this.notificationManager = notificationManager;
    }

    public void watchContentRespectingUserAutoWatchPreference(long contentId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ContentEntityObject entity = this.contentEntityManager.getById(contentId);
        if (entity != null && this.userAccessor.getConfluenceUserPreferences((User)user).isWatchingOwnContent()) {
            this.notificationManager.addContentNotification((User)user, entity);
        }
    }
}

