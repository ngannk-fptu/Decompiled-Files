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
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.SessionCookieConfig
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.PageContext
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.io.IOException;
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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;
import org.apache.sling.scripting.jsp.jasper.compiler.JspRuntimeContext;

class ExternalServletContextWrapper
implements ServletContext {
    private final ServletContext delegate;
    private final PageContext pageContext;

    public int getEffectiveMajorVersion() {
        return this.delegate.getEffectiveMajorVersion();
    }

    public int getEffectiveMinorVersion() {
        return this.delegate.getEffectiveMinorVersion();
    }

    public boolean setInitParameter(String name, String value) {
        return this.delegate.setInitParameter(name, value);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return this.delegate.addServlet(servletName, className);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return this.delegate.addServlet(servletName, servlet);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return this.delegate.addServlet(servletName, servletClass);
    }

    public ServletRegistration.Dynamic addJspFile(String s, String s1) {
        return this.delegate.addJspFile(s, s1);
    }

    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return (T)this.delegate.createServlet(clazz);
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return this.delegate.getServletRegistration(servletName);
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return this.delegate.getServletRegistrations();
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return this.delegate.addFilter(filterName, className);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return this.delegate.addFilter(filterName, filter);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return this.delegate.addFilter(filterName, filterClass);
    }

    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return (T)this.delegate.createFilter(clazz);
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return this.delegate.getFilterRegistration(filterName);
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return this.delegate.getFilterRegistrations();
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return this.delegate.getSessionCookieConfig();
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        this.delegate.setSessionTrackingModes(sessionTrackingModes);
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return this.delegate.getDefaultSessionTrackingModes();
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return this.delegate.getEffectiveSessionTrackingModes();
    }

    public void addListener(String className) {
        this.delegate.addListener(className);
    }

    public <T extends EventListener> void addListener(T t) {
        this.delegate.addListener(t);
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
        this.delegate.addListener(listenerClass);
    }

    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return (T)this.delegate.createListener(clazz);
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.delegate.getJspConfigDescriptor();
    }

    public ClassLoader getClassLoader() {
        return this.delegate.getClassLoader();
    }

    public void declareRoles(String ... roleNames) {
        this.delegate.declareRoles(roleNames);
    }

    public String getVirtualServerName() {
        return this.delegate.getVirtualServerName();
    }

    public int getSessionTimeout() {
        return this.delegate.getSessionTimeout();
    }

    public void setSessionTimeout(int i) {
        this.delegate.setSessionTimeout(i);
    }

    public String getRequestCharacterEncoding() {
        return this.delegate.getRequestCharacterEncoding();
    }

    public void setRequestCharacterEncoding(String s) {
        this.delegate.setRequestCharacterEncoding(s);
    }

    public String getResponseCharacterEncoding() {
        return this.delegate.getResponseCharacterEncoding();
    }

    public void setResponseCharacterEncoding(String s) {
        this.delegate.setResponseCharacterEncoding(s);
    }

    public ExternalServletContextWrapper(ServletContext sc, PageContext pageContext) {
        this.delegate = sc;
        this.pageContext = pageContext;
    }

    public ServletContext getContext(String s) {
        return this.delegate.getContext(s);
    }

    public String getContextPath() {
        return this.delegate.getContextPath();
    }

    public int getMajorVersion() {
        return this.delegate.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.delegate.getMinorVersion();
    }

    public String getMimeType(String s) {
        return this.delegate.getMimeType(s);
    }

    public Set<String> getResourcePaths(String s) {
        return this.delegate.getResourcePaths(s);
    }

    public URL getResource(String s) throws MalformedURLException {
        return this.delegate.getResource(s);
    }

    public InputStream getResourceAsStream(String s) {
        return this.delegate.getResourceAsStream(s);
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return new RequestDispatcherWrapper(this.delegate.getRequestDispatcher(s));
    }

    public RequestDispatcher getNamedDispatcher(String s) {
        return new RequestDispatcherWrapper(this.delegate.getNamedDispatcher(s));
    }

    public Servlet getServlet(String s) throws ServletException {
        return this.delegate.getServlet(s);
    }

    public Enumeration<Servlet> getServlets() {
        return this.delegate.getServlets();
    }

    public Enumeration<String> getServletNames() {
        return this.delegate.getServletNames();
    }

    public void log(String s) {
        this.delegate.log(s);
    }

    public void log(Exception exception, String s) {
        this.delegate.log(exception, s);
    }

    public void log(String s, Throwable throwable) {
        this.delegate.log(s, throwable);
    }

    public String getRealPath(String s) {
        return this.delegate.getRealPath(s);
    }

    public String getServerInfo() {
        return this.delegate.getServerInfo();
    }

    public String getInitParameter(String s) {
        return this.delegate.getInitParameter(s);
    }

    public Enumeration<String> getInitParameterNames() {
        return this.delegate.getInitParameterNames();
    }

    public Object getAttribute(String s) {
        return this.delegate.getAttribute(s);
    }

    public Enumeration<String> getAttributeNames() {
        return this.delegate.getAttributeNames();
    }

    public void setAttribute(String s, Object obj) {
        this.delegate.setAttribute(s, obj);
    }

    public void removeAttribute(String s) {
        this.delegate.removeAttribute(s);
    }

    public String getServletContextName() {
        return this.delegate.getServletContextName();
    }

    class RequestDispatcherWrapper
    implements RequestDispatcher {
        private final RequestDispatcher delegate;

        public RequestDispatcherWrapper(RequestDispatcher rd) {
            this.delegate = rd;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            JspFactory jspFactory = JspFactory.getDefaultFactory();
            if (jspFactory instanceof JspRuntimeContext.JspFactoryHandler) {
                ExternalServletContextWrapper.this.pageContext.getOut().flush();
                int count = ((JspRuntimeContext.JspFactoryHandler)jspFactory).resetUsage();
                try {
                    this.delegate.forward(request, response);
                }
                finally {
                    ((JspRuntimeContext.JspFactoryHandler)jspFactory).setUsage(count);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            JspFactory jspFactory = JspFactory.getDefaultFactory();
            if (jspFactory instanceof JspRuntimeContext.JspFactoryHandler) {
                ExternalServletContextWrapper.this.pageContext.getOut().flush();
                int count = ((JspRuntimeContext.JspFactoryHandler)jspFactory).resetUsage();
                try {
                    this.delegate.include(request, response);
                }
                finally {
                    ((JspRuntimeContext.JspFactoryHandler)jspFactory).setUsage(count);
                }
            }
        }
    }
}

