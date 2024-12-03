/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package com.opensymphony.module.sitemesh;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class Config {
    private ServletConfig servletConfig;
    private FilterConfig filterConfig;
    private String configFile;

    public Config(ServletConfig servletConfig) {
        if (servletConfig == null) {
            throw new NullPointerException("ServletConfig cannot be null");
        }
        this.servletConfig = servletConfig;
        this.configFile = servletConfig.getInitParameter("configFile");
    }

    public Config(FilterConfig filterConfig) {
        if (filterConfig == null) {
            throw new NullPointerException("FilterConfig cannot be null");
        }
        this.filterConfig = filterConfig;
        this.configFile = filterConfig.getInitParameter("configFile");
    }

    public ServletContext getServletContext() {
        return this.servletConfig != null ? this.servletConfig.getServletContext() : this.filterConfig.getServletContext();
    }

    public String getConfigFile() {
        return this.configFile;
    }
}

