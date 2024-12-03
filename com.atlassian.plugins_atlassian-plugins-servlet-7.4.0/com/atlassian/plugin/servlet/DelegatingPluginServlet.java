/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderStack
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.PluginHttpRequestWrapper;
import com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.atlassian.plugin.util.ClassLoaderStack;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DelegatingPluginServlet
extends HttpServlet {
    private final ServletModuleDescriptor descriptor;
    private final HttpServlet servlet;

    public DelegatingPluginServlet(ServletModuleDescriptor descriptor) {
        this.descriptor = descriptor;
        this.servlet = descriptor.getModule();
    }

    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ClassLoaderStack.push((ClassLoader)this.descriptor.getPlugin().getClassLoader());
        try {
            this.servlet.service((ServletRequest)new PluginHttpRequestWrapper(req, this.descriptor), (ServletResponse)res);
        }
        finally {
            ClassLoaderStack.pop();
        }
    }

    public void init(ServletConfig config) throws ServletException {
        ClassLoaderStack.push((ClassLoader)this.descriptor.getPlugin().getClassLoader());
        try {
            this.servlet.init(config);
        }
        finally {
            ClassLoaderStack.pop();
        }
    }

    public void destroy() {
        ClassLoaderStack.push((ClassLoader)this.descriptor.getPlugin().getClassLoader());
        try {
            this.servlet.destroy();
        }
        finally {
            ClassLoaderStack.pop();
        }
    }

    public boolean equals(Object obj) {
        return this.servlet.equals(obj);
    }

    public String getInitParameter(String name) {
        return this.servlet.getInitParameter(name);
    }

    public Enumeration<String> getInitParameterNames() {
        Enumeration initParameterNames = this.servlet.getInitParameterNames();
        return initParameterNames;
    }

    public ServletConfig getServletConfig() {
        return this.servlet.getServletConfig();
    }

    public ServletContext getServletContext() {
        return this.servlet.getServletContext();
    }

    public String getServletInfo() {
        return this.servlet.getServletInfo();
    }

    public String getServletName() {
        return this.servlet.getServletName();
    }

    public int hashCode() {
        return this.servlet.hashCode();
    }

    public void init() throws ServletException {
        this.servlet.init();
    }

    public void log(String message, Throwable t) {
        this.servlet.log(message, t);
    }

    public void log(String msg) {
        this.servlet.log(msg);
    }

    public String toString() {
        return this.servlet.toString();
    }

    public ServletModuleDescriptor getModuleDescriptor() {
        return this.descriptor;
    }

    HttpServlet getDelegatingServlet() {
        return this.servlet;
    }
}

