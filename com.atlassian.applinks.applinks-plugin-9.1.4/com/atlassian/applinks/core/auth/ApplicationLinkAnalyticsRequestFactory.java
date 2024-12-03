/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.net.Request$MethodType
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.core.auth.ApplicationLinkAnalyticsRequest;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.net.Request;
import java.net.URI;
import java.util.Objects;

class ApplicationLinkAnalyticsRequestFactory
implements ApplicationLinkRequestFactory {
    protected final ApplicationLinkRequestFactory wrappedFactory;
    private final ApplicationLink remoteApplicationLink;
    private final EventPublisher publisher;

    ApplicationLinkAnalyticsRequestFactory(ApplicationLinkRequestFactory wrappedFactory, ApplicationLink remoteApplicationLink, EventPublisher publisher) {
        this.wrappedFactory = Objects.requireNonNull(wrappedFactory);
        this.remoteApplicationLink = Objects.requireNonNull(remoteApplicationLink);
        this.publisher = Objects.requireNonNull(publisher);
    }

    public ApplicationLinkRequest createRequest(Request.MethodType methodType, String url) throws CredentialsRequiredException {
        return new ApplicationLinkAnalyticsRequest(this.wrappedFactory.createRequest(methodType, url), this.remoteApplicationLink, this.publisher);
    }

    public URI getAuthorisationURI(URI callback) {
        return this.wrappedFactory.getAuthorisationURI(callback);
    }

    public URI getAuthorisationURI() {
        return this.wrappedFactory.getAuthorisationURI();
    }
}

