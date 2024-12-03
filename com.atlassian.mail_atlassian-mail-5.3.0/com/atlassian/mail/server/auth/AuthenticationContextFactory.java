/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.server.auth;

import com.atlassian.mail.server.auth.AuthenticationContext;
import com.atlassian.mail.server.auth.Credentials;

public interface AuthenticationContextFactory {
    public AuthenticationContext createAuthenticationContext(Credentials var1);
}

