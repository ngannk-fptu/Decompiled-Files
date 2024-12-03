/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 */
package com.atlassian.confluence.notifications;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.ParticipantTemplate;
import com.atlassian.confluence.notifications.RecipientsProvider;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import java.util.Collections;

public abstract class NonUserBasedRecipientsProviderTemplate<PAYLOAD extends NotificationPayload>
extends ParticipantTemplate<PAYLOAD>
implements RecipientsProvider<PAYLOAD> {
    @Override
    public Iterable<NotificationAddress> nonUserBasedRecipientsFor(Notification<PAYLOAD> notification) {
        this.verifyPayloadMatches(notification);
        return this.computeNonUserBasedRecipients(notification);
    }

    @Override
    public Iterable<RoleRecipient> userBasedRecipientsFor(Notification<PAYLOAD> notification) {
        return Collections.emptyList();
    }

    protected abstract Iterable<NotificationAddress> computeNonUserBasedRecipients(Notification<PAYLOAD> var1);
}

