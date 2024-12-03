/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.annotations.Internal;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Internal
public final class ApplinksOAuth {
    public static final String WWW_AUTHENTICATE = String.valueOf("WWW-Authenticate");
    public static final String OAUTH = String.valueOf("OAuth");
    public static final String AUTH_CONFIG_CONSUMER_KEY_OUTBOUND = String.valueOf("consumerKey.outbound");
    public static final String PROPERTY_INCOMING_CONSUMER_KEY = String.valueOf("oauth.incoming.consumerkey");
    public static final String SERVICE_PROVIDER_REQUEST_TOKEN_URL = String.valueOf("serviceProvider.requestTokenUrl");
    public static final String SERVICE_PROVIDER_ACCESS_TOKEN_URL = String.valueOf("serviceProvider.accessTokenUrl");
    public static final String SERVICE_PROVIDER_AUTHORIZE_URL = String.valueOf("serviceProvider.authorizeUrl");
    public static final String OAUTH_PROBLEM = String.valueOf("oauth_problem");
    public static final String PROBLEM_CONSUMER_KEY_UNKNOWN = String.valueOf("consumer_key_unknown");
    public static final String PROBLEM_TIMESTAMP_REFUSED = String.valueOf("timestamp_refused");
    public static final String PROBLEM_SIGNATURE_INVALID = String.valueOf("signature_invalid");

    private ApplinksOAuth() {
    }

    public static boolean hasOAuthChallenge(@Nonnull Response response) {
        return response.getHeaders().containsKey(WWW_AUTHENTICATE) && response.getHeader(WWW_AUTHENTICATE).trim().startsWith(OAUTH);
    }

    public static boolean isAuthLevelDisabled(@Nonnull Response response) throws ResponseException {
        return Response.Status.UNAUTHORIZED.getStatusCode() == response.getStatusCode() && ApplinksOAuth.hasOAuthChallenge(response) && StringUtils.isEmpty((CharSequence)response.getResponseBodyAsString());
    }
}

