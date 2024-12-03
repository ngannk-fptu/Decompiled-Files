/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.applinks.ui.auth;

import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import javax.servlet.http.HttpServletRequest;

class ServletSessionHandler
implements AdminUIAuthenticator.SessionHandler {
    private final HttpServletRequest request;

    public ServletSessionHandler(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void set(String key, Object value) {
        this.request.getSession().setAttribute(key, value);
    }

    @Override
    public Object get(String key) {
        return this.request.getSession().getAttribute(key);
    }
}

