/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.rememberme;

import com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent;
import com.atlassian.plugins.authentication.impl.web.usercontext.rememberme.RememberMeCookieHandler;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FecruComponent
public class NoopRememberMeCookieHandler
implements RememberMeCookieHandler {
    @Override
    public void refreshRememberMeCookie(HttpServletRequest request, HttpServletResponse response, Principal username) {
    }
}

