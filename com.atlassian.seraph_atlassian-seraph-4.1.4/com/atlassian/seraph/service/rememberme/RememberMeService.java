/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.seraph.service.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RememberMeService {
    public String getRememberMeCookieAuthenticatedUsername(HttpServletRequest var1, HttpServletResponse var2);

    public void addRememberMeCookie(HttpServletRequest var1, HttpServletResponse var2, String var3);

    public void removeRememberMeCookie(HttpServletRequest var1, HttpServletResponse var2);
}

