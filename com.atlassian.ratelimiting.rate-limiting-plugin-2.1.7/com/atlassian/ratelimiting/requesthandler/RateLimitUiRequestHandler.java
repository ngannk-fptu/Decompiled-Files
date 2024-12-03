/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.ratelimiting.requesthandler;

import javax.servlet.http.HttpServletRequest;

public interface RateLimitUiRequestHandler {
    public boolean isUiRequest(HttpServletRequest var1);

    public void logRequestInfo(HttpServletRequest var1);
}

