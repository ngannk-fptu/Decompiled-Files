/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.johnson.filters;

import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.config.JohnsonConfig;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.RequestEventCheck;
import com.atlassian.johnson.setup.SetupConfig;
import com.atlassian.johnson.util.StringUtils;
import java.io.IOException;
import java.util.Collection;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractJohnsonFilter
implements Filter {
    protected static final String TEXT_XML_UTF8_CONTENT_TYPE = "text/xml;charset=utf-8";
    protected FilterConfig filterConfig;
    protected JohnsonConfig config;

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String alreadyFilteredKey = this.getClass().getName() + "_already_filtered";
        if (servletRequest.getAttribute(alreadyFilteredKey) != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        servletRequest.setAttribute(alreadyFilteredKey, (Object)Boolean.TRUE);
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String servletPath = AbstractJohnsonFilter.getServletPath(request);
        JohnsonEventContainer appEventContainer = this.getContainerAndRunEventChecks(request);
        SetupConfig setup = this.config.getSetupConfig();
        boolean ignoreUri = this.ignoreURI(servletPath);
        if (appEventContainer.hasEvents() && !ignoreUri) {
            this.handleError(appEventContainer, request, response);
        } else if (!(ignoreUri || setup.isSetup() || setup.isSetupPage(servletPath))) {
            this.handleNotSetup(request, response);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.config = Johnson.getConfig();
    }

    protected JohnsonEventContainer getContainerAndRunEventChecks(HttpServletRequest req) {
        JohnsonEventContainer appEventContainer = Johnson.getEventContainer(this.filterConfig.getServletContext());
        for (RequestEventCheck requestEventCheck : this.config.getRequestEventChecks()) {
            requestEventCheck.check(appEventContainer, req);
        }
        return appEventContainer;
    }

    protected static String getServletPath(HttpServletRequest request) {
        int endIndex;
        String servletPath = request.getServletPath();
        if (!StringUtils.isEmpty(servletPath)) {
            return servletPath;
        }
        String requestUri = request.getRequestURI();
        int startIndex = request.getContextPath().equals("") ? 0 : request.getContextPath().length();
        int n = endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri.lastIndexOf(request.getPathInfo());
        if (startIndex > endIndex) {
            endIndex = startIndex;
        }
        return requestUri.substring(startIndex, endIndex);
    }

    protected String getStringForEvents(Collection<Event> events) {
        StringBuilder message = new StringBuilder();
        for (Event event : events) {
            if (message.length() > 0) {
                message.append("\n");
            }
            message.append(event.getDesc());
        }
        return message.toString();
    }

    protected abstract void handleError(JohnsonEventContainer var1, HttpServletRequest var2, HttpServletResponse var3) throws IOException;

    protected abstract void handleNotSetup(HttpServletRequest var1, HttpServletResponse var2) throws IOException;

    protected boolean ignoreURI(String uri) {
        return uri.equalsIgnoreCase(this.config.getErrorPath()) || this.config.isIgnoredPath(uri);
    }
}

