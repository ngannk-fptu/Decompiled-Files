/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.FileTypeMap
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
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.DefaultResourceLoader
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package com.atlassian.springframework.mock.web;

import com.atlassian.springframework.mock.web.MockRequestDispatcher;
import com.atlassian.springframework.mock.web.MockSessionCookieConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.activation.FileTypeMap;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class MockServletContext
implements ServletContext {
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
    private static final String TEMP_DIR_SYSTEM_PROPERTY = "java.io.tmpdir";
    private static final Set<SessionTrackingMode> DEFAULT_SESSION_TRACKING_MODES = new LinkedHashSet<SessionTrackingMode>(3);
    private final Log logger = LogFactory.getLog(this.getClass());
    private final ResourceLoader resourceLoader;
    private final String resourceBasePath;
    private final Map<String, ServletContext> contexts = new HashMap<String, ServletContext>();
    private final Map<String, RequestDispatcher> namedRequestDispatchers = new HashMap<String, RequestDispatcher>();
    private final Map<String, String> initParameters = new LinkedHashMap<String, String>();
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
    private final Set<String> declaredRoles = new HashSet<String>();
    private final SessionCookieConfig sessionCookieConfig = new MockSessionCookieConfig();
    private String contextPath = "";
    private int majorVersion = 3;
    private int minorVersion = 0;
    private int effectiveMajorVersion = 3;
    private int effectiveMinorVersion = 0;
    private String defaultServletName = "default";
    private String servletContextName = "MockServletContext";
    private Set<SessionTrackingMode> sessionTrackingModes;

    public MockServletContext() {
        this("", null);
    }

    public MockServletContext(String resourceBasePath) {
        this(resourceBasePath, null);
    }

    public MockServletContext(ResourceLoader resourceLoader) {
        this("", resourceLoader);
    }

    public MockServletContext(String resourceBasePath, ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
        this.resourceBasePath = resourceBasePath != null ? resourceBasePath : "";
        String tempDir = System.getProperty(TEMP_DIR_SYSTEM_PROPERTY);
        if (tempDir != null) {
            this.attributes.put("javax.servlet.context.tempdir", new File(tempDir));
        }
        this.registerNamedDispatcher(this.defaultServletName, new MockRequestDispatcher(this.defaultServletName));
    }

    protected String getResourceLocation(String path) {
        if (!((String)path).startsWith("/")) {
            path = "/" + (String)path;
        }
        return this.resourceBasePath + (String)path;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath != null ? contextPath : "";
    }

    public void registerContext(String contextPath, ServletContext context) {
        this.contexts.put(contextPath, context);
    }

    public ServletContext getContext(String contextPath) {
        if (this.contextPath.equals(contextPath)) {
            return this;
        }
        return this.contexts.get(contextPath);
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getEffectiveMajorVersion() {
        return this.effectiveMajorVersion;
    }

    public void setEffectiveMajorVersion(int effectiveMajorVersion) {
        this.effectiveMajorVersion = effectiveMajorVersion;
    }

    public int getEffectiveMinorVersion() {
        return this.effectiveMinorVersion;
    }

    public void setEffectiveMinorVersion(int effectiveMinorVersion) {
        this.effectiveMinorVersion = effectiveMinorVersion;
    }

    public String getMimeType(String filePath) {
        String mimeType = MimeTypeResolver.getMimeType(filePath);
        return "application/octet-stream".equals(mimeType) ? null : mimeType;
    }

    public Set<String> getResourcePaths(String path) {
        Object actualPath = path.endsWith("/") ? path : path + "/";
        Resource resource = this.resourceLoader.getResource(this.getResourceLocation((String)actualPath));
        try {
            File file = resource.getFile();
            Object[] fileList = file.list();
            if (ObjectUtils.isEmpty((Object[])fileList)) {
                return null;
            }
            LinkedHashSet<String> resourcePaths = new LinkedHashSet<String>(fileList.length);
            for (Object fileEntry : fileList) {
                String resultPath = (String)actualPath + (String)fileEntry;
                if (resource.createRelative((String)fileEntry).getFile().isDirectory()) {
                    resultPath = resultPath + "/";
                }
                resourcePaths.add(resultPath);
            }
            return resourcePaths;
        }
        catch (IOException ex) {
            this.logger.warn((Object)("Couldn't get resource paths for " + resource), (Throwable)ex);
            return null;
        }
    }

    public URL getResource(String path) throws MalformedURLException {
        Resource resource = this.resourceLoader.getResource(this.getResourceLocation(path));
        if (!resource.exists()) {
            return null;
        }
        try {
            return resource.getURL();
        }
        catch (MalformedURLException ex) {
            throw ex;
        }
        catch (IOException ex) {
            this.logger.warn((Object)("Couldn't get URL for " + resource), (Throwable)ex);
            return null;
        }
    }

    public InputStream getResourceAsStream(String path) {
        Resource resource = this.resourceLoader.getResource(this.getResourceLocation(path));
        if (!resource.exists()) {
            return null;
        }
        try {
            return resource.getInputStream();
        }
        catch (IOException ex) {
            this.logger.warn((Object)("Couldn't open InputStream for " + resource), (Throwable)ex);
            return null;
        }
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("RequestDispatcher path at ServletContext level must start with '/'");
        }
        return new MockRequestDispatcher(path);
    }

    public RequestDispatcher getNamedDispatcher(String path) {
        return this.namedRequestDispatchers.get(path);
    }

    public void registerNamedDispatcher(String name, RequestDispatcher requestDispatcher) {
        Assert.notNull((Object)name, (String)"RequestDispatcher name must not be null");
        Assert.notNull((Object)requestDispatcher, (String)"RequestDispatcher must not be null");
        this.namedRequestDispatchers.put(name, requestDispatcher);
    }

    public void unregisterNamedDispatcher(String name) {
        Assert.notNull((Object)name, (String)"RequestDispatcher name must not be null");
        this.namedRequestDispatchers.remove(name);
    }

    public String getDefaultServletName() {
        return this.defaultServletName;
    }

    public void setDefaultServletName(String defaultServletName) {
        Assert.hasText((String)defaultServletName, (String)"defaultServletName must not be null or empty");
        this.unregisterNamedDispatcher(this.defaultServletName);
        this.defaultServletName = defaultServletName;
        this.registerNamedDispatcher(this.defaultServletName, new MockRequestDispatcher(this.defaultServletName));
    }

    @Deprecated
    public Servlet getServlet(String name) {
        return null;
    }

    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return Collections.enumeration(new HashSet());
    }

    @Deprecated
    public Enumeration<String> getServletNames() {
        return Collections.enumeration(new HashSet());
    }

    public void log(String message) {
        this.logger.info((Object)message);
    }

    @Deprecated
    public void log(Exception ex, String message) {
        this.logger.info((Object)message, (Throwable)ex);
    }

    public void log(String message, Throwable ex) {
        this.logger.info((Object)message, ex);
    }

    public String getRealPath(String path) {
        Resource resource = this.resourceLoader.getResource(this.getResourceLocation(path));
        try {
            return resource.getFile().getAbsolutePath();
        }
        catch (IOException ex) {
            this.logger.warn((Object)("Couldn't determine real path of resource " + resource), (Throwable)ex);
            return null;
        }
    }

    public String getServerInfo() {
        return "MockServletContext";
    }

    public String getInitParameter(String name) {
        Assert.notNull((Object)name, (String)"Parameter name must not be null");
        return this.initParameters.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.initParameters.keySet());
    }

    public boolean setInitParameter(String name, String value) {
        Assert.notNull((Object)name, (String)"Parameter name must not be null");
        if (this.initParameters.containsKey(name)) {
            return false;
        }
        this.initParameters.put(name, value);
        return true;
    }

    public void addInitParameter(String name, String value) {
        Assert.notNull((Object)name, (String)"Parameter name must not be null");
        this.initParameters.put(name, value);
    }

    public Object getAttribute(String name) {
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        return this.attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(new LinkedHashSet<String>(this.attributes.keySet()));
    }

    public void setAttribute(String name, Object value) {
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            this.attributes.remove(name);
        }
    }

    public void removeAttribute(String name) {
        Assert.notNull((Object)name, (String)"Attribute name must not be null");
        this.attributes.remove(name);
    }

    public String getServletContextName() {
        return this.servletContextName;
    }

    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    public ClassLoader getClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }

    public void declareRoles(String ... roleNames) {
        Assert.notNull((Object)roleNames, (String)"Role names array must not be null");
        for (String roleName : roleNames) {
            Assert.hasLength((String)roleName, (String)"Role name must not be empty");
            this.declaredRoles.add(roleName);
        }
    }

    public Set<String> getDeclaredRoles() {
        return Collections.unmodifiableSet(this.declaredRoles);
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) throws IllegalStateException, IllegalArgumentException {
        this.sessionTrackingModes = sessionTrackingModes;
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return DEFAULT_SESSION_TRACKING_MODES;
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return this.sessionTrackingModes != null ? Collections.unmodifiableSet(this.sessionTrackingModes) : DEFAULT_SESSION_TRACKING_MODES;
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return this.sessionCookieConfig;
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException();
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        throw new UnsupportedOperationException();
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        throw new UnsupportedOperationException();
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        throw new UnsupportedOperationException();
    }

    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return Collections.emptyMap();
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

    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return Collections.emptyMap();
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
        throw new UnsupportedOperationException();
    }

    public void addListener(String className) {
        throw new UnsupportedOperationException();
    }

    public <T extends EventListener> void addListener(T t) {
        throw new UnsupportedOperationException();
    }

    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        throw new UnsupportedOperationException();
    }

    public String getVirtualServerName() {
        return null;
    }

    static {
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.COOKIE);
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.URL);
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.SSL);
    }

    private static class MimeTypeResolver {
        private MimeTypeResolver() {
        }

        public static String getMimeType(String filePath) {
            return FileTypeMap.getDefaultFileTypeMap().getContentType(filePath);
        }
    }
}

