/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 */
package com.sun.jersey.spi.container.servlet;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class WebServletConfig
implements WebConfig {
    private final ServletContainer servlet;

    public WebServletConfig(ServletContainer servlet) {
        this.servlet = servlet;
    }

    @Override
    public WebConfig.ConfigType getConfigType() {
        return WebConfig.ConfigType.ServletConfig;
    }

    @Override
    public String getName() {
        return this.servlet.getServletName();
    }

    @Override
    public String getInitParameter(String name) {
        return this.servlet.getInitParameter(name);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return this.servlet.getInitParameterNames();
    }

    @Override
    public ServletContext getServletContext() {
        return this.servlet.getServletContext();
    }

    @Override
    public ResourceConfig getDefaultResourceConfig(Map<String, Object> props) throws ServletException {
        return this.servlet.getDefaultResourceConfig(props, this.servlet.getServletConfig());
    }
}

