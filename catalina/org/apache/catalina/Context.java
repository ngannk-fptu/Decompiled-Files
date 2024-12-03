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
 *  org.apache.tomcat.ContextBind
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.JarScanner
 *  org.apache.tomcat.util.descriptor.web.ApplicationParameter
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.descriptor.web.FilterMap
 *  org.apache.tomcat.util.descriptor.web.LoginConfig
 *  org.apache.tomcat.util.descriptor.web.SecurityConstraint
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.http.CookieProcessor
 */
package org.apache.catalina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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
import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.ThreadBindingListener;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.ContextBind;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.http.CookieProcessor;

public interface Context
extends Container,
ContextBind {
    public static final String ADD_WELCOME_FILE_EVENT = "addWelcomeFile";
    public static final String REMOVE_WELCOME_FILE_EVENT = "removeWelcomeFile";
    public static final String CLEAR_WELCOME_FILES_EVENT = "clearWelcomeFiles";
    public static final String CHANGE_SESSION_ID_EVENT = "changeSessionId";
    public static final String WEBAPP_PROTOCOL = "webapp:";

    public boolean getAllowCasualMultipartParsing();

    public void setAllowCasualMultipartParsing(boolean var1);

    public Object[] getApplicationEventListeners();

    public void setApplicationEventListeners(Object[] var1);

    public Object[] getApplicationLifecycleListeners();

    public void setApplicationLifecycleListeners(Object[] var1);

    public String getCharset(Locale var1);

    public URL getConfigFile();

    public void setConfigFile(URL var1);

    public boolean getConfigured();

    public void setConfigured(boolean var1);

    public boolean getCookies();

    public void setCookies(boolean var1);

    public String getSessionCookieName();

    public void setSessionCookieName(String var1);

    public boolean getUseHttpOnly();

    public void setUseHttpOnly(boolean var1);

    public String getSessionCookieDomain();

    public void setSessionCookieDomain(String var1);

    public String getSessionCookiePath();

    public void setSessionCookiePath(String var1);

    public boolean getSessionCookiePathUsesTrailingSlash();

    public void setSessionCookiePathUsesTrailingSlash(boolean var1);

    public boolean getCrossContext();

    public String getAltDDName();

    public void setAltDDName(String var1);

    public void setCrossContext(boolean var1);

    public boolean getDenyUncoveredHttpMethods();

    public void setDenyUncoveredHttpMethods(boolean var1);

    public String getDisplayName();

    public void setDisplayName(String var1);

    public boolean getDistributable();

    public void setDistributable(boolean var1);

    public String getDocBase();

    public void setDocBase(String var1);

    public String getEncodedPath();

    public boolean getIgnoreAnnotations();

    public void setIgnoreAnnotations(boolean var1);

    public LoginConfig getLoginConfig();

    public void setLoginConfig(LoginConfig var1);

    public NamingResourcesImpl getNamingResources();

    public void setNamingResources(NamingResourcesImpl var1);

    public String getPath();

    public void setPath(String var1);

    public String getPublicId();

    public void setPublicId(String var1);

    public boolean getReloadable();

    public void setReloadable(boolean var1);

    public boolean getOverride();

    public void setOverride(boolean var1);

    public boolean getPrivileged();

    public void setPrivileged(boolean var1);

    public ServletContext getServletContext();

    public int getSessionTimeout();

    public void setSessionTimeout(int var1);

    public boolean getSwallowAbortedUploads();

    public void setSwallowAbortedUploads(boolean var1);

    public boolean getSwallowOutput();

    public void setSwallowOutput(boolean var1);

    public String getWrapperClass();

    public void setWrapperClass(String var1);

    public boolean getXmlNamespaceAware();

    public void setXmlNamespaceAware(boolean var1);

    public boolean getXmlValidation();

    public void setXmlValidation(boolean var1);

    public boolean getXmlBlockExternal();

    public void setXmlBlockExternal(boolean var1);

    public boolean getTldValidation();

    public void setTldValidation(boolean var1);

    public JarScanner getJarScanner();

    public void setJarScanner(JarScanner var1);

    public Authenticator getAuthenticator();

    public void setLogEffectiveWebXml(boolean var1);

    public boolean getLogEffectiveWebXml();

    public InstanceManager getInstanceManager();

    public void setInstanceManager(InstanceManager var1);

    public void setContainerSciFilter(String var1);

    public String getContainerSciFilter();

    @Deprecated
    default public boolean isParallelAnnotationScanning() {
        return this.getParallelAnnotationScanning();
    }

    public boolean getParallelAnnotationScanning();

    public void setParallelAnnotationScanning(boolean var1);

    public void addApplicationListener(String var1);

    public void addApplicationParameter(ApplicationParameter var1);

    public void addConstraint(SecurityConstraint var1);

    public void addErrorPage(ErrorPage var1);

    public void addFilterDef(FilterDef var1);

    public void addFilterMap(FilterMap var1);

    public void addFilterMapBefore(FilterMap var1);

    public void addLocaleEncodingMappingParameter(String var1, String var2);

    public void addMimeMapping(String var1, String var2);

    public void addParameter(String var1, String var2);

    public void addRoleMapping(String var1, String var2);

    public void addSecurityRole(String var1);

    default public void addServletMappingDecoded(String pattern, String name) {
        this.addServletMappingDecoded(pattern, name, false);
    }

    public void addServletMappingDecoded(String var1, String var2, boolean var3);

    public void addWatchedResource(String var1);

    public void addWelcomeFile(String var1);

    public void addWrapperLifecycle(String var1);

    public void addWrapperListener(String var1);

    public InstanceManager createInstanceManager();

    public Wrapper createWrapper();

    public String[] findApplicationListeners();

    public ApplicationParameter[] findApplicationParameters();

    public SecurityConstraint[] findConstraints();

    public ErrorPage findErrorPage(int var1);

    @Deprecated
    public ErrorPage findErrorPage(String var1);

    public ErrorPage findErrorPage(Throwable var1);

    public ErrorPage[] findErrorPages();

    public FilterDef findFilterDef(String var1);

    public FilterDef[] findFilterDefs();

    public FilterMap[] findFilterMaps();

    public String findMimeMapping(String var1);

    public String[] findMimeMappings();

    public String findParameter(String var1);

    public String[] findParameters();

    public String findRoleMapping(String var1);

    public boolean findSecurityRole(String var1);

    public String[] findSecurityRoles();

    public String findServletMapping(String var1);

    public String[] findServletMappings();

    @Deprecated
    public String findStatusPage(int var1);

    @Deprecated
    public int[] findStatusPages();

    public ThreadBindingListener getThreadBindingListener();

    public void setThreadBindingListener(ThreadBindingListener var1);

    public String[] findWatchedResources();

    public boolean findWelcomeFile(String var1);

    public String[] findWelcomeFiles();

    public String[] findWrapperLifecycles();

    public String[] findWrapperListeners();

    public boolean fireRequestInitEvent(ServletRequest var1);

    public boolean fireRequestDestroyEvent(ServletRequest var1);

    public void reload();

    public void removeApplicationListener(String var1);

    public void removeApplicationParameter(String var1);

    public void removeConstraint(SecurityConstraint var1);

    public void removeErrorPage(ErrorPage var1);

    public void removeFilterDef(FilterDef var1);

    public void removeFilterMap(FilterMap var1);

    public void removeMimeMapping(String var1);

    public void removeParameter(String var1);

    public void removeRoleMapping(String var1);

    public void removeSecurityRole(String var1);

    public void removeServletMapping(String var1);

    public void removeWatchedResource(String var1);

    public void removeWelcomeFile(String var1);

    public void removeWrapperLifecycle(String var1);

    public void removeWrapperListener(String var1);

    public String getRealPath(String var1);

    public int getEffectiveMajorVersion();

    public void setEffectiveMajorVersion(int var1);

    public int getEffectiveMinorVersion();

    public void setEffectiveMinorVersion(int var1);

    public JspConfigDescriptor getJspConfigDescriptor();

    public void setJspConfigDescriptor(JspConfigDescriptor var1);

    public void addServletContainerInitializer(ServletContainerInitializer var1, Set<Class<?>> var2);

    public boolean getPaused();

    public boolean isServlet22();

    public Set<String> addServletSecurity(ServletRegistration.Dynamic var1, ServletSecurityElement var2);

    public void setResourceOnlyServlets(String var1);

    public String getResourceOnlyServlets();

    public boolean isResourceOnlyServlet(String var1);

    public String getBaseName();

    public void setWebappVersion(String var1);

    public String getWebappVersion();

    public void setFireRequestListenersOnForwards(boolean var1);

    public boolean getFireRequestListenersOnForwards();

    public void setPreemptiveAuthentication(boolean var1);

    public boolean getPreemptiveAuthentication();

    public void setSendRedirectBody(boolean var1);

    public boolean getSendRedirectBody();

    public Loader getLoader();

    public void setLoader(Loader var1);

    public WebResourceRoot getResources();

    public void setResources(WebResourceRoot var1);

    public Manager getManager();

    public void setManager(Manager var1);

    public void setAddWebinfClassesResources(boolean var1);

    public boolean getAddWebinfClassesResources();

    public void addPostConstructMethod(String var1, String var2);

    public void addPreDestroyMethod(String var1, String var2);

    public void removePostConstructMethod(String var1);

    public void removePreDestroyMethod(String var1);

    public String findPostConstructMethod(String var1);

    public String findPreDestroyMethod(String var1);

    public Map<String, String> findPostConstructMethods();

    public Map<String, String> findPreDestroyMethods();

    public Object getNamingToken();

    public void setCookieProcessor(CookieProcessor var1);

    public CookieProcessor getCookieProcessor();

    public void setValidateClientProvidedNewSessionId(boolean var1);

    public boolean getValidateClientProvidedNewSessionId();

    public void setMapperContextRootRedirectEnabled(boolean var1);

    public boolean getMapperContextRootRedirectEnabled();

    public void setMapperDirectoryRedirectEnabled(boolean var1);

    public boolean getMapperDirectoryRedirectEnabled();

    public void setUseRelativeRedirects(boolean var1);

    public boolean getUseRelativeRedirects();

    public void setDispatchersUseEncodedPaths(boolean var1);

    public boolean getDispatchersUseEncodedPaths();

    public void setRequestCharacterEncoding(String var1);

    public String getRequestCharacterEncoding();

    public void setResponseCharacterEncoding(String var1);

    public String getResponseCharacterEncoding();

    public void setAllowMultipleLeadingForwardSlashInPath(boolean var1);

    public boolean getAllowMultipleLeadingForwardSlashInPath();

    public void incrementInProgressAsyncCount();

    public void decrementInProgressAsyncCount();

    public void setCreateUploadTargets(boolean var1);

    public boolean getCreateUploadTargets();

    default public ConfigurationSource.Resource findConfigFileResource(String name) throws IOException {
        if (name.startsWith(WEBAPP_PROTOCOL)) {
            String path = name.substring(WEBAPP_PROTOCOL.length());
            WebResource resource = this.getResources().getResource(path);
            if (resource.canRead() && resource.isFile()) {
                InputStream stream = resource.getInputStream();
                try {
                    return new ConfigurationSource.Resource(stream, resource.getURL().toURI());
                }
                catch (URISyntaxException e) {
                    stream.close();
                }
            }
            throw new FileNotFoundException(name);
        }
        return ConfigFileLoader.getSource().getResource(name);
    }

    @Deprecated
    public boolean getUseBloomFilterForArchives();

    @Deprecated
    public void setUseBloomFilterForArchives(boolean var1);
}

