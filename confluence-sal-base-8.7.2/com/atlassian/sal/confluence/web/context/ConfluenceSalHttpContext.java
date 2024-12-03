/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.sal.api.web.context.HttpContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.sal.confluence.web.context;

import com.atlassian.sal.api.web.context.HttpContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConfluenceSalHttpContext
implements HttpContext {
    private final com.atlassian.confluence.web.context.HttpContext httpContext;

    public ConfluenceSalHttpContext(com.atlassian.confluence.web.context.HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public HttpServletRequest getRequest() {
        return this.httpContext.getRequest();
    }

    public HttpServletResponse getResponse() {
        return this.httpContext.getResponse();
    }

    public HttpSession getSession(boolean create) {
        return this.httpContext.getSession(create);
    }
}

