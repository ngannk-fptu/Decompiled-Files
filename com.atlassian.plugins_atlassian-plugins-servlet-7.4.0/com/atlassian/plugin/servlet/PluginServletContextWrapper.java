/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  javax.servlet.Filter
 *  javax.servlet.FilterRegistration
 *  javax.servlet.FilterRegistration$Dynamic
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRegistration
 *  javax.servlet.ServletRegistration$Dynamic
 *  javax.servlet.SessionCookieConfig
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.http.HttpServlet
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.servlet.ServletModuleManager;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServlet;

public class PluginServletContextWrapper
implements ServletContext {
    private final ServletModuleManager servletModuleManager;
    private final Plugin plugin;
    private final ServletContext context;
    private final ConcurrentMap<String, Object> attributes;
    private final Map<String, String> initParams;
    private final Method methodGetContextPath;

    public PluginServletContextWrapper(ServletModuleManager servletModuleManager, Plugin plugin, ServletContext context, ConcurrentMap<String, Object> attributes, Map<String, String> initParams) {
        Method tmpMethod = null;
        this.servletModuleManager = servletModuleManager;
        this.plugin = plugin;
        this.context = context;
        this.attributes = attributes;
        this.initParams = initParams;
        Class<?> cls = context.getClass();
        try {
            tmpMethod = cls.getMethod("getContextPath", new Class[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        this.methodGetContextPath = tmpMethod;
    }

    public Object getAttribute(String name) {
        Object attr = this.attributes.get(name);
        if (attr == null) {
            attr = this.context.getAttribute(name);
        }
        return attr;
    }

    public Enumeration<String> getAttributeNames() {
        HashSet<Object> names = new HashSet<Object>();
        names.addAll(this.attributes.keySet());
        names.addAll(Collections.list(this.context.getAttributeNames()));
        return Collections.enumeration(names);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public void setAttribute(String name, Object object) {
        this.attributes.put(name, object);
    }

    public String getInitParameter(String name) {
        return this.initParams.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.initParams.keySet());
    }

    public URL getResource(String path) throws MalformedURLException {
        URL url = this.plugin.getResource(path);
        if (url == null) {
            url = this.context.getResource(path);
        }
        return url;
    }

    public InputStream getResourceAsStream(String path) {
        InputStream in = this.plugin.getResourceAsStream(path);
        if (in == null) {
            in = this.context.getResourceAsStream(path);
        }
        return in;
    }

    public ServletContext getContext(String uripath) {
        return null;
    }

    public String getContextPath() {
        if (this.methodGetContextPath != null) {
            try {
                return (String)this.methodGetContextPath.invoke((Object)this.context, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access this method", e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Unable to execute getContextPath()", e.getCause());
            }
        }
        throw new UnsupportedOperationException("This servlet context doesn't support 2.5 methods");
    }

    public int getMajorVersion() {
        return this.context.getMajorVersion();
    }

    public String getMimeType(String file) {
        return this.context.getMimeType(file);
    }

    public int getMinorVersion() {
        return this.context.getMinorVersion();
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return this.context.getNamedDispatcher(name);
    }

    public String getRealPath(String path) {
        return this.context.getRealPath(path);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return this.context.getRequestDispatcher(path);
    }

    public Set<String> getResourcePaths(String arg0) {
        return this.context.getResourcePaths(arg0);
    }

    public String getServerInfo() {
        return this.context.getServerInfo();
    }

    public Servlet getServlet(String name) throws ServletException {
        return this.context.getServlet(name);
    }

    public String getServletContextName() {
        return this.context.getServletContextName();
    }

    public Enumeration<String> getServletNames() {
        return this.context.getServletNames();
    }

    public Enumeration<Servlet> getServlets() {
        return this.context.getServlets();
    }

    public void log(Exception exception, String msg) {
        this.context.log(exception, msg);
    }

    public void log(String message, Throwable throwable) {
        this.context.log(message, throwable);
    }

    public void log(String msg) {
        this.context.log(msg);
    }

    public boolean setInitParameter(String name, String value) {
        if (this.initParams.containsKey(name)) {
            return false;
        }
        this.initParams.put(name, value);
        return true;
    }

    public int getEffectiveMajorVersion() {
        return this.context.getEffectiveMajorVersion();
    }

    public int getEffectiveMinorVersion() {
        return this.context.getEffectiveMinorVersion();
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return this.context.getSessionCookieConfig();
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        this.context.setSessionTrackingModes(sessionTrackingModes);
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return this.context.getDefaultSessionTrackingModes();
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return this.context.getEffectiveSessionTrackingModes();
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.context.getJspConfigDescriptor();
    }

    public ClassLoader getClassLoader() {
        return this.context.getClassLoader();
    }

    public void declareRoles(String ... roleNames) {
        this.context.declareRoles(roleNames);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        this.servletModuleManager.addServlet(this.plugin, servletName, className);
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        if (!(servlet instanceof HttpServlet)) {
            throw new IllegalArgumentException("only javax.servlet.http.HttpServlet is supported by atlassian-plugins for javax.servlet.ServletContext#addServlet(String, javax.servlet.Servlet)}");
        }
        this.servletModuleManager.addServlet(this.plugin, servletName, (HttpServlet)servlet, this);
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        this.servletModuleManager.addServlet(this.plugin, servletName, servletClass.getName());
        return null;
    }

    public <T extends Servlet> T createServlet(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    public ServletRegistration getServletRegistration(String servletName) {
        throw new UnsupportedOperationException();
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw new UnsupportedOperationException();
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        throw new UnsupportedOperationException();
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        throw new UnsupportedOperationException();
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        throw new UnsupportedOperationException();
    }

    public <T extends Filter> T createFilter(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        throw new UnsupportedOperationException();
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw new UnsupportedOperationException();
    }

    public void addListener(String className) {
        throw new UnsupportedOperationException();
    }

    public <T extends EventListener> void addListener(T t) {
        throw new UnsupportedOperationException();
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
        throw new UnsupportedOperationException();
    }

    public <T extends EventListener> T createListener(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    public String getVirtualServerName() {
        return this.context.getVirtualServerName();
    }
}

