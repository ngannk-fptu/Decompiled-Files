/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.analytics.client.checklist;

import com.atlassian.analytics.client.LoginPageRedirector;
import com.atlassian.analytics.client.report.EventReportPermissionManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventChecklistServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final UserManager userManager;
    private final LoginPageRedirector loginPageRedirector;
    private final EventReportPermissionManager eventReportPermissionManager;

    public EventChecklistServlet(TemplateRenderer renderer, UserManager userManager, LoginPageRedirector loginPageRedirector, EventReportPermissionManager eventReportPermissionManager) {
        this.renderer = renderer;
        this.userManager = userManager;
        this.loginPageRedirector = loginPageRedirector;
        this.eventReportPermissionManager = eventReportPermissionManager;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.eventReportPermissionManager.hasPermission(this.userManager.getRemoteUserKey(request))) {
            this.loginPageRedirector.redirectToLogin(request, response);
            return;
        }
        ImmutableMap context = ImmutableMap.of();
        this.renderer.render("templates/event-checklist.vm", (Map)context, (Writer)response.getWriter());
    }
}

