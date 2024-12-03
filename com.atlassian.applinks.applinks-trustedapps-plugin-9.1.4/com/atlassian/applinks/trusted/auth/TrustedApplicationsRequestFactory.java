/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.security.auth.trustedapps.CurrentApplication
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.trusted.auth.TrustedRequest;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import java.net.URI;
import java.util.Objects;

public class TrustedApplicationsRequestFactory
implements ApplicationLinkRequestFactory {
    private final CurrentApplication currentApplication;
    private final RequestFactory requestFactory;
    private final UserManager userManager;

    public TrustedApplicationsRequestFactory(CurrentApplication currentApplication, RequestFactory requestFactory, UserManager userManager) {
        this.currentApplication = Objects.requireNonNull(currentApplication, "currentApplication");
        this.requestFactory = Objects.requireNonNull(requestFactory, "requestFactory");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    public ApplicationLinkRequest createRequest(Request.MethodType methodType, String url) {
        Request request = this.requestFactory.createRequest(methodType, url);
        String username = Objects.requireNonNull(this.userManager.getRemoteUsername(), "You have to be logged in to use trusted authentication.");
        return new TrustedRequest(url, request, this.currentApplication, username);
    }

    public URI getAuthorisationURI(URI callback) {
        return null;
    }

    public URI getAuthorisationURI() {
        return null;
    }
}

