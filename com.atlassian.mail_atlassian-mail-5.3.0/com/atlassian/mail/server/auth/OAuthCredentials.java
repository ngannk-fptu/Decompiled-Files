/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.server.auth;

import com.atlassian.mail.server.auth.Credentials;

public interface OAuthCredentials
extends Credentials {
    public String getAccessToken();

    public String getRefreshToken();
}

