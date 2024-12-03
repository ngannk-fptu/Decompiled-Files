/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.Participant;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;

@ExperimentalApi
public interface RecipientsProvider<PAYLOAD extends NotificationPayload>
extends Participant<PAYLOAD> {
    public Iterable<UserRole> getUserRoles();

    public Iterable<RoleRecipient> userBasedRecipientsFor(Notification<PAYLOAD> var1);

    public Iterable<NotificationAddress> nonUserBasedRecipientsFor(Notification<PAYLOAD> var1);
}

