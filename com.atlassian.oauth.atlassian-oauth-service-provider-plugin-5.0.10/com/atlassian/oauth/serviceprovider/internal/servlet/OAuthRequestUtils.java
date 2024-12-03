/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  net.oauth.OAuth$Parameter
 *  net.oauth.server.HttpRequestMessage
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import net.oauth.OAuth;
import net.oauth.server.HttpRequestMessage;

public final class OAuthRequestUtils {
    static final Set<String> OAUTH_DATA_REQUEST_PARAMS = Stream.of("oauth_consumer_key", "oauth_token", "oauth_signature_method", "oauth_signature", "oauth_timestamp", "oauth_nonce").collect(Collectors.toSet());

    private OAuthRequestUtils() {
    }

    public static boolean isOAuthAccessAttempt(HttpServletRequest request) {
        return OAuthRequestUtils.is3LOAuthAccessAttempt(request) || OAuthRequestUtils.is2LOAuthAccessAttempt(request);
    }

    public static boolean is2LOAuthAccessAttempt(HttpServletRequest request) {
        Map<String, String> params = OAuthRequestUtils.extractParameters(request);
        return params.keySet().containsAll(OAUTH_DATA_REQUEST_PARAMS) && "".equals(params.get("oauth_token")) && !OAuthRequestUtils.isRequestTokenRequest(request);
    }

    public static boolean is3LOAuthAccessAttempt(HttpServletRequest request) {
        Map<String, String> params = OAuthRequestUtils.extractParameters(request);
        return params.keySet().containsAll(OAUTH_DATA_REQUEST_PARAMS) && params.containsKey("oauth_token") && !"".equals(params.get("oauth_token")) && !OAuthRequestUtils.isAccessTokenRequest(request);
    }

    private static boolean isRequestTokenRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith("/plugins/servlet/oauth/request-token");
    }

    private static boolean isAccessTokenRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith("/plugins/servlet/oauth/access-token");
    }

    private static Map<String, String> extractParameters(HttpServletRequest request) {
        HashMap<String, String> params = new HashMap<String, String>();
        for (OAuth.Parameter param : HttpRequestMessage.getParameters((HttpServletRequest)request)) {
            params.put(param.getKey(), param.getValue());
        }
        return Collections.unmodifiableMap(params);
    }
}

