/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth2.provider.core.authentication;

import com.atlassian.oauth2.provider.core.authentication.LogoutException;
import com.atlassian.oauth2.provider.core.authentication.LogoutHandler;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfigFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SeraphLogoutHandler
implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws LogoutException {
        try {
            SecurityConfigFactory.getInstance().getAuthenticator().logout(request, response);
        }
        catch (AuthenticatorException e) {
            throw new LogoutException(e);
        }
    }
}

