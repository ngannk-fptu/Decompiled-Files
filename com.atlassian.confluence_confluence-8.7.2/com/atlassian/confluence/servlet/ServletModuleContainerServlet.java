/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.http.method.Methods
 *  com.atlassian.plugin.servlet.ServletModuleContainerServlet
 *  com.atlassian.plugin.servlet.ServletModuleManager
 *  com.atlassian.sal.api.xsrf.XsrfRequestValidator
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  javax.annotation.Nullable
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.http.method.Methods;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.sal.api.xsrf.XsrfRequestValidator;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletModuleContainerServlet
extends com.atlassian.plugin.servlet.ServletModuleContainerServlet {
    private static final LazyComponentReference<ServletModuleManager> servletModuleManagerReference = new LazyComponentReference("servletModuleManager");
    private static final LazyComponentReference<XsrfRequestValidator> xsrfRequestValidatorReference = new LazyComponentReference("xsrfRequestValidator");
    private static final Logger log = LoggerFactory.getLogger(ServletModuleContainerServlet.class);
    private ServletConfig servletConfig;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.servletConfig = servletConfig;
    }

    @Nullable
    public ServletModuleManager getServletModuleManager() {
        return ContainerManager.isContainerSetup() ? (ServletModuleManager)servletModuleManagerReference.get() : null;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.requestRequiresProtection(request) && !this.validateToken(request)) {
            response.sendError(403, "XSRF check failed");
            return;
        }
        super.service(request, response);
    }

    private boolean requestRequiresProtection(HttpServletRequest request) throws ServletException {
        boolean requestRequiresProtection;
        if (!Methods.isMutative((String)request.getMethod())) {
            return false;
        }
        ServletModuleManager servletModuleManager = this.getServletModuleManager();
        if (servletModuleManager == null) {
            return false;
        }
        HttpServlet servlet = servletModuleManager.getServlet(this.getPathInfo(request), this.servletConfig);
        boolean bl = requestRequiresProtection = servlet != null && servlet.getServletConfig() != null && Boolean.valueOf(servlet.getInitParameter("RequireSecurityToken")) != false;
        if (ConfluenceSystemProperties.isDevMode() && !requestRequiresProtection) {
            log.warn("Servlet has not opted in for XSRF protection. Please refer https://developer.atlassian.com/server/confluence/enable-xsrf-protection-for-your-app for configuring it.");
        }
        return requestRequiresProtection;
    }

    private boolean validateToken(HttpServletRequest request) {
        if (!ContainerManager.isContainerSetup()) {
            return true;
        }
        XsrfRequestValidator xsrfRequestValidator = (XsrfRequestValidator)xsrfRequestValidatorReference.get();
        return xsrfRequestValidator == null || xsrfRequestValidator.validateRequestPassesXsrfChecks(request);
    }

    private String getPathInfo(HttpServletRequest request) {
        String pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
        if (pathInfo == null) {
            pathInfo = request.getPathInfo();
        }
        return pathInfo;
    }
}

