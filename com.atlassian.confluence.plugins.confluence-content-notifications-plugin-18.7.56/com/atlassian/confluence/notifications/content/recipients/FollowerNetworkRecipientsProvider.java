/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.recipients;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.notifications.content.WatchTypeBasedRecipientProvider;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;

public class FollowerNetworkRecipientsProvider
extends WatchTypeBasedRecipientProvider<ContentIdPayload> {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private final UserAccessor userAccessor;

    public FollowerNetworkRecipientsProvider(TransactionTemplate transactionTemplate, NotificationManager notificationManager, PageManager pageManager, UserAccessor userAccessor, PermissionManager permissionManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ConfluenceAccessManager confluenceAccessManager) {
        super(transactionTemplate, notificationManager, pageManager, permissionManager, contentEntityManager, confluenceAccessManager);
        this.userAccessor = userAccessor;
    }

    @Override
    protected Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotifications(Notification<ContentIdPayload> notification) {
        Maybe maybeOriginator = notification.getOriginator();
        if (maybeOriginator.isEmpty()) {
            log.warnOrDebug("The notification [%s] has no originator, thus no followers can be provided.", new Object[]{notification});
            return Collections.emptyList();
        }
        ConfluenceUser originator = this.userAccessor.getExistingUserByKey((UserKey)maybeOriginator.get());
        if (originator == null) {
            log.warnOrDebug("Could not find an existing originator with key [%s] for notification [%s], thus no followers can be provided.", new Object[]{notification.getOriginator(), notification});
            return Collections.emptyList();
        }
        return this.permissionFiltered(this.notificationManager.findNotificationsByFollowing((User)originator), ((ContentIdPayload)notification.getPayload()).getContentId());
    }
}

