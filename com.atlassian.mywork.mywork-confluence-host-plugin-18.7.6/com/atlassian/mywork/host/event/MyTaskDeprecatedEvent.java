/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.mywork.host.event;

import com.atlassian.sal.api.user.UserKey;

public class MyTaskDeprecatedEvent {
    private final UserKey userKey;

    public MyTaskDeprecatedEvent(UserKey userKey) {
        this.userKey = userKey;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }
}

