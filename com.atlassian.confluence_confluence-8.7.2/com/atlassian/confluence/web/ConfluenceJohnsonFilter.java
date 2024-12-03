/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.filters.JohnsonFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.web;

import com.atlassian.confluence.impl.tenant.ThreadLocalTenantGate;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.filters.JohnsonFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceJohnsonFilter
extends JohnsonFilter {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceJohnsonFilter.class);

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (this.fullPathIgnored((HttpServletRequest)servletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            super.doFilter(servletRequest, servletResponse, filterChain);
        }
    }

    protected void handleError(JohnsonEventContainer appEventContainer, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String servletPath = ConfluenceJohnsonFilter.getServletPath((HttpServletRequest)servletRequest);
        log.info("The application is still starting up, or there are errors. Redirecting request from '" + servletPath + "' to '" + this.config.getErrorPath() + "'");
        if (servletRequest.getMethod().equalsIgnoreCase("GET")) {
            servletResponse.setStatus(503);
            try {
                ThreadLocalTenantGate.withTenantPermit(() -> {
                    servletRequest.getRequestDispatcher(this.config.getErrorPath()).forward((ServletRequest)servletRequest, (ServletResponse)servletResponse);
                    return null;
                }).call();
                return;
            }
            catch (Exception e) {
                log.error("Unable to forward request to " + this.config.getErrorPath() + ". Performing a redirect instead.", (Throwable)e);
            }
        }
        String contextPath = servletRequest.getContextPath();
        servletResponse.sendRedirect(contextPath + this.config.getErrorPath());
    }

    private boolean fullPathIgnored(HttpServletRequest request) {
        String fullPath = StringUtils.defaultString((String)request.getServletPath()) + StringUtils.defaultString((String)request.getPathInfo());
        return this.config.isIgnoredPath(fullPath);
    }
}

