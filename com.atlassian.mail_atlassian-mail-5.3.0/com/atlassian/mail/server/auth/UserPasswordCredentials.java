/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.server.auth;

import com.atlassian.mail.server.auth.Credentials;

public interface UserPasswordCredentials
extends Credentials {
    public String getUserName();

    public String getPassword();
}

