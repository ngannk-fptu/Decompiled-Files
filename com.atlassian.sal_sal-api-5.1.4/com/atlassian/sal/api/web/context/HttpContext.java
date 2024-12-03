/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.sal.api.web.context;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface HttpContext {
    @Nullable
    public HttpServletRequest getRequest();

    @Nullable
    public HttpServletResponse getResponse();

    @Nullable
    public HttpSession getSession(boolean var1);
}

