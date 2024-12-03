/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;

@ExperimentalApi
public interface DispatchService {
    public void dispatch(Notification var1);

    public void dispatchWithAdditionalRecipients(Notification var1, Iterable<RoleRecipient> var2);

    public void dispatchForExclusiveRecipients(Notification var1, Iterable<RoleRecipient> var2);
}

