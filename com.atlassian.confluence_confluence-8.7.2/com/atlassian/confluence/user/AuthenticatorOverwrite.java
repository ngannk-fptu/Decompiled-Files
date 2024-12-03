/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.config.SecurityConfigFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.config.SecurityConfigFactory;
import java.util.HashSet;

public class AuthenticatorOverwrite {
    public static final String FLAG = "password.confirmation.disabled";
    private static final boolean customAuthenticator;

    public static boolean isCustomAuthenticator() {
        return customAuthenticator;
    }

    public static boolean isPasswordConfirmationDisabled() {
        String overrideDisable = System.getProperty(FLAG);
        if (overrideDisable != null) {
            return Boolean.parseBoolean(overrideDisable);
        }
        return customAuthenticator;
    }

    static {
        HashSet<String> supportedAuthenticators = new HashSet<String>();
        supportedAuthenticators.add("com.atlassian.confluence.user.ConfluenceAuthenticator");
        supportedAuthenticators.add("com.atlassian.confluence.user.ConfluenceGroupJoiningAuthenticator");
        supportedAuthenticators.add("com.atlassian.confluence.user.ConfluenceLDAPGroupJoiningAuthenticator");
        supportedAuthenticators.add("com.atlassian.crowd.integration.seraph.v22.ConfluenceAuthenticator");
        supportedAuthenticators.add("com.atlassian.confluence.user.ConfluenceCrowdSSOAuthenticator");
        Authenticator authenticator = SecurityConfigFactory.getInstance().getAuthenticator();
        customAuthenticator = !supportedAuthenticators.contains(authenticator.getClass().getName());
    }
}

