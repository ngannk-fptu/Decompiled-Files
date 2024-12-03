/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.rememberme;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RememberMeCookieHandler {
    public void refreshRememberMeCookie(HttpServletRequest var1, HttpServletResponse var2, Principal var3);
}

