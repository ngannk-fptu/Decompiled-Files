/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.ratelimiting.requesthandler;

import javax.servlet.http.HttpServletRequest;

public interface RateLimitUserRequestHandler {
    public boolean shouldApplyRateLimiting(HttpServletRequest var1);
}

