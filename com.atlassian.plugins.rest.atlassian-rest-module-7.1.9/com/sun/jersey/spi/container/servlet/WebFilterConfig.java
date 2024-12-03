/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 */
package com.sun.jersey.spi.container.servlet;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.WebConfig;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class WebFilterConfig
implements WebConfig {
    private final FilterConfig filterConfig;

    public WebFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public WebConfig.ConfigType getConfigType() {
        return WebConfig.ConfigType.FilterConfig;
    }

    @Override
    public String getName() {
        return this.filterConfig.getFilterName();
    }

    @Override
    public String getInitParameter(String name) {
        return this.filterConfig.getInitParameter(name);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return this.filterConfig.getInitParameterNames();
    }

    @Override
    public ServletContext getServletContext() {
        return this.filterConfig.getServletContext();
    }

    @Override
    public ResourceConfig getDefaultResourceConfig(Map<String, Object> props) throws ServletException {
        return null;
    }
}

