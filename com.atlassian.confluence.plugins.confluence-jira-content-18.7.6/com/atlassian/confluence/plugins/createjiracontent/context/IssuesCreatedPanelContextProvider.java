/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.plugins.createjiracontent.context;

import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IssuesCreatedPanelContextProvider
implements ContextProvider {
    private final HttpContext httpContext;

    public IssuesCreatedPanelContextProvider(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        context.put("numOfIssues", this.getNumOfIssuesCreated());
        context.put("issuesURL", this.getIssuesURL());
        context.put("issueId", this.getIssueId());
        context.put("addedToPage", this.isAddedToPage());
        context.put("errorMessages", this.getErrorMessages());
        context.put("statusText", this.getStatusText());
        return context;
    }

    private int getNumOfIssuesCreated() {
        return Integer.parseInt(this.httpContext.getRequest().getParameter("numOfIssues"));
    }

    private String getIssuesURL() {
        return this.httpContext.getRequest().getParameter("issuesURL");
    }

    private String getIssueId() {
        return this.httpContext.getRequest().getParameter("issueId");
    }

    private boolean isAddedToPage() {
        return Boolean.parseBoolean(this.httpContext.getRequest().getParameter("addedToPage"));
    }

    private List<String> getErrorMessages() {
        String[] values = this.httpContext.getRequest().getParameterValues("errorMessages");
        if (values == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(values);
    }

    private String getStatusText() {
        return this.httpContext.getRequest().getParameter("statusText");
    }
}

