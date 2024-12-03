/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.tagext.Tag
 */
package freemarker.ext.jsp;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jsp.CustomTagAndELFunctionCombiner;
import freemarker.ext.jsp.EventForwarding;
import freemarker.ext.jsp.JspTagModelBase;
import freemarker.ext.jsp.SimpleTagDirectiveModel;
import freemarker.ext.jsp.TagTransformModel;
import freemarker.ext.jsp.TaglibMethodUtil;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.log.Logger;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class TaglibFactory
implements TemplateHashModel {
    public static final List DEFAULT_CLASSPATH_TLDS = Collections.EMPTY_LIST;
    public static final List DEFAULT_META_INF_TLD_SOURCES = Collections.singletonList(WebInfPerLibJarMetaInfTldSource.INSTANCE);
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private static final int URL_TYPE_FULL = 0;
    private static final int URL_TYPE_ABSOLUTE = 1;
    private static final int URL_TYPE_RELATIVE = 2;
    private static final String META_INF_REL_PATH = "META-INF/";
    private static final String META_INF_ABS_PATH = "/META-INF/";
    private static final String DEFAULT_TLD_RESOURCE_PATH = "/META-INF/taglib.tld";
    private static final String JAR_URL_ENTRY_PATH_START = "!/";
    private static final String PLATFORM_FILE_ENCODING = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
    private final ServletContext servletContext;
    private ObjectWrapper objectWrapper;
    private List metaInfTldSources = DEFAULT_META_INF_TLD_SOURCES;
    private List classpathTlds = DEFAULT_CLASSPATH_TLDS;
    boolean test_emulateNoUrlToFileConversions = false;
    boolean test_emulateNoJarURLConnections = false;
    boolean test_emulateJarEntryUrlOpenStreamFails = false;
    private final Object lock = new Object();
    private final Map taglibs = new HashMap();
    private final Map tldLocations = new HashMap();
    private List failedTldLocations = new ArrayList();
    private int nextTldLocationLookupPhase = 0;

    public TaglibFactory(ServletContext ctx) {
        this.servletContext = ctx;
    }

    @Override
    public TemplateModel get(String taglibUri) throws TemplateModelException {
        Object object = this.lock;
        synchronized (object) {
            String normalizedTaglibUri;
            TldLocation tldLocation;
            block18: {
                Taglib taglib = (Taglib)this.taglibs.get(taglibUri);
                if (taglib != null) {
                    return taglib;
                }
                boolean failedTldListAlreadyIncluded = false;
                try {
                    Taglib taglib2;
                    int urlType;
                    TldLocation explicitlyMappedTldLocation;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Locating TLD for taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + ".");
                    }
                    if ((explicitlyMappedTldLocation = this.getExplicitlyMappedTldLocation(taglibUri)) != null) {
                        tldLocation = explicitlyMappedTldLocation;
                        normalizedTaglibUri = taglibUri;
                        break block18;
                    }
                    try {
                        urlType = TaglibFactory.getUriType(taglibUri);
                    }
                    catch (MalformedURLException e) {
                        throw new TaglibGettingException("Malformed taglib URI: " + StringUtil.jQuote(taglibUri), e);
                    }
                    if (urlType == 2) {
                        normalizedTaglibUri = TaglibFactory.resolveRelativeUri(taglibUri);
                    } else if (urlType == 1) {
                        normalizedTaglibUri = taglibUri;
                    } else {
                        if (urlType == 0) {
                            String failedTLDsList = this.getFailedTLDsList();
                            failedTldListAlreadyIncluded = true;
                            throw new TaglibGettingException("No TLD was found for the " + StringUtil.jQuoteNoXSS(taglibUri) + " JSP taglib URI. (TLD-s are searched according the JSP 2.2 specification. In development- and embedded-servlet-container setups you may also need the \"" + "MetaInfTldSources" + "\" and \"" + "ClasspathTlds" + "\" " + FreemarkerServlet.class.getName() + " init-params or the similar system properites." + (failedTLDsList == null ? "" : " Also note these TLD-s were skipped earlier due to errors; see error in the log: " + failedTLDsList) + ")");
                        }
                        throw new BugException();
                    }
                    if (!normalizedTaglibUri.equals(taglibUri) && (taglib2 = (Taglib)this.taglibs.get(normalizedTaglibUri)) != null) {
                        return taglib2;
                    }
                    tldLocation = TaglibFactory.isJarPath(normalizedTaglibUri) ? new ServletContextJarEntryTldLocation(normalizedTaglibUri, DEFAULT_TLD_RESOURCE_PATH) : new ServletContextTldLocation(normalizedTaglibUri);
                }
                catch (Exception e) {
                    String failedTLDsList = failedTldListAlreadyIncluded ? null : this.getFailedTLDsList();
                    throw new TemplateModelException("Error while looking for TLD file for " + StringUtil.jQuoteNoXSS(taglibUri) + "; see cause exception." + (failedTLDsList == null ? "" : " (Note: These TLD-s were skipped earlier due to errors; see errors in the log: " + failedTLDsList + ")"), e);
                }
            }
            try {
                return this.loadTaglib(tldLocation, normalizedTaglibUri);
            }
            catch (Exception e) {
                throw new TemplateModelException("Error while loading tag library for URI " + StringUtil.jQuoteNoXSS(normalizedTaglibUri) + " from TLD location " + StringUtil.jQuoteNoXSS(tldLocation) + "; see cause exception.", e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getFailedTLDsList() {
        List list = this.failedTldLocations;
        synchronized (list) {
            if (this.failedTldLocations.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.failedTldLocations.size(); ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(StringUtil.jQuote(this.failedTldLocations.get(i)));
            }
            return sb.toString();
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public ObjectWrapper getObjectWrapper() {
        return this.objectWrapper;
    }

    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        this.checkNotStarted();
        this.objectWrapper = objectWrapper;
    }

    public List getMetaInfTldSources() {
        return this.metaInfTldSources;
    }

    public void setMetaInfTldSources(List metaInfTldSources) {
        this.checkNotStarted();
        NullArgumentException.check("metaInfTldSources", metaInfTldSources);
        this.metaInfTldSources = metaInfTldSources;
    }

    public List getClasspathTlds() {
        return this.classpathTlds;
    }

    public void setClasspathTlds(List classpathTlds) {
        this.checkNotStarted();
        NullArgumentException.check("classpathTlds", classpathTlds);
        this.classpathTlds = classpathTlds;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkNotStarted() {
        Object object = this.lock;
        synchronized (object) {
            if (this.nextTldLocationLookupPhase != 0) {
                throw new IllegalStateException(TaglibFactory.class.getName() + " object was already in use.");
            }
        }
    }

    private TldLocation getExplicitlyMappedTldLocation(String uri) throws SAXException, IOException, TaglibGettingException {
        TldLocation tldLocation;
        while ((tldLocation = (TldLocation)this.tldLocations.get(uri)) == null) {
            switch (this.nextTldLocationLookupPhase) {
                case 0: {
                    this.addTldLocationsFromClasspathTlds();
                    break;
                }
                case 1: {
                    this.addTldLocationsFromWebXml();
                    break;
                }
                case 2: {
                    this.addTldLocationsFromWebInfTlds();
                    break;
                }
                case 3: {
                    this.addTldLocationsFromMetaInfTlds();
                    break;
                }
                case 4: {
                    return null;
                }
                default: {
                    throw new BugException();
                }
            }
            ++this.nextTldLocationLookupPhase;
        }
        return tldLocation;
    }

    private void addTldLocationsFromWebXml() throws SAXException, IOException {
        LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/web.xml");
        WebXmlParser webXmlParser = new WebXmlParser();
        InputStream in = this.servletContext.getResourceAsStream("/WEB-INF/web.xml");
        if (in == null) {
            LOG.debug("No web.xml was found in servlet context");
            return;
        }
        try {
            TaglibFactory.parseXml(in, this.servletContext.getResource("/WEB-INF/web.xml").toExternalForm(), webXmlParser);
        }
        finally {
            in.close();
        }
    }

    private void addTldLocationsFromWebInfTlds() throws IOException, SAXException {
        LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/**/*.tld");
        this.addTldLocationsFromServletContextResourceTlds("/WEB-INF");
    }

    private void addTldLocationsFromServletContextResourceTlds(String basePath) throws IOException, SAXException {
        Set unsortedResourcePaths = this.servletContext.getResourcePaths(basePath);
        if (unsortedResourcePaths != null) {
            ArrayList resourcePaths = new ArrayList(unsortedResourcePaths);
            Collections.sort(resourcePaths);
            for (String resourcePath : resourcePaths) {
                if (!resourcePath.endsWith(".tld")) continue;
                this.addTldLocationFromTld(new ServletContextTldLocation(resourcePath));
            }
            for (String resourcePath : resourcePaths) {
                if (!resourcePath.endsWith("/")) continue;
                this.addTldLocationsFromServletContextResourceTlds(resourcePath);
            }
        }
    }

    private void addTldLocationsFromMetaInfTlds() throws IOException, SAXException {
        if (this.metaInfTldSources == null || this.metaInfTldSources.isEmpty()) {
            return;
        }
        Set cpMetaInfDirUrlsWithEF = null;
        int srcIdxStart = 0;
        for (int i = this.metaInfTldSources.size() - 1; i >= 0; --i) {
            if (!(this.metaInfTldSources.get(i) instanceof ClearMetaInfTldSource)) continue;
            srcIdxStart = i + 1;
            break;
        }
        for (int srcIdx = srcIdxStart; srcIdx < this.metaInfTldSources.size(); ++srcIdx) {
            MetaInfTldSource miTldSource = (MetaInfTldSource)this.metaInfTldSources.get(srcIdx);
            if (miTldSource == WebInfPerLibJarMetaInfTldSource.INSTANCE) {
                this.addTldLocationsFromWebInfPerLibJarMetaInfTlds();
                continue;
            }
            if (miTldSource instanceof ClasspathMetaInfTldSource) {
                ClasspathMetaInfTldSource cpMiTldLocation = (ClasspathMetaInfTldSource)miTldSource;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Looking for TLD-s in classpathRoots[" + cpMiTldLocation.getRootContainerPattern() + "]" + META_INF_ABS_PATH + "**/*.tld");
                }
                if (cpMetaInfDirUrlsWithEF == null) {
                    cpMetaInfDirUrlsWithEF = TaglibFactory.collectMetaInfUrlsFromClassLoaders();
                }
                for (URLWithExternalForm urlWithEF : cpMetaInfDirUrlsWithEF) {
                    String rootContainerUrl;
                    URL url = urlWithEF.getUrl();
                    boolean isJarUrl = TaglibFactory.isJarUrl(url);
                    String urlEF = urlWithEF.externalForm;
                    if (isJarUrl) {
                        int sep = urlEF.indexOf(JAR_URL_ENTRY_PATH_START);
                        rootContainerUrl = sep != -1 ? urlEF.substring(0, sep) : urlEF;
                    } else {
                        String string = rootContainerUrl = urlEF.endsWith(META_INF_ABS_PATH) ? urlEF.substring(0, urlEF.length() - META_INF_REL_PATH.length()) : urlEF;
                    }
                    if (!cpMiTldLocation.getRootContainerPattern().matcher(rootContainerUrl).matches()) continue;
                    File urlAsFile = this.urlToFileOrNull(url);
                    if (urlAsFile != null) {
                        this.addTldLocationsFromFileDirectory(urlAsFile);
                        continue;
                    }
                    if (isJarUrl) {
                        this.addTldLocationsFromJarDirectoryEntryURL(url);
                        continue;
                    }
                    if (!LOG.isDebugEnabled()) continue;
                    LOG.debug("Can't list entries under this URL; TLD-s won't be discovered here: " + urlWithEF.getExternalForm());
                }
                continue;
            }
            throw new BugException();
        }
    }

    private void addTldLocationsFromWebInfPerLibJarMetaInfTlds() throws IOException, SAXException {
        Set libEntPaths;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/lib/*.{jar,zip}/META-INF/*.tld");
        }
        if ((libEntPaths = this.servletContext.getResourcePaths("/WEB-INF/lib")) != null) {
            for (String libEntryPath : libEntPaths) {
                if (!TaglibFactory.isJarPath(libEntryPath)) continue;
                this.addTldLocationsFromServletContextJar(libEntryPath);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addTldLocationsFromClasspathTlds() throws SAXException, IOException, TaglibGettingException {
        if (this.classpathTlds == null || this.classpathTlds.size() == 0) {
            return;
        }
        LOG.debug("Looking for TLD locations in TLD-s specified in cfg.classpathTlds");
        for (String tldResourcePath : this.classpathTlds) {
            InputStream in;
            if (tldResourcePath.trim().length() == 0) {
                throw new TaglibGettingException("classpathTlds can't contain empty item");
            }
            if (!tldResourcePath.startsWith("/")) {
                tldResourcePath = "/" + tldResourcePath;
            }
            if (tldResourcePath.endsWith("/")) {
                throw new TaglibGettingException("classpathTlds can't specify a directory: " + tldResourcePath);
            }
            ClasspathTldLocation tldLocation = new ClasspathTldLocation(tldResourcePath);
            try {
                in = tldLocation.getInputStream();
            }
            catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Ignored classpath TLD location " + StringUtil.jQuoteNoXSS(tldResourcePath) + " because of error", e);
                }
                in = null;
            }
            if (in == null) continue;
            try {
                this.addTldLocationFromTld(in, tldLocation);
            }
            finally {
                in.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addTldLocationsFromServletContextJar(String jarResourcePath) throws IOException, MalformedURLException, SAXException {
        String metaInfEntryPath = TaglibFactory.normalizeJarEntryPath(META_INF_ABS_PATH, true);
        JarFile jarFile = this.servletContextResourceToFileOrNull(jarResourcePath);
        if (jarFile != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for /META-INF/*.tld-s in JarFile: servletContext:" + jarResourcePath);
            }
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry curEntry = entries.nextElement();
                String curEntryPath = TaglibFactory.normalizeJarEntryPath(curEntry.getName(), false);
                if (!curEntryPath.startsWith(metaInfEntryPath) || !curEntryPath.endsWith(".tld")) continue;
                this.addTldLocationFromTld(new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
            }
        } else {
            InputStream in;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for /META-INF/*.tld-s in ZipInputStream (slow): servletContext:" + jarResourcePath);
            }
            if ((in = this.servletContext.getResourceAsStream(jarResourcePath)) == null) {
                throw new IOException("ServletContext resource not found: " + jarResourcePath);
            }
            try (ZipInputStream zipIn = new ZipInputStream(in);){
                ZipEntry curEntry;
                while ((curEntry = zipIn.getNextEntry()) != null) {
                    String curEntryPath = TaglibFactory.normalizeJarEntryPath(curEntry.getName(), false);
                    if (!curEntryPath.startsWith(metaInfEntryPath) || !curEntryPath.endsWith(".tld")) continue;
                    this.addTldLocationFromTld(zipIn, new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
                }
            }
            finally {
                in.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addTldLocationsFromJarDirectoryEntryURL(URL jarBaseEntryUrl) throws IOException, MalformedURLException, SAXException {
        Object curEntry;
        String baseEntryPath;
        String rawJarContentUrlEF;
        JarFile jarFile;
        URLConnection urlCon = jarBaseEntryUrl.openConnection();
        if (!this.test_emulateNoJarURLConnections && urlCon instanceof JarURLConnection) {
            JarURLConnection jarCon = (JarURLConnection)urlCon;
            jarFile = jarCon.getJarFile();
            rawJarContentUrlEF = null;
            baseEntryPath = TaglibFactory.normalizeJarEntryPath(jarCon.getEntryName(), true);
            if (baseEntryPath == null) {
                throw TaglibFactory.newFailedToExtractEntryPathException(jarBaseEntryUrl);
            }
        } else {
            String jarBaseEntryUrlEF = jarBaseEntryUrl.toExternalForm();
            int jarEntrySepIdx = jarBaseEntryUrlEF.indexOf(JAR_URL_ENTRY_PATH_START);
            if (jarEntrySepIdx == -1) {
                throw TaglibFactory.newFailedToExtractEntryPathException(jarBaseEntryUrl);
            }
            rawJarContentUrlEF = jarBaseEntryUrlEF.substring(jarBaseEntryUrlEF.indexOf(58) + 1, jarEntrySepIdx);
            baseEntryPath = TaglibFactory.normalizeJarEntryPath(jarBaseEntryUrlEF.substring(jarEntrySepIdx + JAR_URL_ENTRY_PATH_START.length()), true);
            File rawJarContentAsFile = this.urlToFileOrNull(new URL(rawJarContentUrlEF));
            JarFile jarFile2 = jarFile = rawJarContentAsFile != null ? new JarFile(rawJarContentAsFile) : null;
        }
        if (jarFile != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for /META-INF/**/*.tld-s in random access mode: " + jarBaseEntryUrl);
            }
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                curEntry = entries.nextElement();
                String curEntryPath = TaglibFactory.normalizeJarEntryPath(((ZipEntry)curEntry).getName(), false);
                if (!curEntryPath.startsWith(baseEntryPath) || !curEntryPath.endsWith(".tld")) continue;
                String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
                URL tldUrl = TaglibFactory.createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
                this.addTldLocationFromTld(new JarEntryUrlTldLocation(tldUrl, null));
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for /META-INF/**/*.tld-s in stream mode (slow): " + rawJarContentUrlEF);
            }
            try {
                InputStream in = new URL(rawJarContentUrlEF).openStream();
                curEntry = null;
                try (ZipInputStream zipIn2 = new ZipInputStream(in);){
                    ZipEntry curEntry2;
                    while ((curEntry2 = zipIn2.getNextEntry()) != null) {
                        String curEntryPath = TaglibFactory.normalizeJarEntryPath(curEntry2.getName(), false);
                        if (!curEntryPath.startsWith(baseEntryPath) || !curEntryPath.endsWith(".tld")) continue;
                        String curEntryBaseRelativePath = curEntryPath.substring(baseEntryPath.length());
                        URL tldUrl = TaglibFactory.createJarEntryUrl(jarBaseEntryUrl, curEntryBaseRelativePath);
                        this.addTldLocationFromTld(zipIn2, new JarEntryUrlTldLocation(tldUrl, null));
                    }
                }
                catch (Throwable zipIn2) {
                    curEntry = zipIn2;
                    throw zipIn2;
                }
                finally {
                    if (in != null) {
                        if (curEntry != null) {
                            try {
                                in.close();
                            }
                            catch (Throwable zipIn2) {
                                ((Throwable)curEntry).addSuppressed(zipIn2);
                            }
                        } else {
                            in.close();
                        }
                    }
                }
            }
            catch (ZipException e) {
                IOException ioe = new IOException("Error reading ZIP (see cause excepetion) from: " + rawJarContentUrlEF);
                try {
                    ioe.initCause(e);
                }
                catch (Exception e2) {
                    throw e;
                }
                throw ioe;
            }
        }
    }

    private void addTldLocationsFromFileDirectory(File dir) throws IOException, SAXException {
        if (dir.isDirectory()) {
            File[] tldFiles;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for *.tld-s in File directory: " + StringUtil.jQuoteNoXSS(dir));
            }
            if ((tldFiles = dir.listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File urlAsFile, String name) {
                    return TaglibFactory.isTldFileNameIgnoreCase(name);
                }
            })) == null) {
                throw new IOException("Can't list this directory for some reason: " + dir);
            }
            for (int i = 0; i < tldFiles.length; ++i) {
                File file = tldFiles[i];
                this.addTldLocationFromTld(new FileTldLocation(file));
            }
        } else {
            LOG.warn("Skipped scanning for *.tld for non-existent directory: " + StringUtil.jQuoteNoXSS(dir));
        }
    }

    private void addTldLocationFromTld(TldLocation tldLocation) throws IOException, SAXException {
        try (InputStream in = tldLocation.getInputStream();){
            this.addTldLocationFromTld(in, tldLocation);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addTldLocationFromTld(InputStream reusedIn, TldLocation tldLocation) throws SAXException, IOException {
        String taglibUri;
        try {
            taglibUri = this.getTaglibUriFromTld(reusedIn, tldLocation.getXmlSystemId());
        }
        catch (SAXException e) {
            LOG.error("Error while parsing TLD; skipping: " + tldLocation, e);
            List list = this.failedTldLocations;
            synchronized (list) {
                this.failedTldLocations.add(tldLocation.toString());
            }
            taglibUri = null;
        }
        if (taglibUri != null) {
            this.addTldLocation(tldLocation, taglibUri);
        }
    }

    private void addTldLocation(TldLocation tldLocation, String taglibUri) {
        if (this.tldLocations.containsKey(taglibUri)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignored duplicate mapping of taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
            }
        } else {
            this.tldLocations.put(taglibUri, tldLocation);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mapped taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
            }
        }
    }

    private static Set collectMetaInfUrlsFromClassLoaders() throws IOException {
        ClassLoader cccl;
        TreeSet metainfDirUrls = new TreeSet();
        ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
        if (tccl != null) {
            TaglibFactory.collectMetaInfUrlsFromClassLoader(tccl, metainfDirUrls);
        }
        if (!TaglibFactory.isDescendantOfOrSameAs(tccl, cccl = TaglibFactory.class.getClassLoader())) {
            TaglibFactory.collectMetaInfUrlsFromClassLoader(cccl, metainfDirUrls);
        }
        return metainfDirUrls;
    }

    private static void collectMetaInfUrlsFromClassLoader(ClassLoader cl, Set metainfDirUrls) throws IOException {
        Enumeration<URL> urls = cl.getResources(META_INF_REL_PATH);
        if (urls != null) {
            while (urls.hasMoreElements()) {
                metainfDirUrls.add(new URLWithExternalForm(urls.nextElement()));
            }
        }
    }

    private String getTaglibUriFromTld(InputStream tldFileIn, String tldFileXmlSystemId) throws SAXException, IOException {
        TldParserForTaglibUriExtraction tldParser = new TldParserForTaglibUriExtraction();
        TaglibFactory.parseXml(tldFileIn, tldFileXmlSystemId, tldParser);
        return tldParser.getTaglibUri();
    }

    private TemplateHashModel loadTaglib(TldLocation tldLocation, String taglibUri) throws IOException, SAXException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading taglib for URI " + StringUtil.jQuoteNoXSS(taglibUri) + " from TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
        }
        Taglib taglib = new Taglib(this.servletContext, tldLocation, this.objectWrapper);
        this.taglibs.put(taglibUri, taglib);
        this.tldLocations.remove(taglibUri);
        return taglib;
    }

    private static void parseXml(InputStream in, String systemId, DefaultHandler handler) throws SAXException, IOException {
        XMLReader reader;
        InputSource inSrc = new InputSource();
        inSrc.setSystemId(systemId);
        inSrc.setByteStream(TaglibFactory.toCloseIgnoring(in));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        try {
            reader = factory.newSAXParser().getXMLReader();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException("XML parser setup failed", e);
        }
        reader.setEntityResolver(new EmptyContentEntityResolver());
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(inSrc);
    }

    private static String resolveRelativeUri(String uri) throws TaglibGettingException {
        TemplateModel reqHash;
        try {
            reqHash = Environment.getCurrentEnvironment().getVariable("__FreeMarkerServlet.Request__");
        }
        catch (TemplateModelException e) {
            throw new TaglibGettingException("Failed to get FreemarkerServlet request information", e);
        }
        if (reqHash instanceof HttpRequestHashModel) {
            int lastSlash;
            HttpServletRequest req = ((HttpRequestHashModel)reqHash).getRequest();
            String pi = req.getPathInfo();
            String reqPath = req.getServletPath();
            if (reqPath == null) {
                reqPath = "";
            }
            if ((lastSlash = (reqPath = reqPath + (pi == null ? "" : pi)).lastIndexOf(47)) != -1) {
                return reqPath.substring(0, lastSlash + 1) + uri;
            }
            return '/' + uri;
        }
        throw new TaglibGettingException("Can't resolve relative URI " + uri + " as request URL information is unavailable.");
    }

    private static FilterInputStream toCloseIgnoring(InputStream in) {
        return new FilterInputStream(in){

            @Override
            public void close() {
            }
        };
    }

    private static int getUriType(String uri) throws MalformedURLException {
        if (uri == null) {
            throw new IllegalArgumentException("null is not a valid URI");
        }
        if (uri.length() == 0) {
            throw new MalformedURLException("empty string is not a valid URI");
        }
        char c0 = uri.charAt(0);
        if (c0 == '/') {
            return 1;
        }
        if (c0 < 'a' || c0 > 'z') {
            return 2;
        }
        int colon = uri.indexOf(58);
        if (colon == -1) {
            return 2;
        }
        for (int i = 1; i < colon; ++i) {
            char c = uri.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '+' || c == '-' || c == '.') continue;
            return 2;
        }
        return 0;
    }

    private static boolean isJarPath(String uriPath) {
        return uriPath.endsWith(".jar") || uriPath.endsWith(".zip");
    }

    private static boolean isJarUrl(URL url) {
        String scheme = url.getProtocol();
        return "jar".equals(scheme) || "zip".equals(scheme) || "vfszip".equals(scheme) || "wsjar".equals(scheme);
    }

    private static URL createJarEntryUrl(URL jarBaseEntryUrl, String relativeEntryPath) throws MalformedURLException {
        if (relativeEntryPath.startsWith("/")) {
            relativeEntryPath = relativeEntryPath.substring(1);
        }
        try {
            return new URL(jarBaseEntryUrl, StringUtil.URLPathEnc(relativeEntryPath, PLATFORM_FILE_ENCODING));
        }
        catch (UnsupportedEncodingException e) {
            throw new BugException();
        }
    }

    private static String normalizeJarEntryPath(String jarEntryDirPath, boolean directory) {
        if (!jarEntryDirPath.startsWith("/")) {
            jarEntryDirPath = "/" + jarEntryDirPath;
        }
        if (directory && !jarEntryDirPath.endsWith("/")) {
            jarEntryDirPath = jarEntryDirPath + "/";
        }
        return jarEntryDirPath;
    }

    private static MalformedURLException newFailedToExtractEntryPathException(URL url) {
        return new MalformedURLException("Failed to extract jar entry path from: " + url);
    }

    private File urlToFileOrNull(URL url) {
        String filePath;
        if (this.test_emulateNoUrlToFileConversions) {
            return null;
        }
        if (!"file".equals(url.getProtocol())) {
            return null;
        }
        try {
            filePath = url.toURI().getSchemeSpecificPart();
        }
        catch (URISyntaxException e) {
            try {
                filePath = URLDecoder.decode(url.getFile(), PLATFORM_FILE_ENCODING);
            }
            catch (UnsupportedEncodingException e2) {
                throw new BugException(e2);
            }
        }
        return new File(filePath);
    }

    private JarFile servletContextResourceToFileOrNull(String jarResourcePath) throws MalformedURLException, IOException {
        URL jarResourceUrl = this.servletContext.getResource(jarResourcePath);
        if (jarResourceUrl == null) {
            LOG.error("ServletContext resource URL was null (missing resource?): " + jarResourcePath);
            return null;
        }
        File jarResourceAsFile = this.urlToFileOrNull(jarResourceUrl);
        if (jarResourceAsFile == null) {
            return null;
        }
        if (!jarResourceAsFile.isFile()) {
            LOG.error("Jar file doesn't exist - falling back to stream mode: " + jarResourceAsFile);
            return null;
        }
        return new JarFile(jarResourceAsFile);
    }

    private static URL tryCreateServletContextJarEntryUrl(ServletContext servletContext, String servletContextJarFilePath, String entryPath) {
        try {
            URL jarFileUrl = servletContext.getResource(servletContextJarFilePath);
            if (jarFileUrl == null) {
                throw new IOException("Servlet context resource not found: " + servletContextJarFilePath);
            }
            return new URL("jar:" + jarFileUrl.toURI() + JAR_URL_ENTRY_PATH_START + URLEncoder.encode(entryPath.startsWith("/") ? entryPath.substring(1) : entryPath, PLATFORM_FILE_ENCODING));
        }
        catch (Exception e) {
            LOG.error("Couldn't get URL for serlvetContext resource " + StringUtil.jQuoteNoXSS(servletContextJarFilePath) + " / jar entry " + StringUtil.jQuoteNoXSS(entryPath), e);
            return null;
        }
    }

    private static boolean isTldFileNameIgnoreCase(String name) {
        int dotIdx = name.lastIndexOf(46);
        if (dotIdx < 0) {
            return false;
        }
        String extension = name.substring(dotIdx + 1).toLowerCase();
        return extension.equalsIgnoreCase("tld");
    }

    private static ClassLoader tryGetThreadContextClassLoader() {
        ClassLoader tccl;
        try {
            tccl = Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException e) {
            tccl = null;
            LOG.warn("Can't access Thread Context ClassLoader", e);
        }
        return tccl;
    }

    private static boolean isDescendantOfOrSameAs(ClassLoader descendant, ClassLoader parent) {
        while (descendant != null) {
            if (descendant == parent) {
                return true;
            }
            descendant = descendant.getParent();
        }
        return false;
    }

    private static class TaglibGettingException
    extends Exception {
        public TaglibGettingException(String message, Throwable cause) {
            super(message, cause);
        }

        public TaglibGettingException(String message) {
            super(message);
        }
    }

    private static class URLWithExternalForm
    implements Comparable {
        private final URL url;
        private final String externalForm;

        public URLWithExternalForm(URL url) {
            this.url = url;
            this.externalForm = url.toExternalForm();
        }

        public URL getUrl() {
            return this.url;
        }

        public String getExternalForm() {
            return this.externalForm;
        }

        public int hashCode() {
            return this.externalForm.hashCode();
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null) {
                return false;
            }
            if (this.getClass() != that.getClass()) {
                return false;
            }
            return !this.externalForm.equals(((URLWithExternalForm)that).externalForm);
        }

        public String toString() {
            return "URLWithExternalForm(" + this.externalForm + ")";
        }

        public int compareTo(Object that) {
            return this.getExternalForm().compareTo(((URLWithExternalForm)that).getExternalForm());
        }
    }

    private static class TldParsingSAXException
    extends SAXParseException {
        private final Throwable cause;

        TldParsingSAXException(String message, Locator locator) {
            this(message, locator, (Throwable)null);
        }

        TldParsingSAXException(String message, Locator locator, Throwable e) {
            super(message, locator, e instanceof Exception ? (Exception)e : new Exception("Unchecked exception; see cause", e));
            this.cause = e;
        }

        @Override
        public String toString() {
            String message;
            int line;
            StringBuilder sb = new StringBuilder(this.getClass().getName());
            sb.append(": ");
            int startLn = sb.length();
            String systemId = this.getSystemId();
            String publicId = this.getPublicId();
            if (systemId != null || publicId != null) {
                sb.append("In ");
                if (systemId != null) {
                    sb.append(systemId);
                }
                if (publicId != null) {
                    if (systemId != null) {
                        sb.append(" (public ID: ");
                    }
                    sb.append(publicId);
                    if (systemId != null) {
                        sb.append(')');
                    }
                }
            }
            if ((line = this.getLineNumber()) != -1) {
                sb.append(sb.length() != startLn ? ", at " : "At ");
                sb.append("line ");
                sb.append(line);
                int col = this.getColumnNumber();
                if (col != -1) {
                    sb.append(", column ");
                    sb.append(col);
                }
            }
            if ((message = this.getLocalizedMessage()) != null) {
                if (sb.length() != startLn) {
                    sb.append(":\n");
                }
                sb.append(message);
            }
            return sb.toString();
        }

        @Override
        public Throwable getCause() {
            Throwable superCause = super.getCause();
            return superCause == null ? this.cause : superCause;
        }
    }

    private static final class EmptyContentEntityResolver
    implements EntityResolver {
        private EmptyContentEntityResolver() {
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            InputSource is = new InputSource(new ByteArrayInputStream(new byte[0]));
            is.setPublicId(publicId);
            is.setSystemId(systemId);
            return is;
        }
    }

    static final class TldParserForTaglibBuilding
    extends DefaultHandler {
        private static final String E_TAG = "tag";
        private static final String E_NAME = "name";
        private static final String E_TAG_CLASS = "tag-class";
        private static final String E_TAG_CLASS_LEGACY = "tagclass";
        private static final String E_FUNCTION = "function";
        private static final String E_FUNCTION_CLASS = "function-class";
        private static final String E_FUNCTION_SIGNATURE = "function-signature";
        private static final String E_LISTENER = "listener";
        private static final String E_LISTENER_CLASS = "listener-class";
        private final BeansWrapper beansWrapper;
        private final Map<String, TemplateModel> tagsAndFunctions = new HashMap<String, TemplateModel>();
        private final List listeners = new ArrayList();
        private Locator locator;
        private StringBuilder cDataCollector;
        private Stack stack = new Stack();
        private String tagNameCData;
        private String tagClassCData;
        private String functionNameCData;
        private String functionClassCData;
        private String functionSignatureCData;
        private String listenerClassCData;

        TldParserForTaglibBuilding(ObjectWrapper wrapper) {
            if (wrapper instanceof BeansWrapper) {
                this.beansWrapper = (BeansWrapper)wrapper;
            } else {
                this.beansWrapper = null;
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Custom EL functions won't be loaded because " + (wrapper == null ? "no ObjectWrapper was specified for the TaglibFactory (via TaglibFactory.setObjectWrapper(...), exists since 2.3.22)" : "the ObjectWrapper wasn't instance of " + BeansWrapper.class.getName()) + ".");
                }
            }
        }

        Map<String, TemplateModel> getTagsAndFunctions() {
            return this.tagsAndFunctions;
        }

        List getListeners() {
            return this.listeners;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(String nsUri, String localName, String qName, Attributes atts) {
            this.stack.push(qName);
            if (this.stack.size() == 3 && (E_NAME.equals(qName) || E_TAG_CLASS_LEGACY.equals(qName) || E_TAG_CLASS.equals(qName) || E_LISTENER_CLASS.equals(qName) || E_FUNCTION_CLASS.equals(qName) || E_FUNCTION_SIGNATURE.equals(qName))) {
                this.cDataCollector = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] chars, int off, int len) {
            if (this.cDataCollector != null) {
                this.cDataCollector.append(chars, off, len);
            }
        }

        @Override
        public void endElement(String nsuri, String localName, String qName) throws TldParsingSAXException {
            if (!this.stack.peek().equals(qName)) {
                throw new TldParsingSAXException("Unbalanced tag nesting at \"" + qName + "\" end-tag.", this.locator);
            }
            if (this.stack.size() == 3) {
                if (E_NAME.equals(qName)) {
                    if (E_TAG.equals(this.stack.get(1))) {
                        this.tagNameCData = this.pullCData();
                    } else if (E_FUNCTION.equals(this.stack.get(1))) {
                        this.functionNameCData = this.pullCData();
                    }
                } else if (E_TAG_CLASS_LEGACY.equals(qName) || E_TAG_CLASS.equals(qName)) {
                    this.tagClassCData = this.pullCData();
                } else if (E_LISTENER_CLASS.equals(qName)) {
                    this.listenerClassCData = this.pullCData();
                } else if (E_FUNCTION_CLASS.equals(qName)) {
                    this.functionClassCData = this.pullCData();
                } else if (E_FUNCTION_SIGNATURE.equals(qName)) {
                    this.functionSignatureCData = this.pullCData();
                }
            } else if (this.stack.size() == 2) {
                if (E_TAG.equals(qName)) {
                    JspTagModelBase customTagModel;
                    this.checkChildElementNotNull(qName, E_NAME, this.tagNameCData);
                    this.checkChildElementNotNull(qName, E_TAG_CLASS, this.tagClassCData);
                    Class tagClass = this.resoveClassFromTLD(this.tagClassCData, "custom tag", this.tagNameCData);
                    try {
                        customTagModel = Tag.class.isAssignableFrom(tagClass) ? new TagTransformModel(this.tagNameCData, tagClass) : new SimpleTagDirectiveModel(this.tagNameCData, tagClass);
                    }
                    catch (IntrospectionException e) {
                        throw new TldParsingSAXException("JavaBean introspection failed on custom tag class " + this.tagClassCData, this.locator, (Throwable)e);
                    }
                    TemplateModel replacedTagOrFunction = this.tagsAndFunctions.put(this.tagNameCData, (TemplateModel)((Object)customTagModel));
                    if (replacedTagOrFunction != null) {
                        if (CustomTagAndELFunctionCombiner.canBeCombinedAsELFunction(replacedTagOrFunction)) {
                            this.tagsAndFunctions.put(this.tagNameCData, CustomTagAndELFunctionCombiner.combine(customTagModel, (TemplateMethodModelEx)replacedTagOrFunction));
                        } else {
                            LOG.warn("TLD contains multiple tags with name " + StringUtil.jQuote(this.tagNameCData) + "; keeping only the last one.");
                        }
                    }
                    this.tagNameCData = null;
                    this.tagClassCData = null;
                } else if (E_FUNCTION.equals(qName) && this.beansWrapper != null) {
                    TemplateMethodModelEx elFunctionModel;
                    Method functionMethod;
                    this.checkChildElementNotNull(qName, E_FUNCTION_CLASS, this.functionClassCData);
                    this.checkChildElementNotNull(qName, E_FUNCTION_SIGNATURE, this.functionSignatureCData);
                    this.checkChildElementNotNull(qName, E_NAME, this.functionNameCData);
                    Class functionClass = this.resoveClassFromTLD(this.functionClassCData, "custom EL function", this.functionNameCData);
                    try {
                        functionMethod = TaglibMethodUtil.getMethodByFunctionSignature(functionClass, this.functionSignatureCData);
                    }
                    catch (Exception e) {
                        throw new TldParsingSAXException("Error while trying to resolve signature " + StringUtil.jQuote(this.functionSignatureCData) + " on class " + StringUtil.jQuote(functionClass.getName()) + " for custom EL function " + StringUtil.jQuote(this.functionNameCData) + ".", this.locator, (Throwable)e);
                    }
                    int modifiers = functionMethod.getModifiers();
                    if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                        throw new TldParsingSAXException("The custom EL function method must be public and static: " + functionMethod, this.locator);
                    }
                    try {
                        elFunctionModel = this.beansWrapper.wrap(null, functionMethod);
                    }
                    catch (Exception e) {
                        throw new TldParsingSAXException("FreeMarker object wrapping failed on method : " + functionMethod, this.locator);
                    }
                    TemplateModel replacedTagOrFunction = this.tagsAndFunctions.put(this.functionNameCData, elFunctionModel);
                    if (replacedTagOrFunction != null) {
                        if (CustomTagAndELFunctionCombiner.canBeCombinedAsCustomTag(replacedTagOrFunction)) {
                            this.tagsAndFunctions.put(this.functionNameCData, CustomTagAndELFunctionCombiner.combine(replacedTagOrFunction, elFunctionModel));
                        } else {
                            LOG.warn("TLD contains multiple functions with name " + StringUtil.jQuote(this.functionNameCData) + "; keeping only the last one.");
                        }
                    }
                    this.functionNameCData = null;
                    this.functionClassCData = null;
                    this.functionSignatureCData = null;
                } else if (E_LISTENER.equals(qName)) {
                    Object listener;
                    this.checkChildElementNotNull(qName, E_LISTENER_CLASS, this.listenerClassCData);
                    Class listenerClass = this.resoveClassFromTLD(this.listenerClassCData, E_LISTENER, null);
                    try {
                        listener = listenerClass.newInstance();
                    }
                    catch (Exception e) {
                        throw new TldParsingSAXException("Failed to create new instantiate from listener class " + this.listenerClassCData, this.locator, (Throwable)e);
                    }
                    this.listeners.add(listener);
                    this.listenerClassCData = null;
                }
            }
            this.stack.pop();
        }

        private String pullCData() {
            String r = this.cDataCollector.toString().trim();
            this.cDataCollector = null;
            return r;
        }

        private void checkChildElementNotNull(String parentElementName, String childElementName, String value) throws TldParsingSAXException {
            if (value == null) {
                throw new TldParsingSAXException("Missing required \"" + childElementName + "\" element inside the \"" + parentElementName + "\" element.", this.locator);
            }
        }

        private Class resoveClassFromTLD(String className, String entryType, String entryName) throws TldParsingSAXException {
            try {
                return ClassUtil.forName(className);
            }
            catch (ClassNotFoundException | LinkageError e) {
                throw this.newTLDEntryClassLoadingException(e, className, entryType, entryName);
            }
        }

        private TldParsingSAXException newTLDEntryClassLoadingException(Throwable e, String className, String entryType, String entryName) throws TldParsingSAXException {
            int dotIdx = className.lastIndexOf(46);
            if (dotIdx != -1) {
                dotIdx = className.lastIndexOf(46, dotIdx - 1);
            }
            boolean looksLikeNestedClass = dotIdx != -1 && className.length() > dotIdx + 1 && Character.isUpperCase(className.charAt(dotIdx + 1));
            return new TldParsingSAXException((e instanceof ClassNotFoundException ? "Not found class " : "Can't load class ") + StringUtil.jQuote(className) + " for " + entryType + (entryName != null ? " " + StringUtil.jQuote(entryName) : "") + "." + (looksLikeNestedClass ? " Hint: Before nested classes, use \"$\", not \".\"." : ""), this.locator, e);
        }
    }

    private static class TldParserForTaglibUriExtraction
    extends DefaultHandler {
        private static final String E_URI = "uri";
        private StringBuilder cDataCollector;
        private String uri;

        TldParserForTaglibUriExtraction() {
        }

        String getTaglibUri() {
            return this.uri;
        }

        @Override
        public void startElement(String nsuri, String localName, String qName, Attributes atts) {
            if (E_URI.equals(qName)) {
                this.cDataCollector = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] chars, int off, int len) {
            if (this.cDataCollector != null) {
                this.cDataCollector.append(chars, off, len);
            }
        }

        @Override
        public void endElement(String nsuri, String localName, String qName) {
            if (E_URI.equals(qName)) {
                this.uri = this.cDataCollector.toString().trim();
                this.cDataCollector = null;
            }
        }
    }

    private class WebXmlParser
    extends DefaultHandler {
        private static final String E_TAGLIB = "taglib";
        private static final String E_TAGLIB_LOCATION = "taglib-location";
        private static final String E_TAGLIB_URI = "taglib-uri";
        private StringBuilder cDataCollector;
        private String taglibUriCData;
        private String taglibLocationCData;
        private Locator locator;

        private WebXmlParser() {
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(String nsuri, String localName, String qName, Attributes atts) {
            if (E_TAGLIB_URI.equals(qName) || E_TAGLIB_LOCATION.equals(qName)) {
                this.cDataCollector = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] chars, int off, int len) {
            if (this.cDataCollector != null) {
                this.cDataCollector.append(chars, off, len);
            }
        }

        @Override
        public void endElement(String nsUri, String localName, String qName) throws TldParsingSAXException {
            if (E_TAGLIB_URI.equals(qName)) {
                this.taglibUriCData = this.cDataCollector.toString().trim();
                this.cDataCollector = null;
            } else if (E_TAGLIB_LOCATION.equals(qName)) {
                this.taglibLocationCData = this.cDataCollector.toString().trim();
                if (this.taglibLocationCData.length() == 0) {
                    throw new TldParsingSAXException("Required \"taglib-uri\" element was missing or empty", this.locator);
                }
                try {
                    if (TaglibFactory.getUriType(this.taglibLocationCData) == 2) {
                        this.taglibLocationCData = "/WEB-INF/" + this.taglibLocationCData;
                    }
                }
                catch (MalformedURLException e) {
                    throw new TldParsingSAXException("Failed to detect URI type for: " + this.taglibLocationCData, this.locator, (Throwable)e);
                }
                this.cDataCollector = null;
            } else if (E_TAGLIB.equals(qName)) {
                TaglibFactory.this.addTldLocation(TaglibFactory.isJarPath(this.taglibLocationCData) ? new ServletContextJarEntryTldLocation(this.taglibLocationCData, TaglibFactory.DEFAULT_TLD_RESOURCE_PATH) : new ServletContextTldLocation(this.taglibLocationCData), this.taglibUriCData);
            }
        }
    }

    private static final class Taglib
    implements TemplateHashModel {
        private final Map tagsAndFunctions;

        Taglib(ServletContext ctx, TldLocation tldPath, ObjectWrapper wrapper) throws IOException, SAXException {
            this.tagsAndFunctions = Taglib.parseToTagsAndFunctions(ctx, tldPath, wrapper);
        }

        @Override
        public TemplateModel get(String key) {
            return (TemplateModel)this.tagsAndFunctions.get(key);
        }

        @Override
        public boolean isEmpty() {
            return this.tagsAndFunctions.isEmpty();
        }

        private static final Map parseToTagsAndFunctions(ServletContext ctx, TldLocation tldLocation, ObjectWrapper objectWrapper) throws IOException, SAXException {
            TldParserForTaglibBuilding tldParser = new TldParserForTaglibBuilding(objectWrapper);
            try (InputStream in = tldLocation.getInputStream();){
                TaglibFactory.parseXml(in, tldLocation.getXmlSystemId(), tldParser);
            }
            EventForwarding eventForwarding = EventForwarding.getInstance(ctx);
            if (eventForwarding != null) {
                eventForwarding.addListeners(tldParser.getListeners());
            } else if (tldParser.getListeners().size() > 0) {
                throw new TldParsingSAXException("Event listeners specified in the TLD could not be  registered since the web application doesn't have a listener of class " + EventForwarding.class.getName() + ". To remedy this, add this element to web.xml:\n| <listener>\n|   <listener-class>" + EventForwarding.class.getName() + "</listener-class>\n| </listener>", null);
            }
            return tldParser.getTagsAndFunctions();
        }
    }

    private static class FileTldLocation
    implements TldLocation {
        private final File file;

        public FileTldLocation(File file) {
            this.file = file;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(this.file);
        }

        @Override
        public String getXmlSystemId() throws IOException {
            return this.file.toURI().toURL().toExternalForm();
        }

        public String toString() {
            return this.file.toString();
        }
    }

    private class ServletContextJarEntryTldLocation
    extends JarEntryTldLocation {
        private ServletContextJarEntryTldLocation(final String servletContextJarFilePath, String entryPath) {
            super(TaglibFactory.tryCreateServletContextJarEntryUrl(TaglibFactory.this.servletContext, servletContextJarFilePath, entryPath), new InputStreamFactory(){

                @Override
                public InputStream getInputStream() {
                    return TaglibFactory.this.servletContext.getResourceAsStream(servletContextJarFilePath);
                }

                public String toString() {
                    return "servletContext:" + servletContextJarFilePath;
                }
            }, entryPath);
        }
    }

    private class JarEntryUrlTldLocation
    extends JarEntryTldLocation {
        private JarEntryUrlTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory) {
            super(entryUrl, fallbackRawJarContentInputStreamFactory, null);
        }
    }

    private abstract class JarEntryTldLocation
    implements TldLocation {
        private final URL entryUrl;
        private final InputStreamFactory fallbackRawJarContentInputStreamFactory;
        private final String entryPath;

        public JarEntryTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory, String entryPath) {
            if (entryUrl == null) {
                NullArgumentException.check(fallbackRawJarContentInputStreamFactory);
                NullArgumentException.check(entryPath);
            }
            this.entryUrl = entryUrl;
            this.fallbackRawJarContentInputStreamFactory = fallbackRawJarContentInputStreamFactory;
            this.entryPath = entryPath != null ? TaglibFactory.normalizeJarEntryPath(entryPath, false) : null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            String entryPath;
            if (this.entryUrl != null) {
                try {
                    if (TaglibFactory.this.test_emulateJarEntryUrlOpenStreamFails) {
                        throw new RuntimeException("Test only");
                    }
                    return this.entryUrl.openStream();
                }
                catch (Exception e) {
                    if (this.fallbackRawJarContentInputStreamFactory == null) {
                        if (e instanceof IOException) {
                            throw (IOException)e;
                        }
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException)e;
                        }
                        throw new RuntimeException(e);
                    }
                    LOG.error("Failed to open InputStream for URL (will try fallback stream): " + this.entryUrl);
                }
            }
            if (this.entryPath != null) {
                entryPath = this.entryPath;
            } else {
                if (this.entryUrl == null) {
                    throw new IOException("Nothing to deduce jar entry path from.");
                }
                String urlEF = this.entryUrl.toExternalForm();
                int sepIdx = urlEF.indexOf(TaglibFactory.JAR_URL_ENTRY_PATH_START);
                if (sepIdx == -1) {
                    throw new IOException("Couldn't extract jar entry path from: " + urlEF);
                }
                entryPath = TaglibFactory.normalizeJarEntryPath(URLDecoder.decode(urlEF.substring(sepIdx + TaglibFactory.JAR_URL_ENTRY_PATH_START.length()), PLATFORM_FILE_ENCODING), false);
            }
            InputStream rawIn = null;
            ZipInputStream zipIn = null;
            boolean returnedZipIn = false;
            try {
                rawIn = this.fallbackRawJarContentInputStreamFactory.getInputStream();
                if (rawIn == null) {
                    throw new IOException("Jar's InputStreamFactory (" + this.fallbackRawJarContentInputStreamFactory + ") says the resource doesn't exist.");
                }
                zipIn = new ZipInputStream(rawIn);
                while (true) {
                    ZipEntry macthedJarEntry;
                    if ((macthedJarEntry = zipIn.getNextEntry()) == null) {
                        throw new IOException("Could not find JAR entry " + StringUtil.jQuoteNoXSS(entryPath) + ".");
                    }
                    if (!entryPath.equals(TaglibFactory.normalizeJarEntryPath(macthedJarEntry.getName(), false))) continue;
                    returnedZipIn = true;
                    ZipInputStream zipInputStream = zipIn;
                    return zipInputStream;
                }
            }
            finally {
                if (!returnedZipIn) {
                    if (zipIn != null) {
                        zipIn.close();
                    }
                    if (rawIn != null) {
                        rawIn.close();
                    }
                }
            }
        }

        @Override
        public String getXmlSystemId() {
            return this.entryUrl != null ? this.entryUrl.toExternalForm() : null;
        }

        public String toString() {
            return this.entryUrl != null ? this.entryUrl.toExternalForm() : "jar:{" + this.fallbackRawJarContentInputStreamFactory + "}!" + this.entryPath;
        }
    }

    private static class ClasspathTldLocation
    implements TldLocation {
        private final String resourcePath;

        public ClasspathTldLocation(String resourcePath) {
            if (!resourcePath.startsWith("/")) {
                throw new IllegalArgumentException("\"resourcePath\" must start with /");
            }
            this.resourcePath = resourcePath;
        }

        public String toString() {
            return "classpath:" + this.resourcePath;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            InputStream ins;
            ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
            if (tccl != null && (ins = ClassUtil.getReasourceAsStream(tccl, this.resourcePath, true)) != null) {
                return ins;
            }
            return ClassUtil.getReasourceAsStream(this.getClass(), this.resourcePath, false);
        }

        @Override
        public String getXmlSystemId() throws IOException {
            URL url;
            ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
            if (tccl != null && (url = tccl.getResource(this.resourcePath)) != null) {
                return url.toExternalForm();
            }
            url = this.getClass().getResource(this.resourcePath);
            return url == null ? null : url.toExternalForm();
        }
    }

    private class ServletContextTldLocation
    implements TldLocation {
        private final String fileResourcePath;

        public ServletContextTldLocation(String fileResourcePath) {
            this.fileResourcePath = fileResourcePath;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            InputStream in = TaglibFactory.this.servletContext.getResourceAsStream(this.fileResourcePath);
            if (in == null) {
                throw this.newResourceNotFoundException();
            }
            return in;
        }

        @Override
        public String getXmlSystemId() throws IOException {
            URL url = TaglibFactory.this.servletContext.getResource(this.fileResourcePath);
            return url != null ? url.toExternalForm() : null;
        }

        private IOException newResourceNotFoundException() {
            return new IOException("Resource not found: servletContext:" + this.fileResourcePath);
        }

        public final String toString() {
            return "servletContext:" + this.fileResourcePath;
        }
    }

    private static interface InputStreamFactory {
        public InputStream getInputStream();
    }

    private static interface TldLocation {
        public InputStream getInputStream() throws IOException;

        public String getXmlSystemId() throws IOException;
    }

    public static final class ClearMetaInfTldSource
    extends MetaInfTldSource {
        public static final ClearMetaInfTldSource INSTANCE = new ClearMetaInfTldSource();

        private ClearMetaInfTldSource() {
        }
    }

    public static final class ClasspathMetaInfTldSource
    extends MetaInfTldSource {
        private final Pattern rootContainerPattern;

        public ClasspathMetaInfTldSource(Pattern rootContainerPattern) {
            this.rootContainerPattern = rootContainerPattern;
        }

        public Pattern getRootContainerPattern() {
            return this.rootContainerPattern;
        }
    }

    public static final class WebInfPerLibJarMetaInfTldSource
    extends MetaInfTldSource {
        public static final WebInfPerLibJarMetaInfTldSource INSTANCE = new WebInfPerLibJarMetaInfTldSource();

        private WebInfPerLibJarMetaInfTldSource() {
        }
    }

    public static abstract class MetaInfTldSource {
        private MetaInfTldSource() {
        }
    }
}

