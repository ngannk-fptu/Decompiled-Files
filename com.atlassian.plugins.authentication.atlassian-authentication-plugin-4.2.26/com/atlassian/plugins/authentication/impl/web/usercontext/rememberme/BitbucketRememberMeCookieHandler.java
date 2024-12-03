/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.auth.RememberMeService
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.BitbucketImport
 *  javax.inject.Inject
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.rememberme;

import com.atlassian.bitbucket.auth.RememberMeService;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.BitbucketImport;
import com.atlassian.plugins.authentication.impl.web.usercontext.rememberme.RememberMeCookieHandler;
import java.security.Principal;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@BitbucketComponent
public class BitbucketRememberMeCookieHandler
implements RememberMeCookieHandler {
    private final RememberMeService rememberMeService;

    @Inject
    public BitbucketRememberMeCookieHandler(@BitbucketImport RememberMeService rememberMeService) {
        this.rememberMeService = rememberMeService;
    }

    @Override
    public void refreshRememberMeCookie(HttpServletRequest request, HttpServletResponse response, Principal username) {
        this.rememberMeService.createCookie(request, response);
    }
}

