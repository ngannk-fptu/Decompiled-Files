/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.diagnostics.internal.perflog;

import com.atlassian.diagnostics.internal.perflog.PerfLogTabOpenedAnalyticsEvent;
import com.atlassian.diagnostics.internal.perflog.PerformanceLogServletUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PerformanceLogServlet
extends HttpServlet {
    private final PerformanceLogServletUtils performanceLogServletUtils;
    private final SoyTemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final WebSudoManager webSudoManager;
    private final DarkFeatureManager darkFeatureManager;
    private final EventPublisher eventPublisher;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserProfile user = this.userManager.getRemoteUser(request);
        if (user == null || !this.userManager.isSystemAdmin(user.getUserKey())) {
            this.performanceLogServletUtils.redirectToAdminLogin(request, response);
            return;
        }
        Optional enabledForCurrentUser = this.darkFeatureManager.isEnabledForCurrentUser("com.atlassian.jira.in.product.diagnostics.wip.enabled");
        if (enabledForCurrentUser.isPresent() && !((Boolean)enabledForCurrentUser.get()).booleanValue()) {
            response.setContentType("text/html;charset=UTF-8");
            response.sendError(404);
            return;
        }
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            response.setContentType("text/html;charset=UTF-8");
            this.templateRenderer.render((Appendable)response.getWriter(), "com.atlassian.diagnostics.atlassian-diagnostics-plugin:diagnostics-perflog-templates", "diagnostics.perflog", Collections.emptyMap());
            this.eventPublisher.publish((Object)new PerfLogTabOpenedAnalyticsEvent());
        }
        catch (WebSudoSessionException wes) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    public PerformanceLogServlet(PerformanceLogServletUtils performanceLogServletUtils, SoyTemplateRenderer templateRenderer, UserManager userManager, WebSudoManager webSudoManager, DarkFeatureManager darkFeatureManager, EventPublisher eventPublisher) {
        this.performanceLogServletUtils = performanceLogServletUtils;
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.webSudoManager = webSudoManager;
        this.darkFeatureManager = darkFeatureManager;
        this.eventPublisher = eventPublisher;
    }
}

