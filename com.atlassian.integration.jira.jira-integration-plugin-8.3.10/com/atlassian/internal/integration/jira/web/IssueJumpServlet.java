/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.internal.integration.jira.web;

import com.atlassian.internal.integration.jira.InternalJiraService;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class IssueJumpServlet
extends HttpServlet {
    public static final String NEXT_APPLICATION_ID_PARAM = "nextApplicationId";
    public static final String SERVER_SOY_RESOURCE = "com.atlassian.integration.jira.jira-integration-plugin:server-side-templates";
    public static final String SERVLET_PATH = "/plugins/servlet/jira-integration/issues";
    private final ApplicationProperties applicationProperties;
    private final I18nResolver i18nResolver;
    private final InternalJiraService jiraService;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final WebResourceManager webResourceManager;

    public IssueJumpServlet(ApplicationProperties applicationProperties, I18nResolver i18nResolver, InternalJiraService jiraService, SoyTemplateRenderer soyTemplateRenderer, WebResourceManager webResourceManager) {
        this.applicationProperties = applicationProperties;
        this.i18nResolver = i18nResolver;
        this.jiraService = jiraService;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.webResourceManager = webResourceManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (StringUtils.isBlank((CharSequence)pathInfo) || pathInfo.equals("/")) {
            this.renderIssueKeyNotProvided(resp);
            return;
        }
        String issueKey = pathInfo.substring(1);
        if (!this.jiraService.isLinked()) {
            this.renderIssueNotFound(resp, issueKey, this.i18nResolver.getText("error.page.issue.not.found.noapplink.detail", new Serializable[]{this.applicationProperties.getDisplayName()}));
            return;
        }
        String nextApplicationLinkId = req.getParameter(NEXT_APPLICATION_ID_PARAM);
        URI redirectUri = this.jiraService.findIssue(issueKey, nextApplicationLinkId);
        if (redirectUri == null) {
            this.renderIssueNotFound(resp, issueKey, this.i18nResolver.getText("error.page.issue.not.found.detail", new Serializable[]{"<br/>"}));
        } else {
            resp.sendRedirect(redirectUri.toASCIIString());
        }
    }

    private void render(HttpServletResponse resp, String templateName, Map<String, Object> templateData) throws IOException, ServletException {
        this.webResourceManager.requireResourcesForContext("jira-integration-error-page");
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        try {
            this.soyTemplateRenderer.render((Appendable)writer, SERVER_SOY_RESOURCE, templateName, templateData);
        }
        catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new ServletException((Throwable)e);
        }
        finally {
            writer.flush();
        }
    }

    private void renderIssueNotFound(HttpServletResponse resp, String issueKey, String reason) throws IOException, ServletException {
        this.render(resp, "jiraIntegration.page.issueNotFound", (Map<String, Object>)ImmutableMap.builder().put((Object)"issueKey", (Object)issueKey).put((Object)"reason", (Object)reason).build());
    }

    private void renderIssueKeyNotProvided(HttpServletResponse resp) throws IOException, ServletException {
        this.render(resp, "jiraIntegration.page.issueKeyNotProvided", Collections.emptyMap());
    }
}

