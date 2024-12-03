/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.event.events.security.LoginDetails;
import com.atlassian.confluence.event.events.security.SecurityEvent;
import javax.annotation.Nonnull;

public class LoginFailedEvent
extends SecurityEvent {
    private static final long serialVersionUID = 3086117026345925153L;
    private final LoginDetails loginDetails;

    @Deprecated
    public LoginFailedEvent(Object src, String username, String sessionId, String remoteHost, String remoteIP) {
        this(src, username, sessionId, remoteHost, remoteIP, new LoginDetails(LoginDetails.LoginSource.UNKNOWN, LoginDetails.CaptchaState.NOT_SHOWN));
    }

    public LoginFailedEvent(Object src, String username, String sessionId, String remoteHost, String remoteIP, @Nonnull LoginDetails loginDetails) {
        super(src, username, sessionId, remoteHost, remoteIP);
        this.loginDetails = loginDetails;
    }

    public LoginDetails getLoginDetails() {
        return this.loginDetails;
    }
}

