/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 */
package com.atlassian.applinks.api.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.sal.api.net.Request;

public final class Anonymous
implements AuthenticationProvider {
    public static Request createAnonymousRequest(ApplicationLink link, Request.MethodType methodType, String url) {
        try {
            return link.createAuthenticatedRequestFactory(Anonymous.class).createRequest(methodType, url);
        }
        catch (CredentialsRequiredException e) {
            throw new RuntimeException("Unexpected CredentialsRequiredException encountered for Anonymous AuthenticationProvider", e);
        }
    }
}

