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
import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.report.EventReportPermissionManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WhitelistReportServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final UserManager userManager;
    private final LoginPageRedirector loginPageRedirector;
    private final EventReportPermissionManager eventReportPermissionManager;
    private final WhitelistFilter whitelistFilter;

    public WhitelistReportServlet(TemplateRenderer renderer, UserManager userManager, LoginPageRedirector loginPageRedirector, EventReportPermissionManager eventReportPermissionManager, WhitelistFilter whitelistFilter) {
        this.renderer = renderer;
        this.userManager = userManager;
        this.loginPageRedirector = loginPageRedirector;
        this.eventReportPermissionManager = eventReportPermissionManager;
        this.whitelistFilter = whitelistFilter;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.eventReportPermissionManager.hasPermission(this.userManager.getRemoteUserKey(request))) {
            this.loginPageRedirector.redirectToLogin(request, response);
            return;
        }
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("whitelistIds", this.getWhitelistIds());
        if (request.getParameter("query") != null) {
            context.put("query", request.getParameter("query"));
        }
        if (request.getParameter("whitelistId") != null) {
            context.put("selectedWhitelistId", request.getParameter("whitelistId"));
        }
        response.setContentType("text/html; charset=UTF-8");
        this.renderer.render("templates/whitelist-report.vm", context, (Writer)response.getWriter());
    }

    private List<String> getWhitelistIds() {
        ArrayList<String> whitelistIds = new ArrayList<String>();
        whitelistIds.add(this.whitelistFilter.getGlobalWhitelist().getWhitelistId());
        for (Whitelist whitelist : this.whitelistFilter.getPluginWhitelists()) {
            whitelistIds.add(whitelist.getWhitelistId());
        }
        return whitelistIds;
    }
}

