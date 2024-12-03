/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.ui.admin;

import com.atlassian.confluence.plugins.collaborative.content.feedback.service.PermissionService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.user.User;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminServlet
extends HttpServlet {
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final PermissionService permissionService;
    private final WebSudoManager webSudoManager;

    @Autowired
    public AdminServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport WebSudoManager webSudoManager, PermissionService permissionService) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.webSudoManager = webSudoManager;
        this.permissionService = permissionService;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws AuthorisationException, IOException {
        this.permissionService.enforceSysAdmin((User)AuthenticatedUserThreadLocal.get());
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            resp.setContentType("text/html; charset=UTF-8");
            this.soyTemplateRenderer.render((Appendable)resp.getWriter(), "com.atlassian.confluence.plugins.collaborative-editing-feedback-plugin:collaborative-editing-feedback-plugin-admin-template", "Atlassian.Confluence.Plugins.Collaborative.Editing.Feedback.Templates.admin", Collections.emptyMap());
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }
}

