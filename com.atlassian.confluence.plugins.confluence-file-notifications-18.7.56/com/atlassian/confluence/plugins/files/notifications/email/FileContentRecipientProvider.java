/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.content.WatchTypeUtil
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentMentionUpdatePayload;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentPayload;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FileContentRecipientProvider
extends RecipientsProviderTemplate<FileContentPayload> {
    private static final UserRole MENTION_USER_ROLE = new ConfluenceUserRole("com.atlassian.confluence.plugins.mentions");
    private final ContentEntityManager contentEntityManager;
    private final NotificationManager notificationManager;
    private final UserAccessor userAccessor;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final PermissionManager permissionManager;

    public FileContentRecipientProvider(NotificationManager notificationManager, ContentEntityManager contentEntityManager, UserAccessor userAccessor, ConfluenceAccessManager confluenceAccessManager, PermissionManager permissionManager) {
        this.contentEntityManager = contentEntityManager;
        this.notificationManager = notificationManager;
        this.userAccessor = userAccessor;
        this.confluenceAccessManager = confluenceAccessManager;
        this.permissionManager = permissionManager;
    }

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<FileContentPayload> fileContentPayloadNotification) {
        FileContentPayload payload = (FileContentPayload)fileContentPayloadNotification.getPayload();
        ContentEntityObject container = this.contentEntityManager.getById(payload.getContainerNotificationContent().getContentId().asLong());
        if (container == null) {
            return Collections.emptyList();
        }
        if (payload.getType() == FileContentEventType.MENTION_IN_COMMENT) {
            FileContentMentionUpdatePayload mentionUpdatePayload = (FileContentMentionUpdatePayload)payload;
            return this.permissionFiltered(Collections.singletonList(new UserKeyRoleRecipient(MENTION_USER_ROLE, new UserKey(mentionUpdatePayload.getMentionedUserKey()))), container);
        }
        HashSet notificationSet = new HashSet();
        payload.getFileNotificationContents().forEach(fileNotification -> {
            ContentEntityObject content = this.contentEntityManager.getById(fileNotification.getContentId().asLong());
            notificationSet.addAll(this.notificationManager.getNotificationsByContent(content));
        });
        notificationSet.addAll(this.notificationManager.getNotificationsByContent(container));
        if (container instanceof Spaced) {
            Spaced target = (Spaced)container;
            notificationSet.addAll(this.notificationManager.getNotificationsBySpaceAndType(target.getSpace(), null));
        }
        Collection roleRecipients = notificationSet.stream().filter(notification -> notification.getWatchType() != null).map(notification -> new UserKeyRoleRecipient((UserRole)new ConfluenceUserRole(notification.getWatchType().name()), notification.getReceiver().getKey())).collect(Collectors.toSet());
        return this.permissionFiltered(roleRecipients, container);
    }

    @VisibleForTesting
    protected Iterable<RoleRecipient> permissionFiltered(Iterable<RoleRecipient> unfilteredRoleRecipients, ContentEntityObject content) {
        return StreamSupport.stream(unfilteredRoleRecipients.spliterator(), false).filter(recipient -> {
            ConfluenceUser confluenceUser = this.userAccessor.getExistingUserByKey(recipient.getUserKey());
            AccessStatus userAccessStatus = this.confluenceAccessManager.getUserAccessStatus((User)confluenceUser);
            return userAccessStatus.hasLicensedAccess() && this.permissionManager.hasPermissionNoExemptions((User)confluenceUser, Permission.VIEW, (Object)content);
        }).collect(Collectors.toList());
    }

    public Iterable<UserRole> getUserRoles() {
        return WatchTypeUtil.watchTypesToUserRoles();
    }
}

