/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mobile.analytic;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class MobileAgent {
    public static final String DARWIN = "darwin";
    public static final String IOS_AGENT = "ios";
    public static final String UNKNOWN_AGENT = "unknown";
    private final String userAgent;

    public MobileAgent(HttpServletRequest request) {
        this.userAgent = StringUtils.lowerCase((String)request.getHeader("User-Agent"));
    }

    public boolean isIos() {
        return this.userAgent.contains(DARWIN);
    }

    public String getAgent() {
        if (this.isIos()) {
            return IOS_AGENT;
        }
        return UNKNOWN_AGENT;
    }
}

