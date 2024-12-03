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
 *  javax.servlet.ServletContextAttributeEvent
 *  javax.servlet.ServletContextAttributeListener
 *  javax.servlet.ServletContextListener
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRegistration
 *  javax.servlet.ServletRegistration$Dynamic
 *  javax.servlet.ServletRequestAttributeListener
 *  javax.servlet.ServletRequestListener
 *  javax.servlet.ServletSecurityElement
 *  javax.servlet.SessionCookieConfig
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.annotation.ServletSecurity
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.HttpSessionAttributeListener
 *  javax.servlet.http.HttpSessionIdListener
 *  javax.servlet.http.HttpSessionListener
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.CharChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.http.RequestUtil
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletSecurityElement;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.ApplicationDispatcher;
import org.apache.catalina.core.ApplicationFilterRegistration;
import org.apache.catalina.core.ApplicationMapping;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.core.ApplicationSessionCookieConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.mapper.MappingData;
import org.apache.catalina.util.Introspection;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;

public class ApplicationContext
implements ServletContext {
    protected static final boolean STRICT_SERVLET_COMPLIANCE = Globals.STRICT_SERVLET_COMPLIANCE;
    protected static final boolean GET_RESOURCE_REQUIRE_SLASH;
    protected Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    private final Map<String, String> readOnlyAttributes = new ConcurrentHashMap<String, String>();
    private final StandardContext context;
    private final Service service;
    private static final List<String> emptyString;
    private static final List<Servlet> emptyServlet;
    private final ServletContext facade = new ApplicationContextFacade(this);
    private final Map<String, String> parameters = new ConcurrentHashMap<String, String>();
    private static final StringManager sm;
    private final ThreadLocal<DispatchData> dispatchData = new ThreadLocal();
    private SessionCookieConfig sessionCookieConfig;
    private Set<SessionTrackingMode> sessionTrackingModes = null;
    private Set<SessionTrackingMode> defaultSessionTrackingModes = null;
    private Set<SessionTrackingMode> supportedSessionTrackingModes = null;
    private boolean newServletContextListenerAllowed = true;

    public ApplicationContext(StandardContext context) {
        this.context = context;
        this.service = ((Engine)context.getParent().getParent()).getService();
        this.sessionCookieConfig = new ApplicationSessionCookieConfig(context);
        this.populateSessionTrackingModes();
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        HashSet<String> names = new HashSet<String>(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    public ServletContext getContext(String uri) {
        if (uri == null || !uri.startsWith("/")) {
            return null;
        }
        Context child = null;
        try {
            Container host = this.context.getParent();
            child = (Context)host.findChild(uri);
            if (child != null && !child.getState().isAvailable()) {
                child = null;
            }
            if (child == null) {
                int i = uri.indexOf("##");
                if (i > -1) {
                    uri = uri.substring(0, i);
                }
                MessageBytes hostMB = MessageBytes.newInstance();
                hostMB.setString(host.getName());
                MessageBytes pathMB = MessageBytes.newInstance();
                pathMB.setString(uri);
                MappingData mappingData = new MappingData();
                this.service.getMapper().map(hostMB, pathMB, null, mappingData);
                child = mappingData.context;
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            return null;
        }
        if (child == null) {
            return null;
        }
        if (this.context.getCrossContext()) {
            return child.getServletContext();
        }
        if (child == this.context) {
            return this.context.getServletContext();
        }
        return null;
    }

    public String getContextPath() {
        return this.context.getPath();
    }

    public String getInitParameter(String name) {
        if ("org.apache.jasper.XML_VALIDATE_TLD".equals(name) && this.context.getTldValidation()) {
            return "true";
        }
        if ("org.apache.jasper.XML_BLOCK_EXTERNAL".equals(name) && !this.context.getXmlBlockExternal()) {
            return "false";
        }
        return this.parameters.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        HashSet<String> names = new HashSet<String>(this.parameters.keySet());
        if (this.context.getTldValidation()) {
            names.add("org.apache.jasper.XML_VALIDATE_TLD");
        }
        if (!this.context.getXmlBlockExternal()) {
            names.add("org.apache.jasper.XML_BLOCK_EXTERNAL");
        }
        return Collections.enumeration(names);
    }

    public int getMajorVersion() {
        return 4;
    }

    public int getMinorVersion() {
        return 0;
    }

    public String getMimeType(String file) {
        if (file == null) {
            return null;
        }
        int period = file.lastIndexOf(46);
        if (period < 0) {
            return null;
        }
        String extension = file.substring(period + 1);
        if (extension.length() < 1) {
            return null;
        }
        return this.context.findMimeMapping(extension);
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        if (name == null) {
            return null;
        }
        Wrapper wrapper = (Wrapper)this.context.findChild(name);
        if (wrapper == null) {
            return null;
        }
        return new ApplicationDispatcher(wrapper, null, null, null, null, null, name);
    }

    public String getRealPath(String path) {
        String validatedPath = this.validateResourcePath(path, true);
        return this.context.getRealPath(validatedPath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        String queryString;
        String uri;
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.requestDispatcher.iae", new Object[]{path}));
        }
        int pos = path.indexOf(63);
        if (pos >= 0) {
            uri = path.substring(0, pos);
            queryString = path.substring(pos + 1);
        } else {
            uri = path;
            queryString = null;
        }
        String uriNoParams = ApplicationContext.stripPathParams(uri);
        String normalizedUri = RequestUtil.normalize((String)uriNoParams);
        if (normalizedUri == null) {
            return null;
        }
        if (this.getContext().getDispatchersUseEncodedPaths()) {
            String decodedUri = UDecoder.URLDecode((String)normalizedUri, (Charset)StandardCharsets.UTF_8);
            if (!decodedUri.equals(normalizedUri = RequestUtil.normalize((String)decodedUri))) {
                this.getContext().getLogger().warn((Object)sm.getString("applicationContext.illegalDispatchPath", new Object[]{path}), (Throwable)new IllegalArgumentException());
                return null;
            }
            uri = URLEncoder.DEFAULT.encode(this.getContextPath(), StandardCharsets.UTF_8) + uri;
        } else {
            uri = URLEncoder.DEFAULT.encode(this.getContextPath() + uri, StandardCharsets.UTF_8);
        }
        DispatchData dd = this.dispatchData.get();
        if (dd == null) {
            dd = new DispatchData();
            this.dispatchData.set(dd);
        }
        MessageBytes uriMB = dd.uriMB;
        MappingData mappingData = dd.mappingData;
        try {
            uriMB.setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
            CharChunk uriCC = uriMB.getCharChunk();
            try {
                uriCC.append(this.context.getPath());
                uriCC.append(normalizedUri);
                this.service.getMapper().map(this.context, uriMB, mappingData);
                if (mappingData.wrapper == null) {
                    RequestDispatcher requestDispatcher = null;
                    return requestDispatcher;
                }
            }
            catch (Exception e) {
                this.log(sm.getString("applicationContext.mapping.error"), e);
                RequestDispatcher requestDispatcher = null;
                return requestDispatcher;
            }
            Wrapper wrapper = mappingData.wrapper;
            String wrapperPath = mappingData.wrapperPath.toString();
            String pathInfo = mappingData.pathInfo.toString();
            HttpServletMapping mapping = new ApplicationMapping(mappingData).getHttpServletMapping();
            ApplicationDispatcher applicationDispatcher = new ApplicationDispatcher(wrapper, uri, wrapperPath, pathInfo, queryString, mapping, null);
            return applicationDispatcher;
        }
        finally {
            uriMB.recycle();
            mappingData.recycle();
        }
    }

    static String stripPathParams(String input) {
        if (input.indexOf(59) < 0) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length());
        int pos = 0;
        int limit = input.length();
        while (pos < limit) {
            int nextSemiColon = input.indexOf(59, pos);
            if (nextSemiColon < 0) {
                nextSemiColon = limit;
            }
            sb.append(input.substring(pos, nextSemiColon));
            int followingSlash = input.indexOf(47, nextSemiColon);
            if (followingSlash < 0) {
                pos = limit;
                continue;
            }
            pos = followingSlash;
        }
        return sb.toString();
    }

    public URL getResource(String path) throws MalformedURLException {
        String validatedPath = this.validateResourcePath(path, !GET_RESOURCE_REQUIRE_SLASH);
        if (validatedPath == null) {
            throw new MalformedURLException(sm.getString("applicationContext.requestDispatcher.iae", new Object[]{path}));
        }
        WebResourceRoot resources = this.context.getResources();
        if (resources != null) {
            return resources.getResource(validatedPath).getURL();
        }
        return null;
    }

    public InputStream getResourceAsStream(String path) {
        String validatedPath = this.validateResourcePath(path, !GET_RESOURCE_REQUIRE_SLASH);
        if (validatedPath == null) {
            return null;
        }
        WebResourceRoot resources = this.context.getResources();
        if (resources != null) {
            return resources.getResource(validatedPath).getInputStream();
        }
        return null;
    }

    private String validateResourcePath(String path, boolean addMissingInitialSlash) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            if (addMissingInitialSlash) {
                return "/" + path;
            }
            return null;
        }
        return path;
    }

    public Set<String> getResourcePaths(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.resourcePaths.iae", new Object[]{path}));
        }
        WebResourceRoot resources = this.context.getResources();
        if (resources != null) {
            return resources.listWebAppPaths(path);
        }
        return null;
    }

    public String getServerInfo() {
        return ServerInfo.getServerInfo();
    }

    @Deprecated
    public Servlet getServlet(String name) {
        return null;
    }

    public String getServletContextName() {
        return this.context.getDisplayName();
    }

    @Deprecated
    public Enumeration<String> getServletNames() {
        return Collections.enumeration(emptyString);
    }

    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return Collections.enumeration(emptyServlet);
    }

    public void log(String message) {
        this.context.getLogger().info((Object)message);
    }

    @Deprecated
    public void log(Exception exception, String message) {
        this.context.getLogger().error((Object)message, (Throwable)exception);
    }

    public void log(String message, Throwable throwable) {
        this.context.getLogger().error((Object)message, throwable);
    }

    public void removeAttribute(String name) {
        Object value = null;
        if (this.readOnlyAttributes.containsKey(name)) {
            return;
        }
        value = this.attributes.remove(name);
        if (value == null) {
            return;
        }
        Object[] listeners = this.context.getApplicationEventListeners();
        if (listeners == null || listeners.length == 0) {
            return;
        }
        ServletContextAttributeEvent event = new ServletContextAttributeEvent(this.context.getServletContext(), name, value);
        for (Object obj : listeners) {
            if (!(obj instanceof ServletContextAttributeListener)) continue;
            ServletContextAttributeListener listener = (ServletContextAttributeListener)obj;
            try {
                this.context.fireContainerEvent("beforeContextAttributeRemoved", listener);
                listener.attributeRemoved(event);
                this.context.fireContainerEvent("afterContextAttributeRemoved", listener);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.context.fireContainerEvent("afterContextAttributeRemoved", listener);
                this.log(sm.getString("applicationContext.attributeEvent"), t);
            }
        }
    }

    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new NullPointerException(sm.getString("applicationContext.setAttribute.namenull"));
        }
        if (value == null) {
            this.removeAttribute(name);
            return;
        }
        if (this.readOnlyAttributes.containsKey(name)) {
            return;
        }
        Object oldValue = this.attributes.put(name, value);
        boolean replaced = oldValue != null;
        Object[] listeners = this.context.getApplicationEventListeners();
        if (listeners == null || listeners.length == 0) {
            return;
        }
        ServletContextAttributeEvent event = null;
        event = replaced ? new ServletContextAttributeEvent(this.context.getServletContext(), name, oldValue) : new ServletContextAttributeEvent(this.context.getServletContext(), name, value);
        for (Object obj : listeners) {
            if (!(obj instanceof ServletContextAttributeListener)) continue;
            ServletContextAttributeListener listener = (ServletContextAttributeListener)obj;
            try {
                if (replaced) {
                    this.context.fireContainerEvent("beforeContextAttributeReplaced", listener);
                    listener.attributeReplaced(event);
                    this.context.fireContainerEvent("afterContextAttributeReplaced", listener);
                    continue;
                }
                this.context.fireContainerEvent("beforeContextAttributeAdded", listener);
                listener.attributeAdded(event);
                this.context.fireContainerEvent("afterContextAttributeAdded", listener);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                if (replaced) {
                    this.context.fireContainerEvent("afterContextAttributeReplaced", listener);
                } else {
                    this.context.fireContainerEvent("afterContextAttributeAdded", listener);
                }
                this.log(sm.getString("applicationContext.attributeEvent"), t);
            }
        }
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return this.addFilter(filterName, className, null);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return this.addFilter(filterName, null, filter);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return this.addFilter(filterName, filterClass.getName(), null);
    }

    private FilterRegistration.Dynamic addFilter(String filterName, String filterClass, Filter filter) throws IllegalStateException {
        if (filterName == null || filterName.equals("")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.invalidFilterName", new Object[]{filterName}));
        }
        this.checkState("applicationContext.addFilter.ise");
        FilterDef filterDef = this.context.findFilterDef(filterName);
        if (filterDef == null) {
            filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            this.context.addFilterDef(filterDef);
        } else if (filterDef.getFilterName() != null && filterDef.getFilterClass() != null) {
            return null;
        }
        if (filter == null) {
            filterDef.setFilterClass(filterClass);
        } else {
            filterDef.setFilterClass(filter.getClass().getName());
            filterDef.setFilter(filter);
        }
        return new ApplicationFilterRegistration(filterDef, this.context);
    }

    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        try {
            Filter filter = (Filter)this.context.getInstanceManager().newInstance(c.getName());
            return (T)filter;
        }
        catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable((Throwable)e.getCause());
            throw new ServletException((Throwable)e);
        }
        catch (ReflectiveOperationException | NamingException e) {
            throw new ServletException((Throwable)e);
        }
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        FilterDef filterDef = this.context.findFilterDef(filterName);
        if (filterDef == null) {
            return null;
        }
        return new ApplicationFilterRegistration(filterDef, this.context);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return this.addServlet(servletName, className, null, null);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return this.addServlet(servletName, null, servlet, null);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return this.addServlet(servletName, servletClass.getName(), null, null);
    }

    public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        if (jspFile == null || !jspFile.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addJspFile.iae", new Object[]{jspFile}));
        }
        String jspServletClassName = null;
        HashMap<String, String> jspFileInitParams = new HashMap<String, String>();
        Wrapper jspServlet = (Wrapper)this.context.findChild("jsp");
        if (jspServlet == null) {
            jspServletClassName = "org.apache.jasper.servlet.JspServlet";
        } else {
            String[] params;
            jspServletClassName = jspServlet.getServletClass();
            for (String param : params = jspServlet.findInitParameters()) {
                jspFileInitParams.put(param, jspServlet.findInitParameter(param));
            }
        }
        jspFileInitParams.put("jspFile", jspFile);
        return this.addServlet(jspName, jspServletClassName, null, jspFileInitParams);
    }

    private ServletRegistration.Dynamic addServlet(String servletName, String servletClass, Servlet servlet, Map<String, String> initParams) throws IllegalStateException {
        if (servletName == null || servletName.equals("")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.invalidServletName", new Object[]{servletName}));
        }
        this.checkState("applicationContext.addServlet.ise");
        Wrapper wrapper = (Wrapper)this.context.findChild(servletName);
        if (wrapper == null) {
            wrapper = this.context.createWrapper();
            wrapper.setName(servletName);
            this.context.addChild(wrapper);
        } else if (wrapper.getName() != null && wrapper.getServletClass() != null) {
            if (wrapper.isOverridable()) {
                wrapper.setOverridable(false);
            } else {
                return null;
            }
        }
        ServletSecurity annotation = null;
        if (servlet == null) {
            wrapper.setServletClass(servletClass);
            Class<?> clazz = Introspection.loadClass(this.context, servletClass);
            if (clazz != null) {
                annotation = clazz.getAnnotation(ServletSecurity.class);
            }
        } else {
            wrapper.setServletClass(servlet.getClass().getName());
            wrapper.setServlet(servlet);
            if (this.context.wasCreatedDynamicServlet(servlet)) {
                annotation = servlet.getClass().getAnnotation(ServletSecurity.class);
            }
        }
        if (initParams != null) {
            for (Map.Entry entry : initParams.entrySet()) {
                wrapper.addInitParameter((String)entry.getKey(), (String)entry.getValue());
            }
        }
        ApplicationServletRegistration registration = new ApplicationServletRegistration(wrapper, this.context);
        if (annotation != null) {
            registration.setServletSecurity(new ServletSecurityElement(annotation));
        }
        return registration;
    }

    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        try {
            Servlet servlet = (Servlet)this.context.getInstanceManager().newInstance(c.getName());
            this.context.dynamicServletCreated(servlet);
            return (T)servlet;
        }
        catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable((Throwable)e.getCause());
            throw new ServletException((Throwable)e);
        }
        catch (ReflectiveOperationException | NamingException e) {
            throw new ServletException((Throwable)e);
        }
    }

    public ServletRegistration getServletRegistration(String servletName) {
        Wrapper wrapper = (Wrapper)this.context.findChild(servletName);
        if (wrapper == null) {
            return null;
        }
        return new ApplicationServletRegistration(wrapper, this.context);
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return this.defaultSessionTrackingModes;
    }

    private void populateSessionTrackingModes() {
        Connector[] connectors;
        this.defaultSessionTrackingModes = EnumSet.of(SessionTrackingMode.URL);
        this.supportedSessionTrackingModes = EnumSet.of(SessionTrackingMode.URL);
        if (this.context.getCookies()) {
            this.defaultSessionTrackingModes.add(SessionTrackingMode.COOKIE);
            this.supportedSessionTrackingModes.add(SessionTrackingMode.COOKIE);
        }
        for (Connector connector : connectors = this.service.findConnectors()) {
            if (!Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) continue;
            this.supportedSessionTrackingModes.add(SessionTrackingMode.SSL);
            break;
        }
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        if (this.sessionTrackingModes != null) {
            return this.sessionTrackingModes;
        }
        return this.defaultSessionTrackingModes;
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return this.sessionCookieConfig;
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        this.checkState("applicationContext.setSessionTracking.ise");
        for (SessionTrackingMode sessionTrackingMode : sessionTrackingModes) {
            if (this.supportedSessionTrackingModes.contains(sessionTrackingMode)) continue;
            throw new IllegalArgumentException(sm.getString("applicationContext.setSessionTracking.iae.invalid", new Object[]{sessionTrackingMode.toString(), this.getContextPath()}));
        }
        if (sessionTrackingModes.contains(SessionTrackingMode.SSL) && sessionTrackingModes.size() > 1) {
            throw new IllegalArgumentException(sm.getString("applicationContext.setSessionTracking.iae.ssl", new Object[]{this.getContextPath()}));
        }
        this.sessionTrackingModes = sessionTrackingModes;
    }

    public boolean setInitParameter(String name, String value) {
        if (name == null) {
            throw new NullPointerException(sm.getString("applicationContext.setAttribute.namenull"));
        }
        this.checkState("applicationContext.setInitParam.ise");
        return this.parameters.putIfAbsent(name, value) == null;
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
        EventListener listener;
        try {
            listener = this.createListener(listenerClass);
        }
        catch (ServletException e) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.init", new Object[]{listenerClass.getName()}), e);
        }
        this.addListener(listener);
    }

    public void addListener(String className) {
        try {
            if (this.context.getInstanceManager() != null) {
                Object obj = this.context.getInstanceManager().newInstance(className);
                if (!(obj instanceof EventListener)) {
                    throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", new Object[]{className}));
                }
                EventListener listener = (EventListener)obj;
                this.addListener(listener);
            }
        }
        catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable((Throwable)e.getCause());
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.cnfe", new Object[]{className}), e);
        }
        catch (ReflectiveOperationException | NamingException e) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.cnfe", new Object[]{className}), e);
        }
    }

    public <T extends EventListener> void addListener(T t) {
        this.checkState("applicationContext.addListener.ise");
        boolean match = false;
        if (t instanceof ServletContextAttributeListener || t instanceof ServletRequestListener || t instanceof ServletRequestAttributeListener || t instanceof HttpSessionIdListener || t instanceof HttpSessionAttributeListener) {
            this.context.addApplicationEventListener(t);
            match = true;
        }
        if (t instanceof HttpSessionListener || t instanceof ServletContextListener && this.newServletContextListenerAllowed) {
            this.context.addApplicationLifecycleListener(t);
            match = true;
        }
        if (match) {
            return;
        }
        if (t instanceof ServletContextListener) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.sclNotAllowed", new Object[]{t.getClass().getName()}));
        }
        throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", new Object[]{t.getClass().getName()}));
    }

    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        try {
            EventListener listener = (EventListener)this.context.getInstanceManager().newInstance(c);
            if (listener instanceof ServletContextListener || listener instanceof ServletContextAttributeListener || listener instanceof ServletRequestListener || listener instanceof ServletRequestAttributeListener || listener instanceof HttpSessionListener || listener instanceof HttpSessionIdListener || listener instanceof HttpSessionAttributeListener) {
                return (T)listener;
            }
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", new Object[]{listener.getClass().getName()}));
        }
        catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable((Throwable)e.getCause());
            throw new ServletException((Throwable)e);
        }
        catch (ReflectiveOperationException | NamingException e) {
            throw new ServletException((Throwable)e);
        }
    }

    public void declareRoles(String ... roleNames) {
        this.checkState("applicationContext.addRole.ise");
        if (roleNames == null) {
            throw new IllegalArgumentException(sm.getString("applicationContext.roles.iae", new Object[]{this.getContextPath()}));
        }
        for (String role : roleNames) {
            if (role == null || role.isEmpty()) {
                throw new IllegalArgumentException(sm.getString("applicationContext.role.iae", new Object[]{this.getContextPath()}));
            }
            this.context.addSecurityRole(role);
        }
    }

    public ClassLoader getClassLoader() {
        ClassLoader result = this.context.getLoader().getClassLoader();
        if (Globals.IS_SECURITY_ENABLED) {
            ClassLoader parent;
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            for (parent = result; parent != null && parent != tccl; parent = parent.getParent()) {
            }
            if (parent == null) {
                System.getSecurityManager().checkPermission(new RuntimePermission("getClassLoader"));
            }
        }
        return result;
    }

    public int getEffectiveMajorVersion() {
        return this.context.getEffectiveMajorVersion();
    }

    public int getEffectiveMinorVersion() {
        return this.context.getEffectiveMinorVersion();
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        FilterDef[] filterDefs;
        HashMap<String, ApplicationFilterRegistration> result = new HashMap<String, ApplicationFilterRegistration>();
        for (FilterDef filterDef : filterDefs = this.context.findFilterDefs()) {
            result.put(filterDef.getFilterName(), new ApplicationFilterRegistration(filterDef, this.context));
        }
        return result;
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.context.getJspConfigDescriptor();
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        Container[] wrappers;
        HashMap<String, ApplicationServletRegistration> result = new HashMap<String, ApplicationServletRegistration>();
        for (Container wrapper : wrappers = this.context.findChildren()) {
            result.put(wrapper.getName(), new ApplicationServletRegistration((Wrapper)wrapper, this.context));
        }
        return result;
    }

    public String getVirtualServerName() {
        Container host = this.context.getParent();
        Container engine = host.getParent();
        return engine.getName() + "/" + host.getName();
    }

    public int getSessionTimeout() {
        return this.context.getSessionTimeout();
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.checkState("applicationContext.setSessionTimeout.ise");
        this.context.setSessionTimeout(sessionTimeout);
    }

    public String getRequestCharacterEncoding() {
        return this.context.getRequestCharacterEncoding();
    }

    public void setRequestCharacterEncoding(String encoding) {
        this.checkState("applicationContext.setRequestEncoding.ise");
        this.context.setRequestCharacterEncoding(encoding);
    }

    public String getResponseCharacterEncoding() {
        return this.context.getResponseCharacterEncoding();
    }

    public void setResponseCharacterEncoding(String encoding) {
        this.checkState("applicationContext.setResponseEncoding.ise");
        this.context.setResponseCharacterEncoding(encoding);
    }

    private void checkState(String messageKey) {
        if (!this.context.getState().equals((Object)LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString(messageKey, new Object[]{this.getContextPath()}));
        }
    }

    protected StandardContext getContext() {
        return this.context;
    }

    protected void clearAttributes() {
        ArrayList<String> list = new ArrayList<String>(this.attributes.keySet());
        for (String key : list) {
            this.removeAttribute(key);
        }
    }

    protected ServletContext getFacade() {
        return this.facade;
    }

    void setAttributeReadOnly(String name) {
        if (this.attributes.containsKey(name)) {
            this.readOnlyAttributes.put(name, name);
        }
    }

    protected void setNewServletContextListenerAllowed(boolean allowed) {
        this.newServletContextListenerAllowed = allowed;
    }

    static {
        String requireSlash = System.getProperty("org.apache.catalina.core.ApplicationContext.GET_RESOURCE_REQUIRE_SLASH");
        GET_RESOURCE_REQUIRE_SLASH = requireSlash == null ? STRICT_SERVLET_COMPLIANCE : Boolean.parseBoolean(requireSlash);
        emptyString = Collections.emptyList();
        emptyServlet = Collections.emptyList();
        sm = StringManager.getManager(ApplicationContext.class);
    }

    private static final class DispatchData {
        public MessageBytes uriMB = MessageBytes.newInstance();
        public MappingData mappingData;

        DispatchData() {
            CharChunk uriCC = this.uriMB.getCharChunk();
            uriCC.setLimit(-1);
            this.mappingData = new MappingData();
        }
    }
}

