/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 */
package com.sun.jersey.spi.container.servlet;

import com.sun.jersey.api.core.ResourceConfig;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public interface WebConfig {
    public ConfigType getConfigType();

    public String getName();

    public String getInitParameter(String var1);

    public Enumeration getInitParameterNames();

    public ServletContext getServletContext();

    public ResourceConfig getDefaultResourceConfig(Map<String, Object> var1) throws ServletException;

    public static enum ConfigType {
        ServletConfig,
        FilterConfig;

    }
}

