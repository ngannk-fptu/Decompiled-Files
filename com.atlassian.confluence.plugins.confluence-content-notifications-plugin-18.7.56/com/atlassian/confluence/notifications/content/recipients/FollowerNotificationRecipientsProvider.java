/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.notifications.content.recipients;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider;
import com.atlassian.confluence.notifications.content.FollowerPayload;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.util.Collections;

@ExperimentalSpi
public class FollowerNotificationRecipientsProvider
extends RecipientsProviderTemplate<FollowerPayload>
implements BatchingRecipientsProvider<FollowerPayload> {
    private static final UserRole USER_ROLE = new ConfluenceUserRole("NEW_FOLLOWER_NOTIFICATION");

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<FollowerPayload> notification) {
        return ImmutableList.of((Object)new UserKeyRoleRecipient(USER_ROLE, new UserKey(((FollowerPayload)notification.getPayload()).getUserBeingFollowed())));
    }

    public Iterable<UserRole> getUserRoles() {
        return ImmutableList.of((Object)USER_ROLE);
    }

    public Iterable<RoleRecipient> batchUserBasedRecipientsFor(String randomOriginatorUserKey, String id, String contentType) {
        if (!contentType.equals("user")) {
            return Collections.emptyList();
        }
        return ImmutableList.of((Object)new UserKeyRoleRecipient(USER_ROLE, new UserKey(id)));
    }
}

