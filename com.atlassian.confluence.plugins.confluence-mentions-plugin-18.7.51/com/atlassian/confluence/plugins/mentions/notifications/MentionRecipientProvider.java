/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.mentions.notifications;

import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.plugins.mentions.notifications.MentionContentPayload;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.google.common.collect.ImmutableList;

public class MentionRecipientProvider
extends RecipientsProviderTemplate<MentionContentPayload> {
    public static final UserRole USER_ROLE = new ConfluenceUserRole("com.atlassian.confluence.plugins.mentions");

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<MentionContentPayload> mentionContentPayload) {
        return ImmutableList.of((Object)new UserKeyRoleRecipient(USER_ROLE, ((MentionContentPayload)mentionContentPayload.getPayload()).getMentionedUserKey()));
    }

    public Iterable<UserRole> getUserRoles() {
        return ImmutableList.of((Object)USER_ROLE);
    }
}

