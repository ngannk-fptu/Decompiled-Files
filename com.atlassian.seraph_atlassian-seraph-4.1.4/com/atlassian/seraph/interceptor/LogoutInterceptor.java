/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.seraph.interceptor;

import com.atlassian.seraph.interceptor.Interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogoutInterceptor
extends Interceptor {
    public void beforeLogout(HttpServletRequest var1, HttpServletResponse var2);

    public void afterLogout(HttpServletRequest var1, HttpServletResponse var2);
}

