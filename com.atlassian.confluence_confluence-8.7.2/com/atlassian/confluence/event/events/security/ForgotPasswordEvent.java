/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.user.ConfluenceUser;

public class ForgotPasswordEvent
implements NotificationEnabledEvent {
    private final ConfluenceUser user;
    private final String resetPasswordLink;
    private final String changePasswordRequestLink;

    public ForgotPasswordEvent(ConfluenceUser user, String resetPasswordLink, String changePasswordRequestLink) {
        this.user = user;
        this.resetPasswordLink = resetPasswordLink;
        this.changePasswordRequestLink = changePasswordRequestLink;
    }

    public String getResetPasswordLink() {
        return this.resetPasswordLink;
    }

    public String getChangePasswordRequestLink() {
        return this.changePasswordRequestLink;
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public final boolean isSuppressNotifications() {
        return false;
    }
}

