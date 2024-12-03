/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.plugins.tasklist.notification.api.TaskPayload;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Set;

public class TaskNotificationRecipientProvider
extends RecipientsProviderTemplate<TaskPayload> {
    private final ConfluenceAccessManager confluenceAccessManager;
    private final UserAccessor userAccessor;
    public static final UserRole USER_ROLE = new ConfluenceUserRole("TASK_UPDATE_NOTIFICATION");

    public TaskNotificationRecipientProvider(ConfluenceAccessManager confluenceAccessManager, UserAccessor userAccessor) {
        this.confluenceAccessManager = confluenceAccessManager;
        this.userAccessor = userAccessor;
    }

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<TaskPayload> simpleSendTaskPayloadNotification) {
        Set<UserKey> unfilteredUserKeysToNotify = ((TaskPayload)simpleSendTaskPayloadNotification.getPayload()).getTasks().keySet();
        return Iterables.transform((Iterable)Iterables.filter(unfilteredUserKeysToNotify, this.validRecipientFilter()), this.toRoleRecipient());
    }

    private Function<UserKey, RoleRecipient> toRoleRecipient() {
        return new Function<UserKey, RoleRecipient>(){

            public RoleRecipient apply(UserKey userKey) {
                return new UserKeyRoleRecipient(USER_ROLE, userKey);
            }
        };
    }

    private Predicate<UserKey> validRecipientFilter() {
        return new Predicate<UserKey>(){

            public boolean apply(UserKey userKey) {
                ConfluenceUser user = TaskNotificationRecipientProvider.this.userAccessor.getExistingUserByKey(userKey);
                AccessStatus userAccessStatus = TaskNotificationRecipientProvider.this.confluenceAccessManager.getUserAccessStatus((User)user);
                return userAccessStatus.hasLicensedAccess();
            }
        };
    }

    public Iterable<UserRole> getUserRoles() {
        return ImmutableList.of((Object)USER_ROLE);
    }
}

