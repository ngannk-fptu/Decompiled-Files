/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.emailgateway.events;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.sal.api.user.UserKey;

public class EmailThreadStagedForUserEvent
implements NotificationEnabledEvent {
    private final StagedEmailThread emailThread;
    private final UserKey userKey;
    private final boolean error;

    public EmailThreadStagedForUserEvent(StagedEmailThread emailThread, UserKey userKey) {
        this(emailThread, userKey, false);
    }

    public EmailThreadStagedForUserEvent(StagedEmailThread emailThread, UserKey userKey, boolean error) {
        this.emailThread = emailThread;
        this.userKey = userKey;
        this.error = error;
    }

    public StagedEmailThread getEmailThread() {
        return this.emailThread;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isSuppressNotifications() {
        return false;
    }
}

