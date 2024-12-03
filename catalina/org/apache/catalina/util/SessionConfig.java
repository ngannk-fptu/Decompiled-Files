/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.SessionCookieConfig
 */
package org.apache.catalina.util;

import javax.servlet.SessionCookieConfig;
import org.apache.catalina.Context;

public class SessionConfig {
    private static final String DEFAULT_SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String DEFAULT_SESSION_PARAMETER_NAME = "jsessionid";

    public static String getSessionCookieName(Context context) {
        return SessionConfig.getConfiguredSessionCookieName(context, DEFAULT_SESSION_COOKIE_NAME);
    }

    public static String getSessionUriParamName(Context context) {
        return SessionConfig.getConfiguredSessionCookieName(context, DEFAULT_SESSION_PARAMETER_NAME);
    }

    private static String getConfiguredSessionCookieName(Context context, String defaultName) {
        if (context != null) {
            String cookieName = context.getSessionCookieName();
            if (cookieName != null && cookieName.length() > 0) {
                return cookieName;
            }
            SessionCookieConfig scc = context.getServletContext().getSessionCookieConfig();
            cookieName = scc.getName();
            if (cookieName != null && cookieName.length() > 0) {
                return cookieName;
            }
        }
        return defaultName;
    }

    public static String getSessionCookiePath(Context context) {
        SessionCookieConfig scc = context.getServletContext().getSessionCookieConfig();
        String contextPath = context.getSessionCookiePath();
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = scc.getPath();
        }
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = context.getEncodedPath();
        }
        if (context.getSessionCookiePathUsesTrailingSlash()) {
            if (!contextPath.endsWith("/")) {
                contextPath = contextPath + "/";
            }
        } else if (contextPath.length() == 0) {
            contextPath = "/";
        }
        return contextPath;
    }

    private SessionConfig() {
    }
}

