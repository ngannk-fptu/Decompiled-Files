/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.seraph.util.RedirectUtils
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.Johnson;
import com.atlassian.seraph.util.RedirectUtils;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JohnsonDismissEventsServlet
extends HttpServlet {
    public static final String URL_SUFFIX = "/johnson/events/dismiss";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith(URL_SUFFIX)) {
            if (this.notLoggedIn()) {
                String loginUrl = new UrlBuilder(RedirectUtils.getLoginUrl((HttpServletRequest)request)).toString();
                response.sendRedirect(loginUrl);
            } else if (this.canDismissEvents()) {
                JohnsonUtils.dismissEvents();
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                response.sendRedirect(request.getContextPath() + Johnson.getConfig().getErrorPath() + "#no-admin");
            }
        } else {
            response.setStatus(404);
        }
    }

    private PermissionManager getPermissionManager() {
        return (PermissionManager)ContainerManager.getComponent((String)"permissionManager", PermissionManager.class);
    }

    private boolean notLoggedIn() {
        return AuthenticatedUserThreadLocal.get() == null;
    }

    private boolean canDismissEvents() {
        ConfluenceUser requestingUser = AuthenticatedUserThreadLocal.get();
        return this.getPermissionManager().isSystemAdministrator(requestingUser) && JohnsonUtils.allEventsDismissible();
    }
}

