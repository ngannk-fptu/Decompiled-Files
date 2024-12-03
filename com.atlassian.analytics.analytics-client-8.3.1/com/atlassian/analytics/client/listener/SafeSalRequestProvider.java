/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.web.context.HttpContext
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.analytics.client.listener;

import com.atlassian.sal.api.web.context.HttpContext;
import javax.servlet.http.HttpServletRequest;

public class SafeSalRequestProvider {
    private final HttpContext context;

    public SafeSalRequestProvider(HttpContext context) {
        this.context = context;
    }

    public HttpServletRequest getHttpRequest() {
        try {
            return this.context.getRequest();
        }
        catch (IllegalStateException e) {
            return null;
        }
    }
}

