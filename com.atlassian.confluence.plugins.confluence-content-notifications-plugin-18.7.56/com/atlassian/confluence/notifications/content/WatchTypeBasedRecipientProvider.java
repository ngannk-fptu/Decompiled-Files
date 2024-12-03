/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;

public abstract class WatchTypeBasedRecipientProvider<PAYLOAD extends NotificationPayload>
extends RecipientsProviderTemplate<PAYLOAD> {
    protected final TransactionTemplate transactionTemplate;
    protected final NotificationManager notificationManager;
    protected final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;
    private final ConfluenceAccessManager confluenceAccessManager;

    public WatchTypeBasedRecipientProvider(TransactionTemplate transactionTemplate, NotificationManager notificationManager, PageManager pageManager, PermissionManager permissionManager, ContentEntityManager contentEntityManager, ConfluenceAccessManager confluenceAccessManager) {
        this.transactionTemplate = transactionTemplate;
        this.notificationManager = notificationManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
        this.confluenceAccessManager = confluenceAccessManager;
    }

    public Iterable<UserRole> getUserRoles() {
        return WatchTypeUtil.watchTypesToUserRoles();
    }

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<PAYLOAD> notification) {
        return (Iterable)this.transactionTemplate.execute(() -> {
            ImmutableList.Builder roleRecipientBuilder = ImmutableList.builder();
            for (com.atlassian.confluence.mail.notification.Notification fatNotification : this.computeNotifications(notification)) {
                ConfluenceUserRole role = new ConfluenceUserRole(fatNotification.getWatchType().name());
                UserKey userKey = fatNotification.getReceiver().getKey();
                roleRecipientBuilder.add((Object)new UserKeyRoleRecipient((UserRole)role, userKey));
            }
            return roleRecipientBuilder.build();
        });
    }

    protected Iterable<com.atlassian.confluence.mail.notification.Notification> permissionFiltered(List<com.atlassian.confluence.mail.notification.Notification> notifications, long id) {
        ContentEntityObject entityObject = this.contentEntityManager.getById(id);
        return Iterables.filter(notifications, input -> {
            ConfluenceUser receiver = input.getReceiver();
            AccessStatus userAccessStatus = this.confluenceAccessManager.getUserAccessStatus((User)receiver);
            return userAccessStatus.hasLicensedAccess() && this.permissionManager.hasPermissionNoExemptions((User)receiver, Permission.VIEW, (Object)entityObject);
        });
    }

    protected abstract Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotifications(Notification<PAYLOAD> var1);
}

