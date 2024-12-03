/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public final class PluginServletConfig
implements ServletConfig {
    private final BaseServletModuleDescriptor<?> descriptor;
    private final ServletContext servletContext;

    public PluginServletConfig(BaseServletModuleDescriptor<?> descriptor, ServletContext servletContext) {
        this.descriptor = descriptor;
        this.servletContext = servletContext;
    }

    public String getServletName() {
        return this.descriptor.getDisplayName();
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public String getInitParameter(String s) {
        return this.descriptor.getInitParams().get(s);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.descriptor.getInitParams().keySet());
    }
}

