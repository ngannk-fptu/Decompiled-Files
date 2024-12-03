/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugin.servlet.filter;

import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class PluginFilterConfig
implements FilterConfig {
    private final ServletFilterModuleDescriptor descriptor;
    private final ServletContext servletContext;

    public PluginFilterConfig(ServletFilterModuleDescriptor descriptor, ServletContext servletContext) {
        this.descriptor = descriptor;
        this.servletContext = servletContext;
    }

    public String getFilterName() {
        return this.descriptor.getDisplayName();
    }

    public String getInitParameter(String name) {
        return this.descriptor.getInitParams().get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.descriptor.getInitParams().keySet());
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }
}

