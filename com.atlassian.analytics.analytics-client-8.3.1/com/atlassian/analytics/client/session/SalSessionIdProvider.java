/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.web.context.HttpContext
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.analytics.client.session;

import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.sal.api.web.context.HttpContext;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

public class SalSessionIdProvider
implements SessionIdProvider {
    private final HttpContext httpContext;

    public SalSessionIdProvider(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    @Nullable
    public String getSessionId() {
        HttpSession session = this.httpContext.getSession(false);
        if (session != null) {
            return session.getId();
        }
        return null;
    }
}

