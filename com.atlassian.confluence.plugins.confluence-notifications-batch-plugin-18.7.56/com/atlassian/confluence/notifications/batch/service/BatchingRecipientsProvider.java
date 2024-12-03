/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.RecipientsProvider
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.RecipientsProvider;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;

@ExperimentalApi
public interface BatchingRecipientsProvider<PAYLOAD extends NotificationPayload>
extends RecipientsProvider<PAYLOAD> {
    public Iterable<RoleRecipient> batchUserBasedRecipientsFor(String var1, String var2, String var3);
}

