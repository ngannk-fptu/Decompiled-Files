/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.PluginHttpSessionWrapper;
import com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class PluginHttpRequestWrapper
extends HttpServletRequestWrapper {
    private final boolean asyncSupported;
    private final String basePath;
    private final HttpServletRequest delegate;
    private final BaseServletModuleDescriptor<?> descriptor;

    public PluginHttpRequestWrapper(HttpServletRequest request, BaseServletModuleDescriptor<?> descriptor) {
        super(request);
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
        this.asyncSupported = request.isAsyncSupported() && descriptor.isAsyncSupported();
        this.basePath = this.findBasePath(descriptor);
        this.delegate = request;
    }

    public String getServletPath() {
        String servletPath = super.getServletPath();
        if (this.basePath != null) {
            servletPath = servletPath + this.basePath;
        }
        return servletPath;
    }

    public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        if (pathInfo != null && this.basePath != null) {
            if (this.basePath.equals(pathInfo)) {
                return null;
            }
            if (pathInfo.startsWith(this.basePath)) {
                return pathInfo.substring(this.basePath.length());
            }
        }
        return pathInfo;
    }

    public HttpSession getSession() {
        return this.getSession(true);
    }

    public HttpSession getSession(boolean create) {
        HttpSession session = this.delegate.getSession(create);
        if (session == null) {
            return null;
        }
        return session instanceof PluginHttpSessionWrapper ? session : new PluginHttpSessionWrapper(session);
    }

    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public AsyncContext startAsync() {
        this.requireAsyncSupport((ServletRequest)this);
        return super.startAsync();
    }

    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        this.requireAsyncSupport(servletRequest);
        return super.startAsync(servletRequest, servletResponse);
    }

    private static boolean arrayStartsWith(String[] array, String[] prefixArray) {
        if (prefixArray.length > array.length) {
            return false;
        }
        for (int i = prefixArray.length - 1; i >= 0; --i) {
            if (prefixArray[i].equals(array[i])) continue;
            return false;
        }
        return true;
    }

    private static String getMappingRootPath(String pathMapping) {
        return pathMapping.substring(0, pathMapping.length() - "/*".length());
    }

    private static SortedSet<String> getNonAsyncKeys(ServletRequest servletRequest) {
        TreeSet<String> nonAsyncKeys = new TreeSet<String>();
        while (servletRequest instanceof ServletRequestWrapper) {
            if (servletRequest instanceof PluginHttpRequestWrapper) {
                PluginHttpRequestWrapper wrapper = (PluginHttpRequestWrapper)servletRequest;
                if (!wrapper.descriptor.isAsyncSupported()) {
                    nonAsyncKeys.add(wrapper.descriptor.getCompleteKey());
                }
            }
            servletRequest = ((ServletRequestWrapper)servletRequest).getRequest();
        }
        return nonAsyncKeys;
    }

    private static boolean isPathMapping(String path) {
        return path.startsWith("/") && path.endsWith("/*");
    }

    private String findBasePath(BaseServletModuleDescriptor<?> descriptor) {
        String pathInfo = super.getPathInfo();
        if (pathInfo != null) {
            for (String basePath : descriptor.getPaths()) {
                if (!basePath.equals(pathInfo)) continue;
                return basePath;
            }
            String[] pathInfoComponents = StringUtils.split((String)pathInfo, (char)'/');
            for (String basePath : descriptor.getPaths()) {
                String mappingRootPath;
                String[] mappingRootPathComponents;
                if (!PluginHttpRequestWrapper.isPathMapping(basePath) || !PluginHttpRequestWrapper.arrayStartsWith(pathInfoComponents, mappingRootPathComponents = StringUtils.split((String)(mappingRootPath = PluginHttpRequestWrapper.getMappingRootPath(basePath)), (char)'/'))) continue;
                return mappingRootPath;
            }
        }
        return null;
    }

    private void requireAsyncSupport(ServletRequest servletRequest) {
        if (!this.isAsyncSupported()) {
            SortedSet<String> nonAsyncKeys = PluginHttpRequestWrapper.getNonAsyncKeys(servletRequest);
            IllegalStateException ise = new IllegalStateException("One of the plugins in the filter chain does not support async");
            if (!nonAsyncKeys.isEmpty()) {
                LoggerFactory.getLogger(((Object)((Object)this)).getClass()).warn("The following plugin-provided filter(s) do not support async: {}", nonAsyncKeys, (Object)ise);
            }
            throw ise;
        }
    }
}

