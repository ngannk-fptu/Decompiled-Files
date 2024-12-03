/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.notification;

import com.atlassian.sal.api.user.UserKey;

public class DismissedState {
    private final UserKey userKey;
    private final boolean dismissed;

    public DismissedState(UserKey userKey, boolean dismissed) {
        this.userKey = userKey;
        this.dismissed = dismissed;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public boolean isDismissed() {
        return this.dismissed;
    }
}

