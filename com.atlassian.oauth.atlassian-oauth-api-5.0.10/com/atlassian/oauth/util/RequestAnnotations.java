/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.oauth.util;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Validate;

public class RequestAnnotations {
    private static final String OAUTH_REQUEST_FLAG = "com.atlassian.oath.request-flag";
    private static final String OAUTH_CONSUMER_KEY = "com.atlassian.oath.consumer-key";

    public static boolean isOAuthRequest(HttpServletRequest req) throws NullPointerException {
        Objects.requireNonNull(req);
        return req.getAttribute(OAUTH_REQUEST_FLAG) != null;
    }

    public static void markAsOAuthRequest(HttpServletRequest req) throws NullPointerException {
        Objects.requireNonNull(req);
        req.setAttribute(OAUTH_REQUEST_FLAG, (Object)"true");
    }

    public static String getOAuthConsumerKey(HttpServletRequest req) {
        Objects.requireNonNull(req);
        Validate.validState((boolean)RequestAnnotations.isOAuthRequest(req), (String)"cannot get OAuth consumer key out of non-OAuth request!", (Object[])new Object[0]);
        return (String)req.getAttribute(OAUTH_CONSUMER_KEY);
    }

    public static void setOAuthConsumerKey(HttpServletRequest req, String consumerKey) {
        Objects.requireNonNull(req);
        Objects.requireNonNull(consumerKey);
        req.setAttribute(OAUTH_CONSUMER_KEY, (Object)consumerKey);
    }
}

