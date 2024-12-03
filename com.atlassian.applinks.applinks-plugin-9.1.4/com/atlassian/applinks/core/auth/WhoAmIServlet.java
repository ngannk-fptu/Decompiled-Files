/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WhoAmIServlet
extends HttpServlet {
    private final UserManager userManager;

    public WhoAmIServlet(UserManager userManager) {
        this.userManager = userManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String username = this.userManager.getRemoteUsername(request);
        if (username != null) {
            response.getWriter().print(username);
        }
    }
}

