/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.http.RequestUtil
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.ObjectName;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.TrackedWebResource;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.util.LifecycleBase;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.webresources.AbstractResourceSet;
import org.apache.catalina.webresources.Cache;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.FileResourceSet;
import org.apache.catalina.webresources.JarResourceSet;
import org.apache.catalina.webresources.JarWarResourceSet;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.catalina.webresources.WarResourceSet;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;

public class StandardRoot
extends LifecycleMBeanBase
implements WebResourceRoot {
    private static final Log log = LogFactory.getLog(StandardRoot.class);
    protected static final StringManager sm = StringManager.getManager(StandardRoot.class);
    private Context context;
    private boolean allowLinking = false;
    private final List<WebResourceSet> preResources = new ArrayList<WebResourceSet>();
    private WebResourceSet main;
    private final List<WebResourceSet> classResources = new ArrayList<WebResourceSet>();
    private final List<WebResourceSet> jarResources = new ArrayList<WebResourceSet>();
    private final List<WebResourceSet> postResources = new ArrayList<WebResourceSet>();
    private final Cache cache = new Cache(this);
    private boolean cachingAllowed = true;
    private ObjectName cacheJmxName = null;
    private boolean trackLockedFiles = false;
    private final Set<TrackedWebResource> trackedResources = ConcurrentHashMap.newKeySet();
    private WebResourceRoot.ArchiveIndexStrategy archiveIndexStrategy = WebResourceRoot.ArchiveIndexStrategy.SIMPLE;
    private final List<WebResourceSet> mainResources = new ArrayList<WebResourceSet>();
    private final List<List<WebResourceSet>> allResources = new ArrayList<List<WebResourceSet>>();

    public StandardRoot() {
        this.allResources.add(this.preResources);
        this.allResources.add(this.mainResources);
        this.allResources.add(this.classResources);
        this.allResources.add(this.jarResources);
        this.allResources.add(this.postResources);
    }

    public StandardRoot(Context context) {
        this.allResources.add(this.preResources);
        this.allResources.add(this.mainResources);
        this.allResources.add(this.classResources);
        this.allResources.add(this.jarResources);
        this.allResources.add(this.postResources);
        this.context = context;
    }

    @Override
    public String[] list(String path) {
        return this.list(path, true);
    }

    private String[] list(String path, boolean validate) {
        if (validate) {
            path = this.validate(path);
        }
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if (webResourceSet.getClassLoaderOnly()) continue;
                String[] entries = webResourceSet.list(path);
                result.addAll(Arrays.asList(entries));
            }
        }
        return result.toArray(new String[0]);
    }

    @Override
    public Set<String> listWebAppPaths(String path) {
        path = this.validate(path);
        HashSet<String> result = new HashSet<String>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if (webResourceSet.getClassLoaderOnly()) continue;
                result.addAll(webResourceSet.listWebAppPaths(path));
            }
        }
        if (result.size() == 0) {
            return null;
        }
        return result;
    }

    @Override
    public boolean mkdir(String path) {
        if (this.preResourceExists(path = this.validate(path))) {
            return false;
        }
        boolean mkdirResult = this.main.mkdir(path);
        if (mkdirResult && this.isCachingAllowed()) {
            this.cache.removeCacheEntry(path);
        }
        return mkdirResult;
    }

    @Override
    public boolean write(String path, InputStream is, boolean overwrite) {
        path = this.validate(path);
        if (!overwrite && this.preResourceExists(path)) {
            return false;
        }
        boolean writeResult = this.main.write(path, is, overwrite);
        if (writeResult && this.isCachingAllowed()) {
            this.cache.removeCacheEntry(path);
        }
        return writeResult;
    }

    private boolean preResourceExists(String path) {
        for (WebResourceSet webResourceSet : this.preResources) {
            WebResource webResource = webResourceSet.getResource(path);
            if (!webResource.exists()) continue;
            return true;
        }
        return false;
    }

    @Override
    public WebResource getResource(String path) {
        return this.getResource(path, true, false);
    }

    protected WebResource getResource(String path, boolean validate, boolean useClassLoaderResources) {
        if (validate) {
            path = this.validate(path);
        }
        if (this.isCachingAllowed()) {
            return this.cache.getResource(path, useClassLoaderResources);
        }
        return this.getResourceInternal(path, useClassLoaderResources);
    }

    @Override
    public WebResource getClassLoaderResource(String path) {
        return this.getResource("/WEB-INF/classes" + path, true, true);
    }

    @Override
    public WebResource[] getClassLoaderResources(String path) {
        return this.getResources("/WEB-INF/classes" + path, true);
    }

    private String validate(String path) {
        if (!this.getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("standardRoot.checkStateNotStarted"));
        }
        if (path == null || path.length() == 0 || !path.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("standardRoot.invalidPath", new Object[]{path}));
        }
        String result = File.separatorChar == '\\' ? RequestUtil.normalize((String)path, (boolean)true) : RequestUtil.normalize((String)path, (boolean)false);
        if (result == null || result.length() == 0 || !result.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("standardRoot.invalidPathNormal", new Object[]{path, result}));
        }
        return result;
    }

    protected final WebResource getResourceInternal(String path, boolean useClassLoaderResources) {
        WebResource result = null;
        WebResource virtual = null;
        WebResource mainEmpty = null;
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if ((useClassLoaderResources || webResourceSet.getClassLoaderOnly()) && (!useClassLoaderResources || webResourceSet.getStaticOnly())) continue;
                result = webResourceSet.getResource(path);
                if (result.exists()) {
                    return result;
                }
                if (virtual != null) continue;
                if (result.isVirtual()) {
                    virtual = result;
                    continue;
                }
                if (!this.main.equals(webResourceSet)) continue;
                mainEmpty = result;
            }
        }
        if (virtual != null) {
            return virtual;
        }
        return mainEmpty;
    }

    @Override
    public WebResource[] getResources(String path) {
        return this.getResources(path, false);
    }

    private WebResource[] getResources(String path, boolean useClassLoaderResources) {
        path = this.validate(path);
        if (this.isCachingAllowed()) {
            return this.cache.getResources(path, useClassLoaderResources);
        }
        return this.getResourcesInternal(path, useClassLoaderResources);
    }

    protected WebResource[] getResourcesInternal(String path, boolean useClassLoaderResources) {
        ArrayList<WebResource> result = new ArrayList<WebResource>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                WebResource webResource;
                if (!useClassLoaderResources && webResourceSet.getClassLoaderOnly() || !(webResource = webResourceSet.getResource(path)).exists()) continue;
                result.add(webResource);
            }
        }
        if (result.size() == 0) {
            result.add(this.main.getResource(path));
        }
        return result.toArray(new WebResource[0]);
    }

    @Override
    public WebResource[] listResources(String path) {
        return this.listResources(path, true);
    }

    protected WebResource[] listResources(String path, boolean validate) {
        if (validate) {
            path = this.validate(path);
        }
        String[] resources = this.list(path, false);
        WebResource[] result = new WebResource[resources.length];
        for (int i = 0; i < resources.length; ++i) {
            result[i] = path.charAt(path.length() - 1) == '/' ? this.getResource(path + resources[i], false, false) : this.getResource(path + '/' + resources[i], false, false);
        }
        return result;
    }

    @Override
    public void createWebResourceSet(WebResourceRoot.ResourceSetType type, String webAppMount, URL url, String internalPath) {
        BaseLocation baseLocation = new BaseLocation(url);
        this.createWebResourceSet(type, webAppMount, baseLocation.getBasePath(), baseLocation.getArchivePath(), internalPath);
    }

    @Override
    public void createWebResourceSet(WebResourceRoot.ResourceSetType type, String webAppMount, String base, String archivePath, String internalPath) {
        AbstractResourceSet resourceSet;
        List<WebResourceSet> resourceList;
        switch (type) {
            case PRE: {
                resourceList = this.preResources;
                break;
            }
            case CLASSES_JAR: {
                resourceList = this.classResources;
                break;
            }
            case RESOURCE_JAR: {
                resourceList = this.jarResources;
                break;
            }
            case POST: {
                resourceList = this.postResources;
                break;
            }
            default: {
                throw new IllegalArgumentException(sm.getString("standardRoot.createUnknownType", new Object[]{type}));
            }
        }
        File file = new File(base);
        if (file.isFile()) {
            resourceSet = archivePath != null ? new JarWarResourceSet(this, webAppMount, base, archivePath, internalPath) : (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar") ? new JarResourceSet(this, webAppMount, base, internalPath) : new FileResourceSet(this, webAppMount, base, internalPath));
        } else if (file.isDirectory()) {
            resourceSet = new DirResourceSet(this, webAppMount, base, internalPath);
        } else {
            throw new IllegalArgumentException(sm.getString("standardRoot.createInvalidFile", new Object[]{file}));
        }
        if (type.equals((Object)WebResourceRoot.ResourceSetType.CLASSES_JAR)) {
            resourceSet.setClassLoaderOnly(true);
        } else if (type.equals((Object)WebResourceRoot.ResourceSetType.RESOURCE_JAR)) {
            resourceSet.setStaticOnly(true);
        }
        resourceList.add(resourceSet);
    }

    @Override
    public void addPreResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.preResources.add(webResourceSet);
    }

    @Override
    public WebResourceSet[] getPreResources() {
        return this.preResources.toArray(new WebResourceSet[0]);
    }

    @Override
    public void addJarResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.jarResources.add(webResourceSet);
    }

    @Override
    public WebResourceSet[] getJarResources() {
        return this.jarResources.toArray(new WebResourceSet[0]);
    }

    @Override
    public void addPostResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.postResources.add(webResourceSet);
    }

    @Override
    public WebResourceSet[] getPostResources() {
        return this.postResources.toArray(new WebResourceSet[0]);
    }

    protected WebResourceSet[] getClassResources() {
        return this.classResources.toArray(new WebResourceSet[0]);
    }

    protected void addClassResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.classResources.add(webResourceSet);
    }

    @Override
    public void setAllowLinking(boolean allowLinking) {
        if (this.allowLinking != allowLinking && this.cachingAllowed) {
            this.cache.clear();
        }
        this.allowLinking = allowLinking;
    }

    @Override
    public boolean getAllowLinking() {
        return this.allowLinking;
    }

    @Override
    public void setCachingAllowed(boolean cachingAllowed) {
        this.cachingAllowed = cachingAllowed;
        if (!cachingAllowed) {
            this.cache.clear();
        }
    }

    @Override
    public boolean isCachingAllowed() {
        return this.cachingAllowed;
    }

    @Override
    public WebResourceRoot.CacheStrategy getCacheStrategy() {
        return this.cache.getCacheStrategy();
    }

    @Override
    public void setCacheStrategy(WebResourceRoot.CacheStrategy strategy) {
        this.cache.setCacheStrategy(strategy);
    }

    @Override
    public long getCacheTtl() {
        return this.cache.getTtl();
    }

    @Override
    public void setCacheTtl(long cacheTtl) {
        this.cache.setTtl(cacheTtl);
    }

    @Override
    public long getCacheMaxSize() {
        return this.cache.getMaxSize();
    }

    @Override
    public void setCacheMaxSize(long cacheMaxSize) {
        this.cache.setMaxSize(cacheMaxSize);
    }

    @Override
    public void setCacheObjectMaxSize(int cacheObjectMaxSize) {
        this.cache.setObjectMaxSize(cacheObjectMaxSize);
        if (this.getState().isAvailable()) {
            this.cache.enforceObjectMaxSizeLimit();
        }
    }

    @Override
    public int getCacheObjectMaxSize() {
        return this.cache.getObjectMaxSize();
    }

    @Override
    public void setTrackLockedFiles(boolean trackLockedFiles) {
        this.trackLockedFiles = trackLockedFiles;
        if (!trackLockedFiles) {
            this.trackedResources.clear();
        }
    }

    @Override
    public boolean getTrackLockedFiles() {
        return this.trackLockedFiles;
    }

    @Override
    public void setArchiveIndexStrategy(String archiveIndexStrategy) {
        this.archiveIndexStrategy = WebResourceRoot.ArchiveIndexStrategy.valueOf(archiveIndexStrategy.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public String getArchiveIndexStrategy() {
        return this.archiveIndexStrategy.name();
    }

    @Override
    public WebResourceRoot.ArchiveIndexStrategy getArchiveIndexStrategyEnum() {
        return this.archiveIndexStrategy;
    }

    public List<String> getTrackedResources() {
        ArrayList<String> result = new ArrayList<String>(this.trackedResources.size());
        for (TrackedWebResource resource : this.trackedResources) {
            result.add(resource.toString());
        }
        return result;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    protected void processWebInfLib() throws LifecycleException {
        WebResource[] possibleJars;
        for (WebResource possibleJar : possibleJars = this.listResources("/WEB-INF/lib", false)) {
            if (!possibleJar.isFile() || !possibleJar.getName().endsWith(".jar")) continue;
            this.createWebResourceSet(WebResourceRoot.ResourceSetType.CLASSES_JAR, "/WEB-INF/classes", possibleJar.getURL(), "/");
        }
    }

    protected final void setMainResources(WebResourceSet main) {
        this.main = main;
        this.mainResources.clear();
        this.mainResources.add(main);
    }

    @Override
    public void backgroundProcess() {
        this.cache.backgroundProcess();
        this.gc();
    }

    @Override
    public void gc() {
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.gc();
            }
        }
    }

    @Override
    public void registerTrackedResource(TrackedWebResource trackedResource) {
        this.trackedResources.add(trackedResource);
    }

    @Override
    public void deregisterTrackedResource(TrackedWebResource trackedResource) {
        this.trackedResources.remove(trackedResource);
    }

    @Override
    public List<URL> getBaseUrls() {
        ArrayList<URL> result = new ArrayList<URL>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                URL url;
                if (webResourceSet.getClassLoaderOnly() || (url = webResourceSet.getBaseUrl()) == null) continue;
                result.add(url);
            }
        }
        return result;
    }

    protected boolean isPackedWarFile() {
        return this.main instanceof WarResourceSet && this.preResources.isEmpty() && this.postResources.isEmpty();
    }

    @Override
    protected String getDomainInternal() {
        return this.context.getDomain();
    }

    @Override
    protected String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("type=WebResourceRoot");
        keyProperties.append(this.context.getMBeanKeyProperties());
        return keyProperties.toString();
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.context == null) {
            throw new IllegalStateException(sm.getString("standardRoot.noContext"));
        }
        this.cacheJmxName = this.register(this.cache, this.getObjectNameKeyProperties() + ",name=Cache");
        this.registerURLStreamHandlerFactory();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.init();
            }
        }
    }

    protected void registerURLStreamHandlerFactory() {
        if (!JreCompat.isGraalAvailable()) {
            TomcatURLStreamHandlerFactory.register();
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        this.mainResources.clear();
        this.main = this.createMainResourceSet();
        this.mainResources.add(this.main);
        for (List<WebResourceSet> list : this.allResources) {
            if (list == this.classResources) continue;
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.start();
            }
        }
        this.processWebInfLib();
        for (WebResourceSet classResource : this.classResources) {
            classResource.start();
        }
        this.cache.enforceObjectMaxSizeLimit();
        this.setState(LifecycleState.STARTING);
    }

    protected WebResourceSet createMainResourceSet() {
        LifecycleBase mainResourceSet;
        String docBase = this.context.getDocBase();
        if (docBase == null) {
            mainResourceSet = new EmptyResourceSet(this);
        } else {
            File f = new File(docBase);
            if (!f.isAbsolute()) {
                f = new File(((Host)this.context.getParent()).getAppBaseFile(), f.getPath());
            }
            if (f.isDirectory()) {
                mainResourceSet = new DirResourceSet(this, "/", f.getAbsolutePath(), "/");
            } else if (f.isFile() && docBase.endsWith(".war")) {
                mainResourceSet = new WarResourceSet(this, "/", f.getAbsolutePath());
            } else {
                throw new IllegalArgumentException(sm.getString("standardRoot.startInvalidMain", new Object[]{f.getAbsolutePath()}));
            }
        }
        return mainResourceSet;
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.stop();
            }
        }
        if (this.main != null) {
            this.main.destroy();
        }
        this.mainResources.clear();
        for (WebResourceSet webResourceSet : this.jarResources) {
            webResourceSet.destroy();
        }
        this.jarResources.clear();
        for (WebResourceSet webResourceSet : this.classResources) {
            webResourceSet.destroy();
        }
        this.classResources.clear();
        for (TrackedWebResource trackedResource : this.trackedResources) {
            log.error((Object)sm.getString("standardRoot.lockedFile", new Object[]{this.context.getName(), trackedResource.getName()}), (Throwable)trackedResource.getCreatedBy());
            try {
                trackedResource.close();
            }
            catch (IOException iOException) {}
        }
        this.cache.clear();
        this.setState(LifecycleState.STOPPING);
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.destroy();
            }
        }
        this.unregister(this.cacheJmxName);
        super.destroyInternal();
    }

    static class BaseLocation {
        private final String basePath;
        private final String archivePath;

        BaseLocation(URL url) {
            File f = null;
            if ("jar".equals(url.getProtocol()) || "war".equals(url.getProtocol())) {
                String jarUrl = url.toString();
                int endOfFileUrl = -1;
                endOfFileUrl = "jar".equals(url.getProtocol()) ? jarUrl.indexOf("!/") : jarUrl.indexOf(UriUtil.getWarSeparator());
                String fileUrl = jarUrl.substring(4, endOfFileUrl);
                try {
                    f = new File(new URI(fileUrl));
                }
                catch (URISyntaxException e) {
                    throw new IllegalArgumentException(e);
                }
                int startOfArchivePath = endOfFileUrl + 2;
                this.archivePath = jarUrl.length() > startOfArchivePath ? jarUrl.substring(startOfArchivePath) : null;
            } else if ("file".equals(url.getProtocol())) {
                try {
                    f = new File(url.toURI());
                }
                catch (URISyntaxException e) {
                    throw new IllegalArgumentException(e);
                }
                this.archivePath = null;
            } else {
                throw new IllegalArgumentException(sm.getString("standardRoot.unsupportedProtocol", new Object[]{url.getProtocol()}));
            }
            this.basePath = f.getAbsolutePath();
        }

        String getBasePath() {
            return this.basePath;
        }

        String getArchivePath() {
            return this.archivePath;
        }
    }
}

