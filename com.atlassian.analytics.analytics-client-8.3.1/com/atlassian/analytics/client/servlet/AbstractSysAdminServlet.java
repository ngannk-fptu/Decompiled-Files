/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.analytics.client.servlet;

import com.atlassian.analytics.client.LoginPageRedirector;
import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractSysAdminServlet
extends HttpServlet {
    private final WebSudoManager webSudoManager;
    private final LoginPageRedirector loginPageRedirector;
    private final UserPermissionsHelper userPermissionsHelper;

    public AbstractSysAdminServlet(WebSudoManager webSudoManager, LoginPageRedirector loginPageRedirector, UserPermissionsHelper userPermissionsHelper) {
        this.webSudoManager = webSudoManager;
        this.loginPageRedirector = loginPageRedirector;
        this.userPermissionsHelper = userPermissionsHelper;
    }

    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!this.userPermissionsHelper.isRequestUserSystemAdmin(request)) {
            this.loginPageRedirector.redirectToLogin(request, response);
            return;
        }
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            this.doRestrictedGet(request, response);
        }
        catch (WebSudoSessionException wes) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected void doRestrictedGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }
}

