/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.evermind.security.RoleManager
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.user.authenticator.orion;

import com.evermind.security.RoleManager;
import com.opensymphony.user.authenticator.AbstractAuthenticator;
import com.opensymphony.user.authenticator.AuthenticationException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

public class OrionAuthenticator
extends AbstractAuthenticator {
    public boolean login(String username, String password, HttpServletRequest req) throws AuthenticationException {
        try {
            RoleManager roleManager = (RoleManager)new InitialContext().lookup("java:comp/RoleManager");
            try {
                roleManager.login(username, password);
            }
            catch (SecurityException e) {
                return false;
            }
        }
        catch (NamingException e) {
            throw new AuthenticationException("Could not lookup RoleManager. Exception: " + e);
        }
        return true;
    }
}

