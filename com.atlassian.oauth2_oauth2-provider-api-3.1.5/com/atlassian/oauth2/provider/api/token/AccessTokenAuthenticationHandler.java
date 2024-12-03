/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth2.provider.api.token;

import com.atlassian.oauth2.provider.api.token.AuthenticationResult;
import com.atlassian.oauth2.provider.api.token.exception.access.AccessFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AccessTokenAuthenticationHandler {
    public AuthenticationResult authenticate(HttpServletRequest var1, HttpServletResponse var2, String var3) throws AccessFailedException;
}

