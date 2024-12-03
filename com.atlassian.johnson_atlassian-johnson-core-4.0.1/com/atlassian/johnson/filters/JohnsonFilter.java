/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.johnson.filters;

import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.filters.AbstractJohnsonFilter;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JohnsonFilter
extends AbstractJohnsonFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JohnsonFilter.class);

    @Override
    protected void handleError(JohnsonEventContainer appEventContainer, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String servletPath = JohnsonFilter.getServletPath(servletRequest);
        String contextPath = servletRequest.getContextPath();
        LOG.info("The application is still starting up, or there are errors. Redirecting request from '{}' to '{}'", (Object)servletPath, (Object)this.config.getErrorPath());
        String nextUrl = servletRequest.getRequestURI();
        if (servletRequest.getQueryString() != null && !servletRequest.getQueryString().isEmpty()) {
            nextUrl = nextUrl + "?" + servletRequest.getQueryString();
        }
        String redirectUrl = contextPath + this.config.getErrorPath() + "?next=" + URLEncoder.encode(nextUrl, "UTF-8");
        servletResponse.sendRedirect(redirectUrl);
    }

    @Override
    protected void handleNotSetup(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String servletPath = JohnsonFilter.getServletPath(servletRequest);
        String contextPath = servletRequest.getContextPath();
        LOG.info("The application is not yet setup. Redirecting request from '{}' to '{}'", (Object)servletPath, (Object)this.config.getSetupPath());
        servletResponse.sendRedirect(contextPath + this.config.getSetupPath());
    }
}

