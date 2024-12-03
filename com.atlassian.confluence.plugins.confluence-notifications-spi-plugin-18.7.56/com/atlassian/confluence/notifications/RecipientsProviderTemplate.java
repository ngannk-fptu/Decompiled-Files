/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NonUserBasedRecipientsProviderTemplate;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.RecipientsProvider;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import java.util.Collections;

@ExperimentalApi
public abstract class RecipientsProviderTemplate<PAYLOAD extends NotificationPayload>
extends NonUserBasedRecipientsProviderTemplate<PAYLOAD>
implements RecipientsProvider<PAYLOAD> {
    @Override
    public final Iterable<RoleRecipient> userBasedRecipientsFor(Notification<PAYLOAD> notification) {
        this.verifyPayloadMatches(notification);
        return this.computeUserBasedRecipients(notification);
    }

    @Override
    protected Iterable<NotificationAddress> computeNonUserBasedRecipients(Notification<PAYLOAD> notification) {
        return Collections.EMPTY_LIST;
    }

    protected abstract Iterable<RoleRecipient> computeUserBasedRecipients(Notification<PAYLOAD> var1);
}

