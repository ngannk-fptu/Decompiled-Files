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

public interface LoginInterceptor
extends Interceptor {
    public void beforeLogin(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4, boolean var5);

    public void afterLogin(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4, boolean var5, String var6);
}

