/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.event.events.security.LoginDetails;
import com.atlassian.confluence.event.events.security.SecurityEvent;
import java.util.Optional;
import javax.annotation.Nonnull;

public class LoginEvent
extends SecurityEvent {
    public static final String DIRECT = "direct";
    public static final String COOKIE = "cookie";
    public static final String CROWD = "crowd";
    public static final String UNKNOWN = "unknown";
    private static final long serialVersionUID = -1133034636563265726L;
    private final LoginDetails loginDetails;
    private String loginSource;

    @Deprecated
    public LoginEvent(Object src, String username, String sessionId, String remoteHost, String remoteIP, String loginSource) {
        this(src, username, sessionId, remoteHost, remoteIP, new LoginDetails(LoginDetails.LoginSource.UNKNOWN, null));
        this.loginSource = Optional.ofNullable(loginSource).orElse(LoginDetails.LoginSource.UNKNOWN.name().toLowerCase());
    }

    public LoginEvent(Object src, String username, String sessionId, String remoteHost, String remoteIP, @Nonnull LoginDetails loginDetails) {
        super(src, username, sessionId, remoteHost, remoteIP);
        this.loginSource = loginDetails.getLoginSource().toString().toLowerCase();
        this.loginDetails = loginDetails;
    }

    public String getLoginSource() {
        return this.loginSource;
    }

    public LoginDetails getLoginDetails() {
        return this.loginDetails;
    }
}

