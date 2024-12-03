/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.servlet.ServletManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringManagedServlet
extends HttpServlet {
    public static final String COMPONENT_NAME_KEY = "springComponentName";
    private static final Logger log = LoggerFactory.getLogger(SpringManagedServlet.class);
    private ServletManager servletManager;
    private ServletConfig servletConfig;
    private String componentName;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.servletConfig = servletConfig;
        this.componentName = servletConfig.getInitParameter(COMPONENT_NAME_KEY);
        if (!StringUtils.isNotEmpty((CharSequence)this.componentName)) {
            throw new ServletException(this.getServletName() + " missing a springComponentName servlet parameter");
        }
    }

    public void destroy() {
        if (this.servletManager != null) {
            try {
                this.servletManager.servletDestroyed(this);
            }
            catch (Exception e) {
                log.error("Error destroying servlet: " + e, (Throwable)e);
            }
        }
        super.destroy();
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.getServletManager() == null) {
            throw new ServletException("Confluence is not yet configured to service this request");
        }
        this.getServletManager().service(this, request, response);
    }

    private synchronized ServletManager getServletManager() throws ServletException {
        if (this.servletManager == null && GeneralUtil.isSetupComplete()) {
            try {
                this.servletManager = (ServletManager)ContainerManager.getComponent((String)this.componentName);
                if (this.servletManager == null) {
                    log.error(this.getServletName() + " unable to load servlet manager. Could not find component with name: " + this.componentName);
                } else {
                    this.servletManager.servletInitialised(this, this.servletConfig);
                }
            }
            catch (ClassCastException e) {
                log.error(this.getServletName() + " unable to load servlet manager. Component with name " + this.componentName + " is wrong class: " + ContainerManager.getComponent((String)this.componentName).getClass().getName());
            }
        }
        return this.servletManager;
    }
}

