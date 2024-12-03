/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.gadgets.directory.internal.admin;

import com.atlassian.gadgets.directory.internal.admin.AdminPageController;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GadgetDirectoryAdminServlet
extends HttpServlet {
    private final WebSudoManager webSudoManager;
    private final UserManager userManager;
    private final AdminPageController pageController;
    private final WebResourceManager webResourceManager;

    public GadgetDirectoryAdminServlet(@ComponentImport WebSudoManager webSudoManager, @ComponentImport UserManager userManager, AdminPageController pageController, @ComponentImport WebResourceManager webResourceManager) {
        this.webSudoManager = webSudoManager;
        this.userManager = userManager;
        this.pageController = pageController;
        this.webResourceManager = webResourceManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            this.doRender(resp);
        }
        catch (WebSudoSessionException ex) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    private void doRender(HttpServletResponse resp) throws IOException {
        PrintWriter responseWriter = resp.getWriter();
        if (this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey())) {
            this.webResourceManager.requireResource("com.atlassian.gadgets.directory:gadget-directory-admin-client-main");
            this.pageController.renderAdminPage(responseWriter);
        } else {
            this.pageController.renderErrorPage(responseWriter);
        }
    }
}

