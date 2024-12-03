/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.Internal;
import com.atlassian.sal.api.user.UserKey;

@Internal
public interface UserNotificationsDefaultsService {
    public void applyDefaultsForUser(UserKey var1);

    public boolean isUserSettingsDefaults(UserKey var1);
}

