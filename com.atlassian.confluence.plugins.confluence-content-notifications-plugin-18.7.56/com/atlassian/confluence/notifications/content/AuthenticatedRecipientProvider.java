/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.AuthenticatedUserImpersonator
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.content.WatchTypeBasedRecipientProvider;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;

public abstract class AuthenticatedRecipientProvider<PAYLOAD extends NotificationPayload>
extends WatchTypeBasedRecipientProvider<PAYLOAD> {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private final UserAccessor userAccessor;

    public AuthenticatedRecipientProvider(TransactionTemplate transactionTemplate, NotificationManager notificationManager, PageManager pageManager, UserAccessor userAccessor, PermissionManager permissionManager, ContentEntityManager contentEntityManager, ConfluenceAccessManager confluenceAccessManager) {
        super(transactionTemplate, notificationManager, pageManager, permissionManager, contentEntityManager, confluenceAccessManager);
        this.userAccessor = userAccessor;
    }

    @Override
    protected final Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotifications(Notification<PAYLOAD> notification) {
        if (notification.getOriginator().isEmpty()) {
            log.onlyTrace("Notification triggered by anonymous user, authentication not required.", new Object[]{notification.toString()});
            return this.computeNotificationsInContextOfNotifier(notification);
        }
        UserKey userKey = (UserKey)notification.getOriginator().get();
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(userKey);
        return (Iterable)AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser(() -> this.computeNotificationsInContextOfNotifier(notification), (User)user);
    }

    protected abstract Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotificationsInContextOfNotifier(Notification<PAYLOAD> var1);
}

