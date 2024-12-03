/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderStack
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugin.servlet.filter;

import com.atlassian.plugin.servlet.PluginHttpRequestWrapper;
import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.atlassian.plugin.util.ClassLoaderStack;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class DelegatingPluginFilter
implements Filter {
    private final ServletFilterModuleDescriptor descriptor;
    private final Filter filter;

    public DelegatingPluginFilter(ServletFilterModuleDescriptor descriptor) {
        this.descriptor = descriptor;
        this.filter = descriptor.getModule();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        ClassLoaderStack.push((ClassLoader)this.descriptor.getPlugin().getClassLoader());
        try {
            this.filter.init(filterConfig);
        }
        finally {
            ClassLoaderStack.pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ClassLoader pluginClassLoader = this.descriptor.getPlugin().getClassLoader();
        ClassLoaderStack.push((ClassLoader)pluginClassLoader);
        try {
            FilterChain resetContextClassLoaderChain = (servletRequest, servletResponse) -> {
                ClassLoaderStack.pop();
                try {
                    chain.doFilter(servletRequest, servletResponse);
                }
                finally {
                    ClassLoaderStack.push((ClassLoader)pluginClassLoader);
                }
            };
            this.filter.doFilter((ServletRequest)this.createPluginHttpRequestWrapper((HttpServletRequest)request, this.descriptor), response, resetContextClassLoaderChain);
        }
        finally {
            ClassLoaderStack.pop();
        }
    }

    private PluginHttpRequestWrapper createPluginHttpRequestWrapper(HttpServletRequest request, ServletFilterModuleDescriptor descriptor) {
        return request instanceof PluginHttpRequestWrapper ? (PluginHttpRequestWrapper)request : new PluginHttpRequestWrapper(request, descriptor);
    }

    public void destroy() {
        ClassLoaderStack.push((ClassLoader)this.descriptor.getPlugin().getClassLoader());
        try {
            this.filter.destroy();
        }
        finally {
            ClassLoaderStack.pop();
        }
    }

    public Filter getDelegatingFilter() {
        return this.filter;
    }
}

