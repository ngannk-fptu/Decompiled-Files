/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfig;
import java.io.Serializable;
import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAuthenticator
implements Authenticator,
Serializable {
    private SecurityConfig config;

    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
        this.config = config;
    }

    @Override
    public void destroy() {
    }

    @Override
    public String getRemoteUser(HttpServletRequest request) {
        Principal user = this.getUser(request);
        if (user == null) {
            return null;
        }
        return user.getName();
    }

    @Override
    public Principal getUser(HttpServletRequest request) {
        return this.getUser(request, null);
    }

    @Override
    public abstract Principal getUser(HttpServletRequest var1, HttpServletResponse var2);

    @Override
    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticatorException {
        return this.login(request, response, username, password, false);
    }

    @Override
    public abstract boolean login(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4, boolean var5) throws AuthenticatorException;

    @Override
    public abstract boolean logout(HttpServletRequest var1, HttpServletResponse var2) throws AuthenticatorException;

    protected SecurityConfig getConfig() {
        return this.config;
    }
}

