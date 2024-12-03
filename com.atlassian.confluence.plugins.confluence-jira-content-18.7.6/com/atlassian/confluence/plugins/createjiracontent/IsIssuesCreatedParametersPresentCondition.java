/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.web.context.HttpContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.validator.routines.UrlValidator
 */
package com.atlassian.confluence.plugins.createjiracontent;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.web.context.HttpContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

public class IsIssuesCreatedParametersPresentCondition
extends BaseConfluenceCondition {
    public static final String JIRA_ISSUES_CREATED_REQ_PARAM = "JIRAIssuesCreated";
    public static final String NUM_OF_ISSUES_REQ_PARAM = "numOfIssues";
    public static final String ISSUE_NAME_REQ_PARAM = "issueId";
    public static final String ISSUES_URL_REQ_PARAM = "issuesURL";
    public static final String ADDED_TO_PAGE_REQ_PARAM = "addedToPage";
    public static final String ERROR_MESSAGES_REQ_PARAM = "errorMessages";
    public static final String STATUS_TEXT_REQ_PARAM = "statusText";
    private final HttpContext httpContext;

    public IsIssuesCreatedParametersPresentCondition(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        HttpServletRequest request = this.httpContext.getRequest();
        String issueURL = request.getParameter(ISSUES_URL_REQ_PARAM);
        return StringUtils.isNotBlank((CharSequence)request.getParameter(JIRA_ISSUES_CREATED_REQ_PARAM)) && StringUtils.isNotBlank((CharSequence)request.getParameter(NUM_OF_ISSUES_REQ_PARAM)) && StringUtils.isNotBlank((CharSequence)request.getParameter(ADDED_TO_PAGE_REQ_PARAM)) && StringUtils.isNotBlank((CharSequence)issueURL) && UrlValidator.getInstance().isValid(issueURL);
    }
}

