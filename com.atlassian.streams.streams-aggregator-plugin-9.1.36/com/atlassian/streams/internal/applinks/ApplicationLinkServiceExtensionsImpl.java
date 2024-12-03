/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.streams.internal.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.net.Request;
import com.atlassian.streams.internal.applinks.ApplicationLinkServiceExtensions;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

public class ApplicationLinkServiceExtensionsImpl
implements ApplicationLinkServiceExtensions {
    private static final String DEFAULT_TOKEN_ADMIN_PATH = "/plugins/servlet/oauth/users/access-tokens";
    private static final String CONFLUENCE_TOKEN_ADMIN_PATH = "/users/revokeoauthtokens.action";
    private static final String CONFLUENCE_TYPE_KEY = "applinks.confluence";
    private static final String COMPLETION_SERVLET_PATH = "/plugins/servlet/streams/applinks/oauth/completion";
    private final ApplicationProperties applicationProperties;

    public ApplicationLinkServiceExtensionsImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
    }

    @Override
    public boolean isAuthorised(ApplicationLink appLink) {
        ApplicationLinkRequestFactory arf = appLink.createAuthenticatedRequestFactory();
        try {
            arf.createRequest(Request.MethodType.GET, appLink.getDisplayUrl().toString());
            return true;
        }
        catch (CredentialsRequiredException e) {
            return false;
        }
    }

    @Override
    public URI getUserAdminUri(ApplicationLink appLink) {
        String uriPath = appLink.getType().getI18nKey().equals(CONFLUENCE_TYPE_KEY) ? CONFLUENCE_TOKEN_ADMIN_PATH : DEFAULT_TOKEN_ADMIN_PATH;
        return UriBuilder.fromUri((URI)appLink.getDisplayUrl()).path(uriPath).build(new Object[0]);
    }

    @Override
    public URI getAuthCallbackUri(ApplicationLink appLink) {
        return UriBuilder.fromUri((String)this.applicationProperties.getBaseUrl()).path(COMPLETION_SERVLET_PATH).queryParam("applinkId", new Object[]{appLink.getId()}).build(new Object[0]);
    }
}

