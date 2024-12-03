/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.troubleshooting.thready.filter;

import com.atlassian.troubleshooting.thready.filter.AbstractThreadNamingFilter;
import com.atlassian.troubleshooting.thready.manager.RequestValidator;
import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import com.atlassian.troubleshooting.thready.manager.ThreadNameManager;
import javax.servlet.http.HttpServletRequest;

public class UrlThreadNamingFilter
extends AbstractThreadNamingFilter {
    public UrlThreadNamingFilter(ThreadNameManager threadNameManager, ThreadDiagnosticsConfigurationManager threadDiagnosticsConfigurationManager, RequestValidator requestValidator) {
        super(threadNameManager, threadDiagnosticsConfigurationManager, requestValidator);
    }

    @Override
    protected void updateAttributes(HttpServletRequest request, ThreadNameManager threadNameManager) {
        threadNameManager.addThreadAttribute("url", request.getRequestURI());
    }
}

