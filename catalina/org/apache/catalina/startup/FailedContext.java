/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRegistration$Dynamic
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletSecurityElement
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.JarScanner
 *  org.apache.tomcat.util.descriptor.web.ApplicationParameter
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.descriptor.web.FilterMap
 *  org.apache.tomcat.util.descriptor.web.LoginConfig
 *  org.apache.tomcat.util.descriptor.web.SecurityConstraint
 *  org.apache.tomcat.util.http.CookieProcessor
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletSecurityElement;
import javax.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.ThreadBindingListener;
import org.apache.catalina.Valve;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.res.StringManager;

public class FailedContext
extends LifecycleMBeanBase
implements Context {
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");
    private URL configFile;
    private String docBase;
    private String name = null;
    private Container parent;
    private String path = null;
    private String webappVersion = null;

    @Override
    public URL getConfigFile() {
        return this.configFile;
    }

    @Override
    public void setConfigFile(URL configFile) {
        this.configFile = configFile;
    }

    @Override
    public String getDocBase() {
        return this.docBase;
    }

    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Container getParent() {
        return this.parent;
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getWebappVersion() {
        return this.webappVersion;
    }

    @Override
    public void setWebappVersion(String webappVersion) {
        this.webappVersion = webappVersion;
    }

    @Override
    protected String getDomainInternal() {
        Container p = this.getParent();
        if (p == null) {
            return null;
        }
        return p.getDomain();
    }

    @Override
    public String getMBeanKeyProperties() {
        Container c = this;
        StringBuilder keyProperties = new StringBuilder();
        int containerCount = 0;
        while (!(c instanceof Engine)) {
            if (c instanceof Context) {
                keyProperties.append(",context=");
                ContextName cn = new ContextName(c.getName(), false);
                keyProperties.append(cn.getDisplayName());
            } else if (c instanceof Host) {
                keyProperties.append(",host=");
                keyProperties.append(c.getName());
            } else {
                if (c == null) {
                    keyProperties.append(",container");
                    keyProperties.append(containerCount++);
                    keyProperties.append("=null");
                    break;
                }
                keyProperties.append(",container");
                keyProperties.append(containerCount++);
                keyProperties.append('=');
                keyProperties.append(c.getName());
            }
            c = c.getParent();
        }
        return keyProperties.toString();
    }

    @Override
    protected String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("j2eeType=WebModule,name=//");
        String hostname = this.getParent().getName();
        if (hostname == null) {
            keyProperties.append("DEFAULT");
        } else {
            keyProperties.append(hostname);
        }
        String contextName = this.getName();
        if (!contextName.startsWith("/")) {
            keyProperties.append('/');
        }
        keyProperties.append(contextName);
        keyProperties.append(",J2EEApplication=none,J2EEServer=none");
        return keyProperties.toString();
    }

    @Override
    protected void startInternal() throws LifecycleException {
        throw new LifecycleException(sm.getString("failedContext.start", new Object[]{this.getName()}));
    }

    @Override
    protected void stopInternal() throws LifecycleException {
    }

    @Override
    public void addWatchedResource(String name) {
    }

    @Override
    public String[] findWatchedResources() {
        return new String[0];
    }

    @Override
    public void removeWatchedResource(String name) {
    }

    @Override
    public void addChild(Container child) {
    }

    @Override
    public Container findChild(String name) {
        return null;
    }

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }

    @Override
    public void removeChild(Container child) {
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public Loader getLoader() {
        return null;
    }

    @Override
    public void setLoader(Loader loader) {
    }

    @Override
    public Log getLogger() {
        return null;
    }

    @Override
    public String getLogName() {
        return null;
    }

    @Override
    public Manager getManager() {
        return null;
    }

    @Override
    public void setManager(Manager manager) {
    }

    @Override
    public Pipeline getPipeline() {
        return null;
    }

    @Override
    public Cluster getCluster() {
        return null;
    }

    @Override
    public void setCluster(Cluster cluster) {
    }

    @Override
    public int getBackgroundProcessorDelay() {
        return -1;
    }

    @Override
    public void setBackgroundProcessorDelay(int delay) {
    }

    @Override
    public ClassLoader getParentClassLoader() {
        return null;
    }

    @Override
    public void setParentClassLoader(ClassLoader parent) {
    }

    @Override
    public Realm getRealm() {
        return null;
    }

    @Override
    public void setRealm(Realm realm) {
    }

    @Override
    public WebResourceRoot getResources() {
        return null;
    }

    @Override
    public void setResources(WebResourceRoot resources) {
    }

    @Override
    public void backgroundProcess() {
    }

    @Override
    public void addContainerListener(ContainerListener listener) {
    }

    @Override
    public ContainerListener[] findContainerListeners() {
        return null;
    }

    @Override
    public void removeContainerListener(ContainerListener listener) {
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void fireContainerEvent(String type, Object data) {
    }

    @Override
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
    }

    @Override
    public AccessLog getAccessLog() {
        return null;
    }

    @Override
    public int getStartStopThreads() {
        return 0;
    }

    @Override
    public void setStartStopThreads(int startStopThreads) {
    }

    @Override
    public boolean getAllowCasualMultipartParsing() {
        return false;
    }

    @Override
    public void setAllowCasualMultipartParsing(boolean allowCasualMultipartParsing) {
    }

    @Override
    public Object[] getApplicationEventListeners() {
        return null;
    }

    @Override
    public void setApplicationEventListeners(Object[] listeners) {
    }

    @Override
    public Object[] getApplicationLifecycleListeners() {
        return null;
    }

    @Override
    public void setApplicationLifecycleListeners(Object[] listeners) {
    }

    @Override
    public String getCharset(Locale locale) {
        return null;
    }

    @Override
    public boolean getConfigured() {
        return false;
    }

    @Override
    public void setConfigured(boolean configured) {
    }

    @Override
    public boolean getCookies() {
        return false;
    }

    @Override
    public void setCookies(boolean cookies) {
    }

    @Override
    public String getSessionCookieName() {
        return null;
    }

    @Override
    public void setSessionCookieName(String sessionCookieName) {
    }

    @Override
    public boolean getUseHttpOnly() {
        return false;
    }

    @Override
    public void setUseHttpOnly(boolean useHttpOnly) {
    }

    @Override
    public String getSessionCookieDomain() {
        return null;
    }

    @Override
    public void setSessionCookieDomain(String sessionCookieDomain) {
    }

    @Override
    public String getSessionCookiePath() {
        return null;
    }

    @Override
    public void setSessionCookiePath(String sessionCookiePath) {
    }

    @Override
    public boolean getSessionCookiePathUsesTrailingSlash() {
        return false;
    }

    @Override
    public void setSessionCookiePathUsesTrailingSlash(boolean sessionCookiePathUsesTrailingSlash) {
    }

    @Override
    public boolean getCrossContext() {
        return false;
    }

    @Override
    public void setCrossContext(boolean crossContext) {
    }

    @Override
    public String getAltDDName() {
        return null;
    }

    @Override
    public void setAltDDName(String altDDName) {
    }

    @Override
    public boolean getDenyUncoveredHttpMethods() {
        return false;
    }

    @Override
    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String displayName) {
    }

    @Override
    public boolean getDistributable() {
        return false;
    }

    @Override
    public void setDistributable(boolean distributable) {
    }

    @Override
    public String getEncodedPath() {
        return null;
    }

    @Override
    public boolean getIgnoreAnnotations() {
        return false;
    }

    @Override
    public void setIgnoreAnnotations(boolean ignoreAnnotations) {
    }

    @Override
    public LoginConfig getLoginConfig() {
        return null;
    }

    @Override
    public void setLoginConfig(LoginConfig config) {
    }

    @Override
    public NamingResourcesImpl getNamingResources() {
        return null;
    }

    @Override
    public void setNamingResources(NamingResourcesImpl namingResources) {
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public void setPublicId(String publicId) {
    }

    @Override
    public boolean getReloadable() {
        return false;
    }

    @Override
    public void setReloadable(boolean reloadable) {
    }

    @Override
    public boolean getOverride() {
        return false;
    }

    @Override
    public void setOverride(boolean override) {
    }

    @Override
    public boolean getPrivileged() {
        return false;
    }

    @Override
    public void setPrivileged(boolean privileged) {
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {
    }

    @Override
    public boolean getSwallowAbortedUploads() {
        return false;
    }

    @Override
    public void setSwallowAbortedUploads(boolean swallowAbortedUploads) {
    }

    @Override
    public boolean getSwallowOutput() {
        return false;
    }

    @Override
    public void setSwallowOutput(boolean swallowOutput) {
    }

    @Override
    public String getWrapperClass() {
        return null;
    }

    @Override
    public void setWrapperClass(String wrapperClass) {
    }

    @Override
    public boolean getXmlNamespaceAware() {
        return false;
    }

    @Override
    public void setXmlNamespaceAware(boolean xmlNamespaceAware) {
    }

    @Override
    public boolean getXmlValidation() {
        return false;
    }

    @Override
    public void setXmlValidation(boolean xmlValidation) {
    }

    @Override
    public boolean getXmlBlockExternal() {
        return true;
    }

    @Override
    public void setXmlBlockExternal(boolean xmlBlockExternal) {
    }

    @Override
    public boolean getTldValidation() {
        return false;
    }

    @Override
    public void setTldValidation(boolean tldValidation) {
    }

    @Override
    public JarScanner getJarScanner() {
        return null;
    }

    @Override
    public void setJarScanner(JarScanner jarScanner) {
    }

    @Override
    public Authenticator getAuthenticator() {
        return null;
    }

    @Override
    public void setLogEffectiveWebXml(boolean logEffectiveWebXml) {
    }

    @Override
    public boolean getLogEffectiveWebXml() {
        return false;
    }

    @Override
    public void addApplicationListener(String listener) {
    }

    @Override
    public String[] findApplicationListeners() {
        return null;
    }

    @Override
    public void removeApplicationListener(String listener) {
    }

    @Override
    public void addApplicationParameter(ApplicationParameter parameter) {
    }

    @Override
    public ApplicationParameter[] findApplicationParameters() {
        return null;
    }

    @Override
    public void removeApplicationParameter(String name) {
    }

    @Override
    public void addConstraint(SecurityConstraint constraint) {
    }

    @Override
    public SecurityConstraint[] findConstraints() {
        return null;
    }

    @Override
    public void removeConstraint(SecurityConstraint constraint) {
    }

    @Override
    public void addErrorPage(ErrorPage errorPage) {
    }

    @Override
    public ErrorPage findErrorPage(int errorCode) {
        return null;
    }

    @Override
    public ErrorPage findErrorPage(String exceptionType) {
        return null;
    }

    @Override
    public ErrorPage findErrorPage(Throwable throwable) {
        return null;
    }

    @Override
    public ErrorPage[] findErrorPages() {
        return null;
    }

    @Override
    public void removeErrorPage(ErrorPage errorPage) {
    }

    @Override
    public void addFilterDef(FilterDef filterDef) {
    }

    @Override
    public FilterDef findFilterDef(String filterName) {
        return null;
    }

    @Override
    public FilterDef[] findFilterDefs() {
        return null;
    }

    @Override
    public void removeFilterDef(FilterDef filterDef) {
    }

    @Override
    public void addFilterMap(FilterMap filterMap) {
    }

    @Override
    public void addFilterMapBefore(FilterMap filterMap) {
    }

    @Override
    public FilterMap[] findFilterMaps() {
        return null;
    }

    @Override
    public void removeFilterMap(FilterMap filterMap) {
    }

    @Override
    public void addLocaleEncodingMappingParameter(String locale, String encoding) {
    }

    @Override
    public void addMimeMapping(String extension, String mimeType) {
    }

    @Override
    public String findMimeMapping(String extension) {
        return null;
    }

    @Override
    public String[] findMimeMappings() {
        return null;
    }

    @Override
    public void removeMimeMapping(String extension) {
    }

    @Override
    public void addParameter(String name, String value) {
    }

    @Override
    public String findParameter(String name) {
        return null;
    }

    @Override
    public String[] findParameters() {
        return null;
    }

    @Override
    public void removeParameter(String name) {
    }

    @Override
    public void addRoleMapping(String role, String link) {
    }

    @Override
    public String findRoleMapping(String role) {
        return null;
    }

    @Override
    public void removeRoleMapping(String role) {
    }

    @Override
    public void addSecurityRole(String role) {
    }

    @Override
    public boolean findSecurityRole(String role) {
        return false;
    }

    @Override
    public String[] findSecurityRoles() {
        return null;
    }

    @Override
    public void removeSecurityRole(String role) {
    }

    @Override
    public void addServletMappingDecoded(String pattern, String name, boolean jspWildcard) {
    }

    @Override
    public String findServletMapping(String pattern) {
        return null;
    }

    @Override
    public String[] findServletMappings() {
        return null;
    }

    @Override
    public void removeServletMapping(String pattern) {
    }

    @Override
    public void addWelcomeFile(String name) {
    }

    @Override
    public boolean findWelcomeFile(String name) {
        return false;
    }

    @Override
    public String[] findWelcomeFiles() {
        return null;
    }

    @Override
    public void removeWelcomeFile(String name) {
    }

    @Override
    public void addWrapperLifecycle(String listener) {
    }

    @Override
    public String[] findWrapperLifecycles() {
        return null;
    }

    @Override
    public void removeWrapperLifecycle(String listener) {
    }

    @Override
    public void addWrapperListener(String listener) {
    }

    @Override
    public String[] findWrapperListeners() {
        return null;
    }

    @Override
    public void removeWrapperListener(String listener) {
    }

    @Override
    public InstanceManager createInstanceManager() {
        return null;
    }

    @Override
    public Wrapper createWrapper() {
        return null;
    }

    @Override
    public String findStatusPage(int status) {
        return null;
    }

    @Override
    public int[] findStatusPages() {
        return null;
    }

    @Override
    public boolean fireRequestInitEvent(ServletRequest request) {
        return false;
    }

    @Override
    public boolean fireRequestDestroyEvent(ServletRequest request) {
        return false;
    }

    @Override
    public void reload() {
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public void setEffectiveMajorVersion(int major) {
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public void setEffectiveMinorVersion(int minor) {
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public void setJspConfigDescriptor(JspConfigDescriptor descriptor) {
    }

    @Override
    public void addServletContainerInitializer(ServletContainerInitializer sci, Set<Class<?>> classes) {
    }

    @Override
    public boolean getPaused() {
        return false;
    }

    @Override
    public boolean isServlet22() {
        return false;
    }

    @Override
    public Set<String> addServletSecurity(ServletRegistration.Dynamic registration, ServletSecurityElement servletSecurityElement) {
        return null;
    }

    @Override
    public void setResourceOnlyServlets(String resourceOnlyServlets) {
    }

    @Override
    public String getResourceOnlyServlets() {
        return null;
    }

    @Override
    public boolean isResourceOnlyServlet(String servletName) {
        return false;
    }

    @Override
    public String getBaseName() {
        return null;
    }

    @Override
    public void setFireRequestListenersOnForwards(boolean enable) {
    }

    @Override
    public boolean getFireRequestListenersOnForwards() {
        return false;
    }

    @Override
    public void setPreemptiveAuthentication(boolean enable) {
    }

    @Override
    public boolean getPreemptiveAuthentication() {
        return false;
    }

    @Override
    public void setSendRedirectBody(boolean enable) {
    }

    @Override
    public boolean getSendRedirectBody() {
        return false;
    }

    public synchronized void addValve(Valve valve) {
    }

    @Override
    public File getCatalinaBase() {
        return null;
    }

    @Override
    public File getCatalinaHome() {
        return null;
    }

    @Override
    public void setAddWebinfClassesResources(boolean addWebinfClassesResources) {
    }

    @Override
    public boolean getAddWebinfClassesResources() {
        return false;
    }

    @Override
    public void addPostConstructMethod(String clazz, String method) {
    }

    @Override
    public void addPreDestroyMethod(String clazz, String method) {
    }

    @Override
    public void removePostConstructMethod(String clazz) {
    }

    @Override
    public void removePreDestroyMethod(String clazz) {
    }

    @Override
    public String findPostConstructMethod(String clazz) {
        return null;
    }

    @Override
    public String findPreDestroyMethod(String clazz) {
        return null;
    }

    @Override
    public Map<String, String> findPostConstructMethods() {
        return null;
    }

    @Override
    public Map<String, String> findPreDestroyMethods() {
        return null;
    }

    @Override
    public InstanceManager getInstanceManager() {
        return null;
    }

    @Override
    public void setInstanceManager(InstanceManager instanceManager) {
    }

    @Override
    public void setContainerSciFilter(String containerSciFilter) {
    }

    @Override
    public String getContainerSciFilter() {
        return null;
    }

    @Override
    public ThreadBindingListener getThreadBindingListener() {
        return null;
    }

    @Override
    public void setThreadBindingListener(ThreadBindingListener threadBindingListener) {
    }

    public ClassLoader bind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
        return null;
    }

    public void unbind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
    }

    @Override
    public Object getNamingToken() {
        return null;
    }

    @Override
    public void setCookieProcessor(CookieProcessor cookieProcessor) {
    }

    @Override
    public CookieProcessor getCookieProcessor() {
        return null;
    }

    @Override
    public void setValidateClientProvidedNewSessionId(boolean validateClientProvidedNewSessionId) {
    }

    @Override
    public boolean getValidateClientProvidedNewSessionId() {
        return false;
    }

    @Override
    public void setMapperContextRootRedirectEnabled(boolean mapperContextRootRedirectEnabled) {
    }

    @Override
    public boolean getMapperContextRootRedirectEnabled() {
        return false;
    }

    @Override
    public void setMapperDirectoryRedirectEnabled(boolean mapperDirectoryRedirectEnabled) {
    }

    @Override
    public boolean getMapperDirectoryRedirectEnabled() {
        return false;
    }

    @Override
    public void setUseRelativeRedirects(boolean useRelativeRedirects) {
    }

    @Override
    public boolean getUseRelativeRedirects() {
        return true;
    }

    @Override
    public void setDispatchersUseEncodedPaths(boolean dispatchersUseEncodedPaths) {
    }

    @Override
    public boolean getDispatchersUseEncodedPaths() {
        return true;
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) {
    }

    @Override
    public String getRequestCharacterEncoding() {
        return null;
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {
    }

    @Override
    public String getResponseCharacterEncoding() {
        return null;
    }

    @Override
    public void setAllowMultipleLeadingForwardSlashInPath(boolean allowMultipleLeadingForwardSlashInPath) {
    }

    @Override
    public boolean getAllowMultipleLeadingForwardSlashInPath() {
        return false;
    }

    @Override
    public void incrementInProgressAsyncCount() {
    }

    @Override
    public void decrementInProgressAsyncCount() {
    }

    @Override
    public void setCreateUploadTargets(boolean createUploadTargets) {
    }

    @Override
    public boolean getCreateUploadTargets() {
        return false;
    }

    @Override
    public boolean getParallelAnnotationScanning() {
        return false;
    }

    @Override
    public void setParallelAnnotationScanning(boolean parallelAnnotationScanning) {
    }

    @Override
    public boolean getUseBloomFilterForArchives() {
        return false;
    }

    @Override
    public void setUseBloomFilterForArchives(boolean useBloomFilterForArchives) {
    }
}

