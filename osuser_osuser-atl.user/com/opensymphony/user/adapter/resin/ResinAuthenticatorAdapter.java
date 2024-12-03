/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.caucho.http.security.AbstractAuthenticator
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.opensymphony.user.adapter.resin;

import com.caucho.http.security.AbstractAuthenticator;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import java.security.Principal;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResinAuthenticatorAdapter
extends AbstractAuthenticator {
    private UserManager userManager;

    public boolean isUserInRole(HttpServletRequest request, HttpServletResponse response, ServletContext context, Principal principal, String s) throws ServletException {
        return super.isUserInRole(request, response, context, principal, s);
    }

    public void init() throws ServletException {
        super.init();
        this.userManager = UserManager.getInstance();
    }

    protected Principal getUserPrincipalImpl(HttpServletRequest request, ServletContext context) throws ServletException {
        return null;
    }

    protected Principal loginImpl(String username, String password) {
        User user = null;
        try {
            user = this.userManager.getUser(username);
        }
        catch (EntityNotFoundException e) {
            return null;
        }
        if (user.authenticate(password)) {
            return user;
        }
        return null;
    }

    protected Principal loginImpl(HttpServletRequest request, HttpServletResponse response, ServletContext context, String s, String s1) throws ServletException {
        return this.loginImpl(s, s1);
    }
}

