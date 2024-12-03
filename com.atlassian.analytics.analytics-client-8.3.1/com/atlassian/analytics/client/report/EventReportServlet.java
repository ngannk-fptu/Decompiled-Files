/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.analytics.client.report;

import com.atlassian.analytics.client.LoginPageRedirector;
import com.atlassian.analytics.client.report.EventReportPermissionManager;
import com.atlassian.analytics.client.report.EventReporter;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventReportServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final EventReporter eventReporter;
    private final UserManager userManager;
    private final LoginPageRedirector loginPageRedirector;
    private final EventReportPermissionManager eventReportPermissionManager;

    public EventReportServlet(TemplateRenderer renderer, EventReporter eventReporter, UserManager userManager, LoginPageRedirector loginPageRedirector, EventReportPermissionManager eventReportPermissionManager) {
        this.renderer = renderer;
        this.eventReporter = eventReporter;
        this.userManager = userManager;
        this.loginPageRedirector = loginPageRedirector;
        this.eventReportPermissionManager = eventReportPermissionManager;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.eventReportPermissionManager.hasPermission(this.userManager.getRemoteUserKey(request))) {
            this.loginPageRedirector.redirectToLogin(request, response);
            return;
        }
        Map<String, Boolean> context = Collections.singletonMap("capturing", this.eventReporter.isCapturing());
        response.setContentType("text/html; charset=UTF-8");
        this.renderer.render("templates/event-report.vm", context, (Writer)response.getWriter());
    }
}

