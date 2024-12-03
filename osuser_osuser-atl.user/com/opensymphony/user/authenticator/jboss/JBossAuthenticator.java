/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.user.authenticator.jboss;

import com.opensymphony.user.authenticator.AbstractAuthenticator;
import com.opensymphony.user.authenticator.AuthenticationException;
import com.opensymphony.user.authenticator.JAASCallbackHandler;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

public class JBossAuthenticator
extends AbstractAuthenticator {
    public boolean login(String username, String password, HttpServletRequest req) throws AuthenticationException {
        JAASCallbackHandler handler = new JAASCallbackHandler(username, password);
        try {
            LoginContext lc = new LoginContext("osuser", handler);
            lc.login();
            Subject subject = lc.getSubject();
        }
        catch (LoginException e) {
            return false;
        }
        return true;
    }
}

