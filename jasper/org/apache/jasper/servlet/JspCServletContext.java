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
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.JarScanFilter
 *  org.apache.tomcat.JarScanType
 *  org.apache.tomcat.JarScannerCallback
 *  org.apache.tomcat.util.descriptor.web.FragmentJarScannerCallback
 *  org.apache.tomcat.util.descriptor.web.WebXml
 *  org.apache.tomcat.util.descriptor.web.WebXmlParser
 *  org.apache.tomcat.util.scan.JarFactory
 *  org.apache.tomcat.util.scan.StandardJarScanFilter
 *  org.apache.tomcat.util.scan.StandardJarScanner
 */
package org.apache.jasper.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
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
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.descriptor.web.FragmentJarScannerCallback;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.apache.tomcat.util.scan.JarFactory;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class JspCServletContext
implements ServletContext {
    private final Map<String, Object> myAttributes;
    private final Map<String, String> myParameters = new ConcurrentHashMap<String, String>();
    private final PrintWriter myLogWriter;
    private final URL myResourceBaseURL;
    private WebXml webXml;
    private List<URL> resourceJARs;
    private JspConfigDescriptor jspConfigDescriptor;
    private final ClassLoader loader;

    public JspCServletContext(PrintWriter aLogWriter, URL aResourceBaseURL, ClassLoader classLoader, boolean validate, boolean blockExternal) throws JasperException {
        this.myAttributes = new HashMap<String, Object>();
        this.myParameters.put("org.apache.jasper.XML_BLOCK_EXTERNAL", String.valueOf(blockExternal));
        this.myLogWriter = aLogWriter;
        this.myResourceBaseURL = aResourceBaseURL;
        this.loader = classLoader;
        this.webXml = this.buildMergedWebXml(validate, blockExternal);
        this.jspConfigDescriptor = this.webXml.getJspConfigDescriptor();
    }

    private WebXml buildMergedWebXml(boolean validate, boolean blockExternal) throws JasperException {
        WebXml webXml = new WebXml();
        WebXmlParser webXmlParser = new WebXmlParser(validate, validate, blockExternal);
        webXmlParser.setClassLoader(this.getClass().getClassLoader());
        try {
            URL url = this.getResource("/WEB-INF/web.xml");
            if (!webXmlParser.parseWebXml(url, webXml, false)) {
                throw new JasperException(Localizer.getMessage("jspc.error.invalidWebXml"));
            }
        }
        catch (IOException e) {
            throw new JasperException(e);
        }
        if (webXml.isMetadataComplete()) {
            return webXml;
        }
        Set absoluteOrdering = webXml.getAbsoluteOrdering();
        if (absoluteOrdering != null && absoluteOrdering.isEmpty()) {
            return webXml;
        }
        Map<String, WebXml> fragments = this.scanForFragments(webXmlParser);
        Set orderedFragments = WebXml.orderWebFragments((WebXml)webXml, fragments, (ServletContext)this);
        this.resourceJARs = this.scanForResourceJARs(orderedFragments, fragments.values());
        webXml.merge(orderedFragments);
        return webXml;
    }

    private List<URL> scanForResourceJARs(Set<WebXml> orderedFragments, Collection<WebXml> fragments) throws JasperException {
        ArrayList<URL> resourceJars = new ArrayList<URL>();
        LinkedHashSet<WebXml> resourceFragments = new LinkedHashSet<WebXml>(orderedFragments);
        for (WebXml fragment : fragments) {
            if (resourceFragments.contains(fragment)) continue;
            resourceFragments.add(fragment);
        }
        for (WebXml resourceFragment : resourceFragments) {
            try {
                Jar jar = JarFactory.newInstance((URL)resourceFragment.getURL());
                try {
                    if (!jar.exists("META-INF/resources/")) continue;
                    resourceJars.add(resourceFragment.getURL());
                }
                finally {
                    if (jar == null) continue;
                    jar.close();
                }
            }
            catch (IOException ioe) {
                throw new JasperException(ioe);
            }
        }
        return resourceJars;
    }

    private Map<String, WebXml> scanForFragments(WebXmlParser webXmlParser) throws JasperException {
        StandardJarScanner scanner = new StandardJarScanner();
        scanner.setScanClassPath(false);
        scanner.setJarScanFilter((JarScanFilter)new StandardJarScanFilter());
        FragmentJarScannerCallback callback = new FragmentJarScannerCallback(webXmlParser, false, true);
        scanner.scan(JarScanType.PLUGGABILITY, (ServletContext)this, (JarScannerCallback)callback);
        if (!callback.isOk()) {
            throw new JasperException(Localizer.getMessage("jspc.error.invalidFragment"));
        }
        return callback.getFragments();
    }

    public Object getAttribute(String name) {
        return this.myAttributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.myAttributes.keySet());
    }

    public ServletContext getContext(String uripath) {
        return null;
    }

    public String getContextPath() {
        return null;
    }

    public String getInitParameter(String name) {
        return this.myParameters.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.myParameters.keySet());
    }

    public int getMajorVersion() {
        return 4;
    }

    public String getMimeType(String file) {
        return null;
    }

    public int getMinorVersion() {
        return 0;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }

    public String getRealPath(String path) {
        if (!this.myResourceBaseURL.getProtocol().equals("file")) {
            return null;
        }
        if (!path.startsWith("/")) {
            return null;
        }
        try {
            File f = new File(this.getResource(path).toURI());
            return f.getAbsolutePath();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            return null;
        }
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public URL getResource(String path) throws MalformedURLException {
        if (!path.startsWith("/")) {
            throw new MalformedURLException(Localizer.getMessage("jsp.error.URLMustStartWithSlash", path));
        }
        path = path.substring(1);
        URL url = null;
        try {
            URI uri = new URI(this.myResourceBaseURL.toExternalForm() + path);
            url = uri.toURL();
            InputStream is = url.openStream();
            if (is != null) {
                is.close();
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            url = null;
        }
        if (url != null) return url;
        if (this.resourceJARs == null) return url;
        String jarPath = "META-INF/resources/" + path;
        Iterator<URL> iterator = this.resourceJARs.iterator();
        while (iterator.hasNext()) {
            URL jarUrl = iterator.next();
            try {
                Jar jar = JarFactory.newInstance((URL)jarUrl);
                try {
                    if (!jar.exists(jarPath)) continue;
                    URL uRL = new URI(jar.getURL(jarPath)).toURL();
                    return uRL;
                }
                finally {
                    if (jar == null) continue;
                    jar.close();
                }
            }
            catch (IOException | URISyntaxException exception) {}
        }
        return url;
    }

    public InputStream getResourceAsStream(String path) {
        try {
            return this.getResource(path).openStream();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            return null;
        }
    }

    public Set<String> getResourcePaths(String path) {
        String[] theFiles;
        File theBaseDir;
        String basePath;
        HashSet<String> thePaths = new HashSet<String>();
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        if ((basePath = this.getRealPath(path)) != null && (theBaseDir = new File(basePath)).isDirectory() && (theFiles = theBaseDir.list()) != null) {
            for (String theFile : theFiles) {
                File testFile = new File(basePath + File.separator + theFile);
                if (testFile.isFile()) {
                    thePaths.add(path + theFile);
                    continue;
                }
                if (!testFile.isDirectory()) continue;
                thePaths.add(path + theFile + "/");
            }
        }
        if (this.resourceJARs != null) {
            String jarPath = "META-INF/resources" + path;
            for (URL jarUrl : this.resourceJARs) {
                try (Jar jar = JarFactory.newInstance((URL)jarUrl);){
                    jar.nextEntry();
                    String entryName = jar.getEntryName();
                    while (entryName != null) {
                        if (entryName.startsWith(jarPath) && entryName.length() > jarPath.length()) {
                            int sep = entryName.indexOf(47, jarPath.length());
                            if (sep < 0) {
                                thePaths.add(entryName.substring(18));
                            } else {
                                thePaths.add(entryName.substring(18, sep + 1));
                            }
                        }
                        jar.nextEntry();
                        entryName = jar.getEntryName();
                    }
                }
                catch (IOException e) {
                    this.log(e.getMessage(), e);
                }
            }
        }
        return thePaths;
    }

    public String getServerInfo() {
        return "JspC/ApacheTomcat9";
    }

    @Deprecated
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    public String getServletContextName() {
        return this.getServerInfo();
    }

    @Deprecated
    public Enumeration<String> getServletNames() {
        return new Vector().elements();
    }

    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return new Vector().elements();
    }

    public void log(String message) {
        this.myLogWriter.println(message);
    }

    @Deprecated
    public void log(Exception exception, String message) {
        this.log(message, exception);
    }

    public void log(String message, Throwable exception) {
        this.myLogWriter.println(message);
        exception.printStackTrace(this.myLogWriter);
    }

    public void removeAttribute(String name) {
        this.myAttributes.remove(name);
    }

    public void setAttribute(String name, Object value) {
        this.myAttributes.put(name, value);
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return null;
    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return EnumSet.noneOf(SessionTrackingMode.class);
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return EnumSet.noneOf(SessionTrackingMode.class);
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        return null;
    }

    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        return null;
    }

    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        return null;
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    public boolean setInitParameter(String name, String value) {
        return this.myParameters.putIfAbsent(name, value) == null;
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
    }

    public void addListener(String className) {
    }

    public <T extends EventListener> void addListener(T t) {
    }

    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        return null;
    }

    public void declareRoles(String ... roleNames) {
    }

    public ClassLoader getClassLoader() {
        return this.loader;
    }

    public int getEffectiveMajorVersion() {
        return this.webXml.getMajorVersion();
    }

    public int getEffectiveMinorVersion() {
        return this.webXml.getMinorVersion();
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.jspConfigDescriptor;
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    public String getVirtualServerName() {
        return null;
    }

    public int getSessionTimeout() {
        return 0;
    }

    public void setSessionTimeout(int sessionTimeout) {
    }

    public String getRequestCharacterEncoding() {
        return null;
    }

    public void setRequestCharacterEncoding(String encoding) {
    }

    public String getResponseCharacterEncoding() {
        return null;
    }

    public void setResponseCharacterEncoding(String encoding) {
    }
}

