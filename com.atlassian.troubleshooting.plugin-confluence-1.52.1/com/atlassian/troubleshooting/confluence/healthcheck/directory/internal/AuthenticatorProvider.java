/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.config.SecurityConfigFactory
 */
package com.atlassian.troubleshooting.confluence.healthcheck.directory.internal;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.config.SecurityConfigFactory;

public class AuthenticatorProvider {
    Authenticator getAuthenticator() {
        return SecurityConfigFactory.getInstance().getAuthenticator();
    }
}

