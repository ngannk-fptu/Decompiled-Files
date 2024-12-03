/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.applinks.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.OAuth2ConnectionType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import java.net.URI;

@PublicApi
public interface ReadOnlyApplicationLink {
    public ApplicationId getId();

    public ApplicationType getType();

    public String getName();

    public URI getDisplayUrl();

    public URI getRpcUrl();

    public boolean isPrimary();

    public boolean isSystem();

    default public OAuth2ConnectionType getOAuth2ConnectionType() {
        return null;
    }

    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory();

    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory(Class<? extends AuthenticationProvider> var1);

    public ApplicationLinkRequestFactory createImpersonatingAuthenticatedRequestFactory();

    public ApplicationLinkRequestFactory createNonImpersonatingAuthenticatedRequestFactory();
}

