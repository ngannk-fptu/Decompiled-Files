/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  weblogic.security.services.Authentication
 *  weblogic.servlet.security.ServletAuthentication
 */
package com.opensymphony.user.authenticator.weblogic;

import com.opensymphony.user.authenticator.AbstractAuthenticator;
import com.opensymphony.user.authenticator.AuthenticationException;
import com.opensymphony.user.authenticator.JAASCallbackHandler;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weblogic.security.services.Authentication;
import weblogic.servlet.security.ServletAuthentication;

public class WeblogicAuthenticator
extends AbstractAuthenticator {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$authenticator$weblogic$WeblogicAuthenticator == null ? (class$com$opensymphony$user$authenticator$weblogic$WeblogicAuthenticator = WeblogicAuthenticator.class$("com.opensymphony.user.authenticator.weblogic.WeblogicAuthenticator")) : class$com$opensymphony$user$authenticator$weblogic$WeblogicAuthenticator));
    static /* synthetic */ Class class$com$opensymphony$user$authenticator$weblogic$WeblogicAuthenticator;

    public boolean login(String username, String password, HttpServletRequest req) throws AuthenticationException {
        JAASCallbackHandler handler = new JAASCallbackHandler(username, password);
        try {
            Subject subject = this.properties.getProperty("realm") == null ? Authentication.login((CallbackHandler)handler) : Authentication.login((String)this.properties.getProperty("realm"), (CallbackHandler)handler);
            ServletAuthentication.runAs((Subject)subject, (HttpServletRequest)req);
        }
        catch (LoginException e) {
            log.warn((Object)("Error authenticating username " + username + ":" + e));
            return false;
        }
        return true;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

