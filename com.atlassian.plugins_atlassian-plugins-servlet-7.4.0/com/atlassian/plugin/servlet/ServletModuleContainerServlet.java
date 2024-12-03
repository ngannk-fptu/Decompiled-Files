/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.util.RequestUtil;
import com.atlassian.plugin.servlet.util.ServletContextServletModuleManagerAccessor;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletModuleContainerServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ServletModuleContainerServlet.class);
    private ServletConfig servletConfig;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.servletConfig = servletConfig;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletModuleManager servletModuleManager = this.getServletModuleManager();
        if (servletModuleManager == null) {
            log.error("Could not get ServletModuleManager?");
            response.sendError(500, "Could not get ServletModuleManager.");
            return;
        }
        HttpServlet servlet = servletModuleManager.getServlet(RequestUtil.getPathInfo(request), this.servletConfig);
        if (servlet == null) {
            log.debug("No servlet found for: " + RequestUtil.getRequestURI(request));
            response.sendError(404, "Could not find servlet.");
            return;
        }
        try {
            servlet.service((ServletRequest)request, (ServletResponse)response);
        }
        catch (ServletException e) {
            log.error(e.getMessage(), (Throwable)e);
            response.sendError(500, e.getMessage());
        }
    }

    protected ServletModuleManager getServletModuleManager() {
        return ServletContextServletModuleManagerAccessor.getServletModuleManager(this.getServletContext());
    }
}

