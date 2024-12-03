/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.user;

public class UserEmailSSOAuthenticationFailedEvent {
    private final Reason reason;

    public UserEmailSSOAuthenticationFailedEvent(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }

    public static enum Reason {
        EMAIL_DUPLICATED,
        EMAIL_INVALID;

    }
}

