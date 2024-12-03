/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package org.apache.sling.scripting.jsp;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

class JspServletConfig
implements ServletConfig {
    private final ServletContext servletContext;
    private String servletName;
    private final Map<String, String> properties;

    JspServletConfig(ServletContext servletContext, Map<String, String> config) {
        this.servletContext = servletContext;
        this.servletName = config.get("service.description");
        if (this.servletName == null) {
            this.servletName = "JSP Script Handler";
        }
        this.properties = config;
    }

    public String getInitParameter(String name) {
        return this.properties.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.properties.keySet());
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public String getServletName() {
        return this.servletName;
    }

    public String getConfigKey() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append(';');
        }
        return sb.toString();
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }
}

