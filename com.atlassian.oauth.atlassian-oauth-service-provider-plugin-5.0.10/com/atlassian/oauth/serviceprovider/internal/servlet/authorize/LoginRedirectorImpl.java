/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.authorize;

import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.LoginRedirector;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class LoginRedirectorImpl
implements LoginRedirector {
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;

    public LoginRedirectorImpl(UserManager userManager, LoginUriProvider loginUriProvider) {
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.loginUriProvider = Objects.requireNonNull(loginUriProvider, "loginUriProvider");
    }

    @Override
    public boolean isLoggedIn(HttpServletRequest request) {
        return this.userManager.getRemoteUsername(request) != null;
    }

    @Override
    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(this.loginUriProvider.getLoginUri(this.getUri(request)).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}

