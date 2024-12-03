/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.internal.integration.jira.web;

import com.atlassian.internal.integration.jira.IconRequest;
import com.atlassian.internal.integration.jira.InternalJiraService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;

public class JiraIconServlet
extends HttpServlet {
    private static final long serialVersionUID = -1290766128724561166L;
    private static final String JIRA_PROJECT_ID_PATTERN = "\\d+";
    protected InternalJiraService jiraService;

    public JiraIconServlet(InternalJiraService internalJiraService) {
        this.jiraService = internalJiraService;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String projectId = req.getParameter("pid");
        if (projectId == null || !projectId.matches(JIRA_PROJECT_ID_PATTERN)) {
            resp.setStatus(400);
            resp.setContentType("text/html; charset=utf-8");
            PrintWriter writer = resp.getWriter();
            writer.println("Invalid project ID: " + StringEscapeUtils.escapeHtml4((String)projectId));
            writer.flush();
            return;
        }
        IconRequest context = new IconRequest(req);
        this.jiraService.streamIcon(context, resp);
    }
}

