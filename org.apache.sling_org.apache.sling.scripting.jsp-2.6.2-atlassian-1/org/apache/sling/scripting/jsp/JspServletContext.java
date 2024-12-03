/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.sling.scripting.jsp;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
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
import org.apache.sling.scripting.jsp.SlingIOProvider;
import org.apache.sling.scripting.jsp.SlingTldLocationsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JspServletContext
implements ServletContext {
    private static final Logger log = LoggerFactory.getLogger(JspServletContext.class);
    private final SlingIOProvider ioProvider;
    private final ServletContext delegatee;
    private final SlingTldLocationsCache tcs;

    JspServletContext(SlingIOProvider ioProvider, ServletContext componentContext, SlingTldLocationsCache tcs) {
        this.ioProvider = ioProvider;
        this.delegatee = componentContext;
        this.tcs = tcs;
    }

    public URL getResource(String path) throws MalformedURLException {
        URL url;
        if (path.startsWith("/") && (url = this.ioProvider.getURL(path)) != null) {
            return url;
        }
        return this.getUrlForResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        if (path.startsWith("/")) {
            try {
                return this.ioProvider.getInputStream(path);
            }
            catch (Exception ex) {
                log.debug("getResourceAsStream: Cannot get resource {}: {}", (Object)path, (Object)ex.getMessage());
            }
        }
        try {
            URL url = this.getUrlForResource(path);
            if (url != null) {
                return url.openStream();
            }
        }
        catch (Exception e) {
            log.debug("getResourceAsStream: Cannot access resource {} through URL: {}", (Object)path, (Object)e.getMessage());
        }
        return null;
    }

    public Set<String> getResourcePaths(String path) {
        return this.ioProvider.getResourcePaths(path);
    }

    public void log(String msg) {
        log.info(msg);
    }

    @Deprecated
    public void log(Exception exception, String msg) {
        this.log(msg, exception);
    }

    public void log(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    public Object getAttribute(String name) {
        return this.delegatee.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return this.delegatee.getAttributeNames();
    }

    public void removeAttribute(String name) {
        this.delegatee.removeAttribute(name);
    }

    public void setAttribute(String name, Object object) {
        this.delegatee.setAttribute(name, object);
    }

    public ServletContext getContext(String uripath) {
        return this.delegatee.getContext(uripath);
    }

    public String getInitParameter(String name) {
        return this.delegatee.getInitParameter(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return this.delegatee.getInitParameterNames();
    }

    public int getMajorVersion() {
        return this.delegatee.getMajorVersion();
    }

    public String getMimeType(String file) {
        return this.delegatee.getMimeType(file);
    }

    public int getMinorVersion() {
        return this.delegatee.getMinorVersion();
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return this.delegatee.getNamedDispatcher(name);
    }

    public String getRealPath(String path) {
        return this.delegatee.getRealPath(path);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return this.delegatee.getRequestDispatcher(path);
    }

    public String getServerInfo() {
        return this.delegatee.getServerInfo();
    }

    @Deprecated
    public Servlet getServlet(String name) throws ServletException {
        return this.delegatee.getServlet(name);
    }

    public String getServletContextName() {
        return this.delegatee.getServletContextName();
    }

    @Deprecated
    public Enumeration<String> getServletNames() {
        return this.delegatee.getServletNames();
    }

    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return this.delegatee.getServlets();
    }

    public String getContextPath() {
        return this.delegatee.getContextPath();
    }

    private URL getUrlForResource(String path) {
        int cs = path.indexOf(":/");
        if (cs > 0 && cs < path.length() - 2) {
            URL url;
            if ((cs += 2) < path.length() && path.charAt(cs) != '/') {
                path = path.substring(0, cs) + "/" + path.substring(cs);
            }
            if ((url = this.tcs.getTldLocationURL(path)) != null) {
                return url;
            }
            try {
                return new URL(path);
            }
            catch (MalformedURLException mue) {
                log.debug("getUrlForResource: Cannot create URL for {}: {}", (Object)path, (Object)mue.getMessage());
            }
        }
        return null;
    }

    public int getEffectiveMajorVersion() {
        return this.delegatee.getEffectiveMajorVersion();
    }

    public int getEffectiveMinorVersion() {
        return this.delegatee.getEffectiveMinorVersion();
    }

    public boolean setInitParameter(String name, String value) {
        return this.delegatee.setInitParameter(name, value);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return this.delegatee.addServlet(servletName, className);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return this.delegatee.addServlet(servletName, servlet);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return this.delegatee.addServlet(servletName, servletClass);
    }

    public ServletRegistration.Dynamic addJspFile(String s, String s1) {
        return this.delegatee.addJspFile(s, s1);
    }

    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return (T)this.delegatee.createServlet(clazz);
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return this.delegatee.getServletRegistration(servletName);
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return this.delegatee.getServletRegistrations();
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return this.delegatee.addFilter(filterName, className);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return this.delegatee.addFilter(filterName, filter);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return this.delegatee.addFilter(filterName, filterClass);
    }

    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return (T)this.delegatee.createFilter(clazz);
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return this.delegatee.getFilterRegistration(filterName);
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return this.delegatee.getFilterRegistrations();
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return this.delegatee.getSessionCookieConfig();
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        this.delegatee.setSessionTrackingModes(sessionTrackingModes);
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return this.delegatee.getDefaultSessionTrackingModes();
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return this.delegatee.getEffectiveSessionTrackingModes();
    }

    public void addListener(String className) {
        this.delegatee.addListener(className);
    }

    public <T extends EventListener> void addListener(T t) {
        this.delegatee.addListener(t);
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
        this.delegatee.addListener(listenerClass);
    }

    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return (T)this.delegatee.createListener(clazz);
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.delegatee.getJspConfigDescriptor();
    }

    public ClassLoader getClassLoader() {
        return this.delegatee.getClassLoader();
    }

    public void declareRoles(String ... roleNames) {
        this.delegatee.declareRoles(roleNames);
    }

    public String getVirtualServerName() {
        return this.delegatee.getVirtualServerName();
    }

    public int getSessionTimeout() {
        return this.delegatee.getSessionTimeout();
    }

    public void setSessionTimeout(int i) {
        this.delegatee.setSessionTimeout(i);
    }

    public String getRequestCharacterEncoding() {
        return this.delegatee.getRequestCharacterEncoding();
    }

    public void setRequestCharacterEncoding(String s) {
        this.delegatee.setRequestCharacterEncoding(s);
    }

    public String getResponseCharacterEncoding() {
        return this.delegatee.getResponseCharacterEncoding();
    }

    public void setResponseCharacterEncoding(String s) {
        this.delegatee.setResponseCharacterEncoding(s);
    }
}

