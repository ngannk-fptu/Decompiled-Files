/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.auth.AuthenticationService
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.oauth2.provider.core.authentication;

import com.atlassian.bitbucket.auth.AuthenticationService;
import com.atlassian.oauth2.provider.core.authentication.LogoutHandler;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BitbucketLogoutHandler
implements LogoutHandler {
    private final AuthenticationService authenticationService;

    public BitbucketLogoutHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        this.authenticationService.clear();
        Optional.ofNullable(request.getSession(false)).ifPresent(HttpSession::invalidate);
    }
}

