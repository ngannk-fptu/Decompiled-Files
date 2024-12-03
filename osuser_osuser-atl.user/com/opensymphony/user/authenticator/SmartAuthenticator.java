/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.user.authenticator;

import com.opensymphony.user.authenticator.AbstractAuthenticator;
import com.opensymphony.user.authenticator.AuthenticationException;
import com.opensymphony.user.authenticator.Authenticator;
import com.opensymphony.user.authenticator.jboss.JBossAuthenticator;
import com.opensymphony.user.authenticator.orion.OrionAuthenticator;
import com.opensymphony.user.authenticator.weblogic.WeblogicAuthenticator;
import com.opensymphony.util.ClassLoaderUtil;
import javax.servlet.http.HttpServletRequest;

public class SmartAuthenticator
extends AbstractAuthenticator {
    private transient Authenticator authenticator = null;

    public boolean login(String username, String password, HttpServletRequest req) throws AuthenticationException {
        if (this.authenticator == null) {
            this.authenticator = this.loadOrionAuthenticator();
        }
        if (this.authenticator == null) {
            this.authenticator = this.loadWeblogicAuthenticator();
        }
        if (this.authenticator == null) {
            this.authenticator = this.loadJBossAuthenticator();
        }
        if (this.authenticator == null) {
            throw new AuthenticationException("SmartAuthenticator could not find authenticator to load");
        }
        return this.authenticator.login(username, password, req);
    }

    private Authenticator loadJBossAuthenticator() {
        try {
            ClassLoaderUtil.loadClass((String)"org.jboss.security.AuthenticationManager", this.getClass());
            JBossAuthenticator jbossAuth = new JBossAuthenticator();
            jbossAuth.init(this.properties);
            return jbossAuth;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    private Authenticator loadOrionAuthenticator() {
        try {
            ClassLoaderUtil.loadClass((String)"com.evermind.security.RoleManager", this.getClass());
            OrionAuthenticator orionAuth = new OrionAuthenticator();
            orionAuth.init(this.properties);
            return orionAuth;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    private Authenticator loadWeblogicAuthenticator() {
        try {
            ClassLoaderUtil.loadClass((String)"weblogic.servlet.security.ServletAuthentication", this.getClass());
            WeblogicAuthenticator wlsAuth = new WeblogicAuthenticator();
            wlsAuth.init(this.properties);
            return wlsAuth;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }
}

