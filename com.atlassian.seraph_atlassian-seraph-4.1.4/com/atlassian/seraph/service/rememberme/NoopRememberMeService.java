/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.seraph.service.rememberme;

import com.atlassian.seraph.service.rememberme.RememberMeService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoopRememberMeService
implements RememberMeService {
    public static final NoopRememberMeService INSTANCE = new NoopRememberMeService();

    private NoopRememberMeService() {
    }

    @Override
    public void addRememberMeCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String authenticatedUsername) {
    }

    @Override
    public void removeRememberMeCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    }

    @Override
    public String getRememberMeCookieAuthenticatedUsername(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return null;
    }
}

