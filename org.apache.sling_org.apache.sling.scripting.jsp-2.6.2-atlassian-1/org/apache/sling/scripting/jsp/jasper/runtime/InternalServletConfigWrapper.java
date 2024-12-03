/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterRegistration
 *  javax.servlet.FilterRegistration$Dynamic
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRegistration
 *  javax.servlet.ServletRegistration$Dynamic
 *  javax.servlet.SessionCookieConfig
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.jsp.PageContext
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.jsp.PageContext;
import org.apache.sling.scripting.jsp.jasper.runtime.ExternalServletContextWrapper;

class InternalServletConfigWrapper
implements ServletConfig {
    private final ServletConfig delegate;
    private final PageContext pageContext;

    public InternalServletConfigWrapper(ServletConfig sc, PageContext pageContext) {
        this.delegate = sc;
        this.pageContext = pageContext;
    }

    public String getServletName() {
        return this.delegate.getServletName();
    }

    public ServletContext getServletContext() {
        return new InternalServletContextWrapper(this.delegate.getServletContext());
    }

    public String getInitParameter(String s) {
        return this.delegate.getInitParameter(s);
    }

    public Enumeration getInitParameterNames() {
        return this.delegate.getInitParameterNames();
    }

    class InternalServletContextWrapper
    implements ServletContext {
        private final ServletContext delegate;

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

        public InternalServletContextWrapper(ServletContext sc) {
            this.delegate = sc;
        }

        public ServletContext getContext(String s) {
            ServletContext sc = this.delegate.getContext(s);
            if (sc == this.delegate) {
                return this;
            }
            return new ExternalServletContextWrapper(sc, InternalServletConfigWrapper.this.pageContext);
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
            return this.delegate.getRequestDispatcher(s);
        }

        public RequestDispatcher getNamedDispatcher(String s) {
            return this.delegate.getNamedDispatcher(s);
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
    }
}

