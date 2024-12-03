/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.bc.license.JiraLicenseService
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.analytics.client.filter;

import com.atlassian.analytics.client.filter.AbstractHttpFilter;
import com.atlassian.jira.bc.license.JiraLicenseService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class JiraAnalyticsFilter
extends AbstractHttpFilter {
    private final JiraLicenseService jiraLicenseService;

    public JiraAnalyticsFilter(JiraLicenseService jiraLicenseService) {
        this.jiraLicenseService = jiraLicenseService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        for (String sen : this.jiraLicenseService.getSupportEntitlementNumbers()) {
            if (!StringUtils.isNotBlank((CharSequence)sen)) continue;
            JiraAnalyticsFilter.setB3TraceId(request);
            break;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

