/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;

public interface IndividualRecipientPreferences {
    public Iterable<ServerConfiguration> getServers(UserKey var1);

    public boolean shouldSend(RoleRecipient var1, UserKey var2, ServerConfiguration var3);
}

