/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.api.medium.recipient;

import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;

public interface RoleRecipient {
    public UserRole getRole();

    public UserKey getUserKey();

    public boolean shouldOverrideSendingOwnEventNotifications();
}

