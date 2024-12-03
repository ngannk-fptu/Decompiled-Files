/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider;
import com.atlassian.confluence.plugins.like.notifications.LikeNotification;
import com.atlassian.confluence.plugins.like.notifications.LikeNotificationManager;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.confluence.plugins.like.notifications.SimpleLikePayload;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateLikeEventRecipientProvider
extends RecipientsProviderTemplate<LikePayload>
implements BatchingRecipientsProvider<LikePayload> {
    private final LikeNotificationManager likeNotificationManager;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final PermissionManager permissionManager;
    private static final List<UserRole> USER_ROLES = ImmutableList.of((Object)new ConfluenceUserRole(Notification.WatchType.NETWORK.name()), (Object)new ConfluenceUserRole(Notification.WatchType.SINGLE_PAGE.name()));

    public CreateLikeEventRecipientProvider(LikeNotificationManager likeNotificationManager, ConfluenceAccessManager confluenceAccessManager, PermissionManager permissionManager) {
        this.likeNotificationManager = likeNotificationManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.permissionManager = permissionManager;
    }

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<LikePayload> notification) {
        return this.getRoleRecipients((LikePayload)notification.getPayload());
    }

    private Iterable<RoleRecipient> getRoleRecipients(LikePayload payload) {
        List<LikeNotification> notifications = this.likeNotificationManager.getNotifications(payload);
        return notifications.stream().filter(this::validRecipientFilter).map(input -> new UserKeyRoleRecipient(input.getRole(), input.getRecipient().getKey())).collect(Collectors.toList());
    }

    public Iterable<UserRole> getUserRoles() {
        return USER_ROLES;
    }

    private boolean validRecipientFilter(LikeNotification notification) {
        ConfluenceUser receiver = notification.getRecipient();
        return this.confluenceAccessManager.getUserAccessStatus((User)receiver).hasLicensedAccess() && this.permissionManager.hasPermissionNoExemptions((User)receiver, Permission.VIEW, (Object)notification.getContent());
    }

    public Iterable<RoleRecipient> batchUserBasedRecipientsFor(String randomOriginatorUserKey, String id, String contentType) {
        ContentType type = ContentType.valueOf((String)contentType);
        if (!type.in(new BaseApiEnum[]{ContentType.PAGE, ContentType.BLOG_POST, ContentType.COMMENT})) {
            return Collections.emptyList();
        }
        SimpleLikePayload payload = new SimpleLikePayload(Long.parseLong(id), type, randomOriginatorUserKey);
        return this.getRoleRecipients(payload);
    }
}

