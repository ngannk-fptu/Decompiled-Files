/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.WebappProperties
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstrumentableClassLoader
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.PermissionCheck
 *  org.apache.tomcat.util.threads.ThreadPoolExecutor
 */
package org.apache.catalina.loader;

import java.beans.Introspector;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.catalina.Container;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.loader.ResourceEntry;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.juli.WebappProperties;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstrumentableClassLoader;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.PermissionCheck;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

public abstract class WebappClassLoaderBase
extends URLClassLoader
implements Lifecycle,
InstrumentableClassLoader,
WebappProperties,
PermissionCheck {
    private static final Log log = LogFactory.getLog(WebappClassLoaderBase.class);
    private static final List<String> JVM_THREAD_GROUP_NAMES = new ArrayList<String>();
    private static final String JVM_THREAD_GROUP_SYSTEM = "system";
    private static final String CLASS_FILE_SUFFIX = ".class";
    protected static final StringManager sm;
    protected WebResourceRoot resources = null;
    protected final Map<String, ResourceEntry> resourceEntries = new ConcurrentHashMap<String, ResourceEntry>();
    protected boolean delegate = false;
    private final Map<String, Long> jarModificationTimes = new HashMap<String, Long>();
    protected final ArrayList<Permission> permissionList = new ArrayList();
    protected final HashMap<String, PermissionCollection> loaderPC = new HashMap();
    protected final SecurityManager securityManager;
    protected final ClassLoader parent;
    private ClassLoader javaseClassLoader;
    private boolean clearReferencesRmiTargets = true;
    private boolean clearReferencesStopThreads = false;
    private boolean clearReferencesStopTimerThreads = false;
    private boolean clearReferencesLogFactoryRelease = true;
    private boolean clearReferencesHttpClientKeepAliveThread = true;
    private boolean clearReferencesObjectStreamClassCaches = true;
    private boolean clearReferencesThreadLocals = true;
    private boolean skipMemoryLeakChecksOnJvmShutdown = false;
    private final List<ClassFileTransformer> transformers = new CopyOnWriteArrayList<ClassFileTransformer>();
    private boolean hasExternalRepositories = false;
    private List<URL> localRepositories = new ArrayList<URL>();
    private volatile LifecycleState state = LifecycleState.NEW;

    protected WebappClassLoaderBase() {
        super(new URL[0]);
        ClassLoader p = this.getParent();
        if (p == null) {
            p = WebappClassLoaderBase.getSystemClassLoader();
        }
        this.parent = p;
        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = WebappClassLoaderBase.getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.javaseClassLoader = j;
        this.securityManager = System.getSecurityManager();
        if (this.securityManager != null) {
            this.refreshPolicy();
        }
    }

    protected WebappClassLoaderBase(ClassLoader parent) {
        super(new URL[0], parent);
        ClassLoader p = this.getParent();
        if (p == null) {
            p = WebappClassLoaderBase.getSystemClassLoader();
        }
        this.parent = p;
        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = WebappClassLoaderBase.getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.javaseClassLoader = j;
        this.securityManager = System.getSecurityManager();
        if (this.securityManager != null) {
            this.refreshPolicy();
        }
    }

    @Deprecated
    public WebResourceRoot getResources() {
        return null;
    }

    public void setResources(WebResourceRoot resources) {
        this.resources = resources;
    }

    public String getContextName() {
        if (this.resources == null) {
            return "Unknown";
        }
        return this.resources.getContext().getBaseName();
    }

    public boolean getDelegate() {
        return this.delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    void addPermission(URL url) {
        if (url == null) {
            return;
        }
        if (this.securityManager != null) {
            String protocol = url.getProtocol();
            if ("file".equalsIgnoreCase(protocol)) {
                String path;
                File f;
                try {
                    URI uri = url.toURI();
                    f = new File(uri);
                    path = f.getCanonicalPath();
                }
                catch (IOException | URISyntaxException e) {
                    log.warn((Object)sm.getString("webappClassLoader.addPermissionNoCanonicalFile", new Object[]{url.toExternalForm()}));
                    return;
                }
                if (f.isFile()) {
                    this.addPermission(new FilePermission(path, "read"));
                } else if (f.isDirectory()) {
                    this.addPermission(new FilePermission(path, "read"));
                    this.addPermission(new FilePermission(path + File.separator + "-", "read"));
                }
            } else {
                log.warn((Object)sm.getString("webappClassLoader.addPermissionNoProtocol", new Object[]{protocol, url.toExternalForm()}));
            }
        }
    }

    void addPermission(Permission permission) {
        if (this.securityManager != null && permission != null) {
            this.permissionList.add(permission);
        }
    }

    public boolean getClearReferencesRmiTargets() {
        return this.clearReferencesRmiTargets;
    }

    public void setClearReferencesRmiTargets(boolean clearReferencesRmiTargets) {
        this.clearReferencesRmiTargets = clearReferencesRmiTargets;
    }

    public boolean getClearReferencesStopThreads() {
        return this.clearReferencesStopThreads;
    }

    public void setClearReferencesStopThreads(boolean clearReferencesStopThreads) {
        this.clearReferencesStopThreads = clearReferencesStopThreads;
    }

    public boolean getClearReferencesStopTimerThreads() {
        return this.clearReferencesStopTimerThreads;
    }

    public void setClearReferencesStopTimerThreads(boolean clearReferencesStopTimerThreads) {
        this.clearReferencesStopTimerThreads = clearReferencesStopTimerThreads;
    }

    public boolean getClearReferencesLogFactoryRelease() {
        return this.clearReferencesLogFactoryRelease;
    }

    public void setClearReferencesLogFactoryRelease(boolean clearReferencesLogFactoryRelease) {
        this.clearReferencesLogFactoryRelease = clearReferencesLogFactoryRelease;
    }

    public boolean getClearReferencesHttpClientKeepAliveThread() {
        return this.clearReferencesHttpClientKeepAliveThread;
    }

    public void setClearReferencesHttpClientKeepAliveThread(boolean clearReferencesHttpClientKeepAliveThread) {
        this.clearReferencesHttpClientKeepAliveThread = clearReferencesHttpClientKeepAliveThread;
    }

    public boolean getClearReferencesObjectStreamClassCaches() {
        return this.clearReferencesObjectStreamClassCaches;
    }

    public void setClearReferencesObjectStreamClassCaches(boolean clearReferencesObjectStreamClassCaches) {
        this.clearReferencesObjectStreamClassCaches = clearReferencesObjectStreamClassCaches;
    }

    public boolean getClearReferencesThreadLocals() {
        return this.clearReferencesThreadLocals;
    }

    public void setClearReferencesThreadLocals(boolean clearReferencesThreadLocals) {
        this.clearReferencesThreadLocals = clearReferencesThreadLocals;
    }

    public boolean getSkipMemoryLeakChecksOnJvmShutdown() {
        return this.skipMemoryLeakChecksOnJvmShutdown;
    }

    public void setSkipMemoryLeakChecksOnJvmShutdown(boolean skipMemoryLeakChecksOnJvmShutdown) {
        this.skipMemoryLeakChecksOnJvmShutdown = skipMemoryLeakChecksOnJvmShutdown;
    }

    public void addTransformer(ClassFileTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException(sm.getString("webappClassLoader.addTransformer.illegalArgument", new Object[]{this.getContextName()}));
        }
        if (this.transformers.contains(transformer)) {
            log.warn((Object)sm.getString("webappClassLoader.addTransformer.duplicate", new Object[]{transformer, this.getContextName()}));
            return;
        }
        this.transformers.add(transformer);
        log.info((Object)sm.getString("webappClassLoader.addTransformer", new Object[]{transformer, this.getContextName()}));
    }

    public void removeTransformer(ClassFileTransformer transformer) {
        if (transformer == null) {
            return;
        }
        if (this.transformers.remove(transformer)) {
            log.info((Object)sm.getString("webappClassLoader.removeTransformer", new Object[]{transformer, this.getContextName()}));
        }
    }

    protected void copyStateWithoutTransformers(WebappClassLoaderBase base) {
        base.resources = this.resources;
        base.delegate = this.delegate;
        base.state = LifecycleState.NEW;
        base.clearReferencesStopThreads = this.clearReferencesStopThreads;
        base.clearReferencesStopTimerThreads = this.clearReferencesStopTimerThreads;
        base.clearReferencesLogFactoryRelease = this.clearReferencesLogFactoryRelease;
        base.clearReferencesHttpClientKeepAliveThread = this.clearReferencesHttpClientKeepAliveThread;
        base.jarModificationTimes.putAll(this.jarModificationTimes);
        base.permissionList.addAll(this.permissionList);
        base.loaderPC.putAll(this.loaderPC);
    }

    public boolean modified() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"modified()");
        }
        for (Map.Entry<String, ResourceEntry> entry : this.resourceEntries.entrySet()) {
            long cachedLastModified = entry.getValue().lastModified;
            long lastModified = this.resources.getClassLoaderResource(entry.getKey()).getLastModified();
            if (lastModified == cachedLastModified) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("webappClassLoader.resourceModified", new Object[]{entry.getKey(), new Date(cachedLastModified), new Date(lastModified)}));
            }
            return true;
        }
        WebResource[] jars = this.resources.listResources("/WEB-INF/lib");
        int jarCount = 0;
        for (WebResource jar : jars) {
            if (!jar.getName().endsWith(".jar") || !jar.isFile() || !jar.canRead()) continue;
            ++jarCount;
            Long recordedLastModified = this.jarModificationTimes.get(jar.getName());
            if (recordedLastModified == null) {
                log.info((Object)sm.getString("webappClassLoader.jarsAdded", new Object[]{this.resources.getContext().getName()}));
                return true;
            }
            if (recordedLastModified.longValue() == jar.getLastModified()) continue;
            log.info((Object)sm.getString("webappClassLoader.jarsModified", new Object[]{this.resources.getContext().getName()}));
            return true;
        }
        if (jarCount < this.jarModificationTimes.size()) {
            log.info((Object)sm.getString("webappClassLoader.jarsRemoved", new Object[]{this.resources.getContext().getName()}));
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append("\r\n  context: ");
        sb.append(this.getContextName());
        sb.append("\r\n  delegate: ");
        sb.append(this.delegate);
        sb.append("\r\n");
        if (this.parent != null) {
            sb.append("----------> Parent Classloader:\r\n");
            sb.append(this.parent.toString());
            sb.append("\r\n");
        }
        if (this.transformers.size() > 0) {
            sb.append("----------> Class file transformers:\r\n");
            for (ClassFileTransformer transformer : this.transformers) {
                sb.append(transformer).append("\r\n");
            }
        }
        return sb.toString();
    }

    protected final Class<?> doDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectionDomain) {
        return super.defineClass(name, b, off, len, protectionDomain);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        int i;
        if (log.isDebugEnabled()) {
            log.debug((Object)("    findClass(" + name + ")"));
        }
        this.checkStateForClassLoading(name);
        if (this.securityManager != null && (i = name.lastIndexOf(46)) >= 0) {
            try {
                if (log.isTraceEnabled()) {
                    log.trace((Object)"      securityManager.checkPackageDefinition");
                }
                this.securityManager.checkPackageDefinition(name.substring(0, i));
            }
            catch (Exception se) {
                if (log.isTraceEnabled()) {
                    log.trace((Object)"      -->Exception-->ClassNotFoundException", (Throwable)se);
                }
                throw new ClassNotFoundException(name, se);
            }
        }
        Class<?> clazz = null;
        try {
            if (log.isTraceEnabled()) {
                log.trace((Object)("      findClassInternal(" + name + ")"));
            }
            try {
                if (this.securityManager != null) {
                    PrivilegedFindClassByName dp = new PrivilegedFindClassByName(name);
                    clazz = (Class<?>)AccessController.doPrivileged(dp);
                } else {
                    clazz = this.findClassInternal(name);
                }
            }
            catch (AccessControlException ace) {
                log.warn((Object)sm.getString("webappClassLoader.securityException", new Object[]{name, ace.getMessage()}), (Throwable)ace);
                throw new ClassNotFoundException(name, ace);
            }
            catch (RuntimeException e) {
                if (log.isTraceEnabled()) {
                    log.trace((Object)"      -->RuntimeException Rethrown", (Throwable)e);
                }
                throw e;
            }
            if (clazz == null && this.hasExternalRepositories) {
                try {
                    clazz = super.findClass(name);
                }
                catch (AccessControlException ace) {
                    log.warn((Object)sm.getString("webappClassLoader.securityException", new Object[]{name, ace.getMessage()}), (Throwable)ace);
                    throw new ClassNotFoundException(name, ace);
                }
                catch (RuntimeException e) {
                    if (log.isTraceEnabled()) {
                        log.trace((Object)"      -->RuntimeException Rethrown", (Throwable)e);
                    }
                    throw e;
                }
            }
            if (clazz == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"    --> Returning ClassNotFoundException");
                }
                throw new ClassNotFoundException(name);
            }
        }
        catch (ClassNotFoundException e) {
            if (log.isTraceEnabled()) {
                log.trace((Object)"    --> Passing on ClassNotFoundException");
            }
            throw e;
        }
        if (log.isTraceEnabled()) {
            log.debug((Object)("      Returning class " + clazz));
        }
        if (log.isTraceEnabled()) {
            ClassLoader cl = Globals.IS_SECURITY_ENABLED ? AccessController.doPrivileged(new PrivilegedGetClassLoader(clazz)) : clazz.getClassLoader();
            log.debug((Object)("      Loaded by " + cl.toString()));
        }
        return clazz;
    }

    @Override
    public URL findResource(String name) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("    findResource(" + name + ")"));
        }
        this.checkStateForResourceLoading(name);
        URL url = null;
        String path = this.nameToPath(name);
        WebResource resource = this.resources.getClassLoaderResource(path);
        if (resource.exists()) {
            url = resource.getURL();
            this.trackLastModified(path, resource);
        }
        if (url == null && this.hasExternalRepositories) {
            url = super.findResource(name);
        }
        if (log.isDebugEnabled()) {
            if (url != null) {
                log.debug((Object)("    --> Returning '" + url.toString() + "'"));
            } else {
                log.debug((Object)"    --> Resource not found, returning null");
            }
        }
        return url;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void trackLastModified(String path, WebResource resource) {
        if (this.resourceEntries.containsKey(path)) {
            return;
        }
        ResourceEntry entry = new ResourceEntry();
        entry.lastModified = resource.getLastModified();
        Map<String, ResourceEntry> map = this.resourceEntries;
        synchronized (map) {
            this.resourceEntries.putIfAbsent(path, entry);
        }
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        WebResource[] webResources;
        if (log.isDebugEnabled()) {
            log.debug((Object)("    findResources(" + name + ")"));
        }
        this.checkStateForResourceLoading(name);
        LinkedHashSet<URL> result = new LinkedHashSet<URL>();
        String path = this.nameToPath(name);
        for (WebResource webResource : webResources = this.resources.getClassLoaderResources(path)) {
            if (!webResource.exists()) continue;
            result.add(webResource.getURL());
        }
        if (this.hasExternalRepositories) {
            Enumeration<URL> otherResourcePaths = super.findResources(name);
            while (otherResourcePaths.hasMoreElements()) {
                result.add(otherResourcePaths.nextElement());
            }
        }
        return Collections.enumeration(result);
    }

    @Override
    public URL getResource(String name) {
        boolean delegateFirst;
        if (log.isDebugEnabled()) {
            log.debug((Object)("getResource(" + name + ")"));
        }
        this.checkStateForResourceLoading(name);
        URL url = null;
        boolean bl = delegateFirst = this.delegate || this.filter(name, false);
        if (delegateFirst) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Delegating to parent classloader " + this.parent));
            }
            if ((url = this.parent.getResource(name)) != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("  --> Returning '" + url.toString() + "'"));
                }
                return url;
            }
        }
        if ((url = this.findResource(name)) != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  --> Returning '" + url.toString() + "'"));
            }
            return url;
        }
        if (!delegateFirst && (url = this.parent.getResource(name)) != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  --> Returning '" + url.toString() + "'"));
            }
            return url;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"  --> Resource not found, returning null");
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        boolean delegateFirst;
        Enumeration<URL> parentResources = this.parent.getResources(name);
        Enumeration<URL> localResources = this.findResources(name);
        boolean bl = delegateFirst = this.delegate || this.filter(name, false);
        if (delegateFirst) {
            return new CombinedEnumeration(parentResources, localResources);
        }
        return new CombinedEnumeration(localResources, parentResources);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        String path;
        WebResource resource;
        boolean delegateFirst;
        if (log.isDebugEnabled()) {
            log.debug((Object)("getResourceAsStream(" + name + ")"));
        }
        this.checkStateForResourceLoading(name);
        InputStream stream = null;
        boolean bl = delegateFirst = this.delegate || this.filter(name, false);
        if (delegateFirst) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Delegating to parent classloader " + this.parent));
            }
            if ((stream = this.parent.getResourceAsStream(name)) != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"  --> Returning stream from parent");
                }
                return stream;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"  Searching local repositories");
        }
        if ((resource = this.resources.getClassLoaderResource(path = this.nameToPath(name))).exists()) {
            stream = resource.getInputStream();
            this.trackLastModified(path, resource);
        }
        try {
            URL url;
            if (this.hasExternalRepositories && stream == null && (url = super.findResource(name)) != null) {
                stream = url.openStream();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (stream != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"  --> Returning stream from local");
            }
            return stream;
        }
        if (!delegateFirst) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Delegating to parent classloader unconditionally " + this.parent));
            }
            if ((stream = this.parent.getResourceAsStream(name)) != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"  --> Returning stream from parent");
                }
                return stream;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"  --> Resource not found, returning null");
        }
        return null;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        WebappClassLoaderBase webappClassLoaderBase = JreCompat.isGraalAvailable() ? this : this.getClassLoadingLock(name);
        synchronized (webappClassLoaderBase) {
            boolean delegateLoad;
            int i;
            boolean tryLoadingFromJavaseLoader;
            if (log.isDebugEnabled()) {
                log.debug((Object)("loadClass(" + name + ", " + resolve + ")"));
            }
            Class<?> clazz = null;
            this.checkStateForClassLoading(name);
            clazz = this.findLoadedClass0(name);
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"  Returning class from cache");
                }
                if (resolve) {
                    this.resolveClass(clazz);
                }
                return clazz;
            }
            Class<?> clazz2 = clazz = JreCompat.isGraalAvailable() ? null : this.findLoadedClass(name);
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"  Returning class from cache");
                }
                if (resolve) {
                    this.resolveClass(clazz);
                }
                return clazz;
            }
            String resourceName = this.binaryNameToPath(name, false);
            ClassLoader javaseLoader = this.getJavaseClassLoader();
            try {
                URL url;
                if (this.securityManager != null) {
                    PrivilegedJavaseGetResource dp = new PrivilegedJavaseGetResource(resourceName);
                    url = AccessController.doPrivileged(dp);
                } else {
                    url = javaseLoader.getResource(resourceName);
                }
                tryLoadingFromJavaseLoader = url != null;
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                tryLoadingFromJavaseLoader = true;
            }
            if (tryLoadingFromJavaseLoader) {
                try {
                    clazz = javaseLoader.loadClass(name);
                    if (clazz != null) {
                        if (resolve) {
                            this.resolveClass(clazz);
                        }
                        return clazz;
                    }
                }
                catch (ClassNotFoundException t) {
                    // empty catch block
                }
            }
            if (this.securityManager != null && (i = name.lastIndexOf(46)) >= 0) {
                try {
                    this.securityManager.checkPackageAccess(name.substring(0, i));
                }
                catch (SecurityException se) {
                    String error = sm.getString("webappClassLoader.restrictedPackage", new Object[]{name});
                    log.info((Object)error, (Throwable)se);
                    throw new ClassNotFoundException(error, se);
                }
            }
            boolean bl = delegateLoad = this.delegate || this.filter(name, true);
            if (delegateLoad) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("  Delegating to parent classloader1 " + this.parent));
                }
                try {
                    clazz = Class.forName(name, false, this.parent);
                    if (clazz != null) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"  Loading class from parent");
                        }
                        if (resolve) {
                            this.resolveClass(clazz);
                        }
                        return clazz;
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)"  Searching local repositories");
            }
            try {
                clazz = this.findClass(name);
                if (clazz != null) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"  Loading class from local repository");
                    }
                    if (resolve) {
                        this.resolveClass(clazz);
                    }
                    return clazz;
                }
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
            if (!delegateLoad) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("  Delegating to parent classloader at end: " + this.parent));
                }
                try {
                    clazz = Class.forName(name, false, this.parent);
                    if (clazz != null) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"  Loading class from parent");
                        }
                        if (resolve) {
                            this.resolveClass(clazz);
                        }
                        return clazz;
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
        }
        throw new ClassNotFoundException(name);
    }

    protected void checkStateForClassLoading(String className) throws ClassNotFoundException {
        try {
            this.checkStateForResourceLoading(className);
        }
        catch (IllegalStateException ise) {
            throw new ClassNotFoundException(ise.getMessage(), ise);
        }
    }

    protected void checkStateForResourceLoading(String resource) throws IllegalStateException {
        if (!this.state.isAvailable()) {
            String msg = sm.getString("webappClassLoader.stopped", new Object[]{resource});
            IllegalStateException ise = new IllegalStateException(msg);
            log.info((Object)msg, (Throwable)ise);
            throw ise;
        }
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codeSource) {
        String codeUrl = codeSource.getLocation().toString();
        PermissionCollection pc = this.loaderPC.get(codeUrl);
        if (pc == null && (pc = super.getPermissions(codeSource)) != null) {
            for (Permission p : this.permissionList) {
                pc.add(p);
            }
            this.loaderPC.put(codeUrl, pc);
        }
        return pc;
    }

    public boolean check(Permission permission) {
        URL contextRootUrl;
        CodeSource cs;
        PermissionCollection pc;
        if (!Globals.IS_SECURITY_ENABLED) {
            return true;
        }
        Policy currentPolicy = Policy.getPolicy();
        return currentPolicy != null && (pc = currentPolicy.getPermissions(cs = new CodeSource(contextRootUrl = this.resources.getResource("/").getCodeBase(), (Certificate[])null))).implies(permission);
    }

    @Override
    public URL[] getURLs() {
        ArrayList<URL> result = new ArrayList<URL>();
        result.addAll(this.localRepositories);
        result.addAll(Arrays.asList(super.getURLs()));
        return result.toArray(new URL[0]);
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
    }

    @Override
    public LifecycleState getState() {
        return this.state;
    }

    @Override
    public String getStateName() {
        return this.getState().toString();
    }

    @Override
    public void init() {
        this.state = LifecycleState.INITIALIZED;
    }

    @Override
    public void start() throws LifecycleException {
        WebResource[] jars;
        WebResource[] classesResources;
        this.state = LifecycleState.STARTING_PREP;
        for (WebResource classes : classesResources = this.resources.getResources("/WEB-INF/classes")) {
            if (!classes.isDirectory() || !classes.canRead()) continue;
            this.localRepositories.add(classes.getURL());
        }
        for (WebResource jar : jars = this.resources.listResources("/WEB-INF/lib")) {
            if (!jar.getName().endsWith(".jar") || !jar.isFile() || !jar.canRead()) continue;
            this.localRepositories.add(jar.getURL());
            this.jarModificationTimes.put(jar.getName(), jar.getLastModified());
        }
        this.state = LifecycleState.STARTED;
    }

    @Override
    public void stop() throws LifecycleException {
        this.state = LifecycleState.STOPPING_PREP;
        this.clearReferences();
        this.state = LifecycleState.STOPPING;
        this.resourceEntries.clear();
        this.jarModificationTimes.clear();
        this.resources = null;
        this.permissionList.clear();
        this.loaderPC.clear();
        this.state = LifecycleState.STOPPED;
    }

    @Override
    public void destroy() {
        this.state = LifecycleState.DESTROYING;
        try {
            super.close();
        }
        catch (IOException ioe) {
            log.warn((Object)sm.getString("webappClassLoader.superCloseFail"), (Throwable)ioe);
        }
        this.state = LifecycleState.DESTROYED;
    }

    protected ClassLoader getJavaseClassLoader() {
        return this.javaseClassLoader;
    }

    protected void setJavaseClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException(sm.getString("webappClassLoader.javaseClassLoaderNull"));
        }
        this.javaseClassLoader = classLoader;
    }

    protected void clearReferences() {
        if (this.skipMemoryLeakChecksOnJvmShutdown && !this.resources.getContext().getParent().getState().isAvailable()) {
            try {
                Thread dummyHook = new Thread();
                Runtime.getRuntime().addShutdownHook(dummyHook);
                Runtime.getRuntime().removeShutdownHook(dummyHook);
            }
            catch (IllegalStateException ise) {
                return;
            }
        }
        if (!JreCompat.isGraalAvailable()) {
            this.clearReferencesJdbc();
        }
        this.clearReferencesThreads();
        if (this.clearReferencesObjectStreamClassCaches && !JreCompat.isGraalAvailable()) {
            this.clearReferencesObjectStreamClassCaches();
        }
        if (this.clearReferencesThreadLocals && !JreCompat.isGraalAvailable()) {
            this.checkThreadLocalsForLeaks();
        }
        if (this.clearReferencesRmiTargets) {
            this.clearReferencesRmiTargets();
        }
        IntrospectionUtils.clear();
        if (this.clearReferencesLogFactoryRelease) {
            LogFactory.release((ClassLoader)this);
        }
        Introspector.flushCaches();
        TomcatURLStreamHandlerFactory.release(this);
    }

    private void clearReferencesJdbc() {
        byte[] classBytes = new byte[2048];
        int offset = 0;
        try (InputStream is = this.getResourceAsStream("org/apache/catalina/loader/JdbcLeakPrevention.class");){
            int read = is.read(classBytes, offset, classBytes.length - offset);
            while (read > -1) {
                if ((offset += read) == classBytes.length) {
                    byte[] tmp = new byte[classBytes.length * 2];
                    System.arraycopy(classBytes, 0, tmp, 0, classBytes.length);
                    classBytes = tmp;
                }
                read = is.read(classBytes, offset, classBytes.length - offset);
            }
            Class<?> lpClass = this.defineClass("org.apache.catalina.loader.JdbcLeakPrevention", classBytes, 0, offset, this.getClass().getProtectionDomain());
            Object obj = lpClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            List driverNames = (List)obj.getClass().getMethod("clearJdbcDriverRegistrations", new Class[0]).invoke(obj, new Object[0]);
            for (String name : driverNames) {
                log.warn((Object)sm.getString("webappClassLoader.clearJdbc", new Object[]{this.getContextName(), name}));
            }
        }
        catch (Exception e) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
            ExceptionUtils.handleThrowable((Throwable)t);
            log.warn((Object)sm.getString("webappClassLoader.jdbcRemoveFailed", new Object[]{this.getContextName()}), t);
        }
    }

    private void clearReferencesThreads() {
        Thread[] threads = this.getThreads();
        ArrayList<Thread> threadsToStop = new ArrayList<Thread>();
        for (Thread thread : threads) {
            ClassLoader ccl;
            if (thread == null || (ccl = thread.getContextClassLoader()) != this || thread == Thread.currentThread()) continue;
            String threadName = thread.getName();
            ThreadGroup tg = thread.getThreadGroup();
            if (tg != null && JVM_THREAD_GROUP_NAMES.contains(tg.getName())) {
                if (!this.clearReferencesHttpClientKeepAliveThread || !threadName.equals("Keep-Alive-Timer")) continue;
                thread.setContextClassLoader(this.parent);
                log.debug((Object)sm.getString("webappClassLoader.checkThreadsHttpClient"));
                continue;
            }
            if (!thread.isAlive()) continue;
            if (thread.getClass().getName().startsWith("java.util.Timer") && this.clearReferencesStopTimerThreads) {
                this.clearReferencesStopTimerThread(thread);
                continue;
            }
            if (this.isRequestThread(thread)) {
                log.warn((Object)sm.getString("webappClassLoader.stackTraceRequestThread", new Object[]{this.getContextName(), threadName, this.getStackTrace(thread)}));
            } else {
                log.warn((Object)sm.getString("webappClassLoader.stackTrace", new Object[]{this.getContextName(), threadName, this.getStackTrace(thread)}));
            }
            if (!this.clearReferencesStopThreads) continue;
            boolean usingExecutor = false;
            try {
                Object executor = JreCompat.getInstance().getExecutor(thread);
                if (executor instanceof ThreadPoolExecutor) {
                    ((ThreadPoolExecutor)executor).shutdownNow();
                    usingExecutor = true;
                } else if (executor instanceof java.util.concurrent.ThreadPoolExecutor) {
                    ((java.util.concurrent.ThreadPoolExecutor)executor).shutdownNow();
                    usingExecutor = true;
                }
            }
            catch (IllegalAccessException | NoSuchFieldException | RuntimeException e) {
                log.warn((Object)sm.getString("webappClassLoader.stopThreadFail", new Object[]{thread.getName(), this.getContextName()}), (Throwable)e);
            }
            if (!usingExecutor && !thread.isInterrupted()) {
                thread.interrupt();
            }
            threadsToStop.add(thread);
        }
        int count = 0;
        for (Thread t : threadsToStop) {
            while (t.isAlive() && count < 100) {
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException e) {
                    break;
                }
                ++count;
            }
            if (!t.isAlive()) continue;
            t.stop();
        }
    }

    private boolean isRequestThread(Thread thread) {
        StackTraceElement[] elements = thread.getStackTrace();
        if (elements == null || elements.length == 0) {
            return false;
        }
        for (int i = 0; i < elements.length; ++i) {
            StackTraceElement element = elements[elements.length - (i + 1)];
            if (!"org.apache.catalina.connector.CoyoteAdapter".equals(element.getClassName())) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearReferencesStopTimerThread(Thread thread) {
        try {
            try {
                Field newTasksMayBeScheduledField = thread.getClass().getDeclaredField("newTasksMayBeScheduled");
                newTasksMayBeScheduledField.setAccessible(true);
                Field queueField = thread.getClass().getDeclaredField("queue");
                queueField.setAccessible(true);
                Object queue = queueField.get(thread);
                Method clearMethod = queue.getClass().getDeclaredMethod("clear", new Class[0]);
                clearMethod.setAccessible(true);
                Object object = queue;
                synchronized (object) {
                    newTasksMayBeScheduledField.setBoolean(thread, false);
                    clearMethod.invoke(queue, new Object[0]);
                    queue.notifyAll();
                }
            }
            catch (NoSuchFieldException nfe) {
                Method cancelMethod = thread.getClass().getDeclaredMethod("cancel", new Class[0]);
                Thread thread2 = thread;
                synchronized (thread2) {
                    cancelMethod.setAccessible(true);
                    cancelMethod.invoke((Object)thread, new Object[0]);
                }
            }
            log.warn((Object)sm.getString("webappClassLoader.warnTimerThread", new Object[]{this.getContextName(), thread.getName()}));
        }
        catch (Exception e) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
            ExceptionUtils.handleThrowable((Throwable)t);
            log.warn((Object)sm.getString("webappClassLoader.stopTimerThreadFail", new Object[]{thread.getName(), this.getContextName()}), t);
        }
    }

    private void checkThreadLocalsForLeaks() {
        Thread[] threads = this.getThreads();
        try {
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            Class<?> tlmClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = tlmClass.getDeclaredField("table");
            tableField.setAccessible(true);
            Method expungeStaleEntriesMethod = tlmClass.getDeclaredMethod("expungeStaleEntries", new Class[0]);
            expungeStaleEntriesMethod.setAccessible(true);
            for (Thread thread : threads) {
                if (thread == null) continue;
                Object threadLocalMap = threadLocalsField.get(thread);
                if (null != threadLocalMap) {
                    expungeStaleEntriesMethod.invoke(threadLocalMap, new Object[0]);
                    this.checkThreadLocalMapForLeaks(threadLocalMap, tableField);
                }
                if (null == (threadLocalMap = inheritableThreadLocalsField.get(thread))) continue;
                expungeStaleEntriesMethod.invoke(threadLocalMap, new Object[0]);
                this.checkThreadLocalMapForLeaks(threadLocalMap, tableField);
            }
        }
        catch (Throwable t) {
            JreCompat jreCompat = JreCompat.getInstance();
            if (jreCompat.isInstanceOfInaccessibleObjectException(t)) {
                String currentModule = JreCompat.getInstance().getModuleName(this.getClass());
                log.warn((Object)sm.getString("webappClassLoader.addExportsThreadLocal", new Object[]{currentModule}));
            }
            ExceptionUtils.handleThrowable((Throwable)t);
            log.warn((Object)sm.getString("webappClassLoader.checkThreadLocalsForLeaksFail", new Object[]{this.getContextName()}), t);
        }
    }

    private void checkThreadLocalMapForLeaks(Object map, Field internalTableField) throws IllegalAccessException, NoSuchFieldException {
        Object[] table;
        if (map != null && (table = (Object[])internalTableField.get(map)) != null) {
            for (Object obj : table) {
                if (obj == null) continue;
                boolean keyLoadedByWebapp = false;
                boolean valueLoadedByWebapp = false;
                Object key = ((Reference)obj).get();
                if (this.equals(key) || this.loadedByThisOrChild(key)) {
                    keyLoadedByWebapp = true;
                }
                Field valueField = obj.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                Object value = valueField.get(obj);
                if (this.equals(value) || this.loadedByThisOrChild(value)) {
                    valueLoadedByWebapp = true;
                }
                if (!keyLoadedByWebapp && !valueLoadedByWebapp) continue;
                Object[] args = new Object[5];
                args[0] = this.getContextName();
                if (key != null) {
                    args[1] = this.getPrettyClassName(key.getClass());
                    try {
                        args[2] = key.toString();
                    }
                    catch (Exception e) {
                        log.warn((Object)sm.getString("webappClassLoader.checkThreadLocalsForLeaks.badKey", new Object[]{args[1]}), (Throwable)e);
                        args[2] = sm.getString("webappClassLoader.checkThreadLocalsForLeaks.unknown");
                    }
                }
                if (value != null) {
                    args[3] = this.getPrettyClassName(value.getClass());
                    try {
                        args[4] = value.toString();
                    }
                    catch (Exception e) {
                        log.warn((Object)sm.getString("webappClassLoader.checkThreadLocalsForLeaks.badValue", new Object[]{args[3]}), (Throwable)e);
                        args[4] = sm.getString("webappClassLoader.checkThreadLocalsForLeaks.unknown");
                    }
                }
                if (valueLoadedByWebapp) {
                    log.error((Object)sm.getString("webappClassLoader.checkThreadLocalsForLeaks", args));
                    continue;
                }
                if (value == null) {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)sm.getString("webappClassLoader.checkThreadLocalsForLeaksNull", args));
                    continue;
                }
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("webappClassLoader.checkThreadLocalsForLeaksNone", args));
            }
        }
    }

    private String getPrettyClassName(Class<?> clazz) {
        String name = clazz.getCanonicalName();
        if (name == null) {
            name = clazz.getName();
        }
        return name;
    }

    private String getStackTrace(Thread thread) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement ste : thread.getStackTrace()) {
            builder.append("\n ").append(ste);
        }
        return builder.toString();
    }

    private boolean loadedByThisOrChild(Object o) {
        if (o == null) {
            return false;
        }
        Class<?> clazz = o instanceof Class ? (Class<?>)o : o.getClass();
        for (ClassLoader cl = clazz.getClassLoader(); cl != null; cl = cl.getParent()) {
            if (cl != this) continue;
            return true;
        }
        if (o instanceof Collection) {
            try {
                for (Object entry : (Collection)o) {
                    if (!this.loadedByThisOrChild(entry)) continue;
                    return true;
                }
            }
            catch (ConcurrentModificationException e) {
                log.warn((Object)sm.getString("webappClassLoader.loadedByThisOrChildFail", new Object[]{clazz.getName(), this.getContextName()}), (Throwable)e);
            }
        }
        return false;
    }

    private Thread[] getThreads() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        try {
            while (tg.getParent() != null) {
                tg = tg.getParent();
            }
        }
        catch (SecurityException se) {
            String msg = sm.getString("webappClassLoader.getThreadGroupError", new Object[]{tg.getName()});
            if (log.isDebugEnabled()) {
                log.debug((Object)msg, (Throwable)se);
            }
            log.warn((Object)msg);
        }
        int threadCountGuess = tg.activeCount() + 50;
        Thread[] threads = new Thread[threadCountGuess];
        int threadCountActual = tg.enumerate(threads);
        while (threadCountActual == threadCountGuess) {
            threads = new Thread[threadCountGuess *= 2];
            threadCountActual = tg.enumerate(threads);
        }
        return threads;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearReferencesRmiTargets() {
        try {
            Object tableLock;
            Class<?> objectTargetClass = Class.forName("sun.rmi.transport.Target");
            Field cclField = objectTargetClass.getDeclaredField("ccl");
            cclField.setAccessible(true);
            Field stubField = objectTargetClass.getDeclaredField("stub");
            stubField.setAccessible(true);
            Class<?> objectTableClass = Class.forName("sun.rmi.transport.ObjectTable");
            Field objTableField = objectTableClass.getDeclaredField("objTable");
            objTableField.setAccessible(true);
            Object objTable = objTableField.get(null);
            if (objTable == null) {
                return;
            }
            Field tableLockField = objectTableClass.getDeclaredField("tableLock");
            tableLockField.setAccessible(true);
            Object object = tableLock = tableLockField.get(null);
            synchronized (object) {
                if (objTable instanceof Map) {
                    Iterator iter = ((Map)objTable).values().iterator();
                    while (iter.hasNext()) {
                        Object obj = iter.next();
                        Object cclObject = cclField.get(obj);
                        if (this != cclObject) continue;
                        iter.remove();
                        Object stubObject = stubField.get(obj);
                        log.error((Object)sm.getString("webappClassLoader.clearRmi", new Object[]{stubObject.getClass().getName(), stubObject}));
                    }
                }
                Field implTableField = objectTableClass.getDeclaredField("implTable");
                implTableField.setAccessible(true);
                Object implTable = implTableField.get(null);
                if (implTable == null) {
                    return;
                }
                if (implTable instanceof Map) {
                    Iterator iter = ((Map)implTable).values().iterator();
                    while (iter.hasNext()) {
                        Object obj = iter.next();
                        Object cclObject = cclField.get(obj);
                        if (this != cclObject) continue;
                        iter.remove();
                    }
                }
            }
        }
        catch (ClassNotFoundException e) {
            log.info((Object)sm.getString("webappClassLoader.clearRmiInfo", new Object[]{this.getContextName()}), (Throwable)e);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            log.warn((Object)sm.getString("webappClassLoader.clearRmiFail", new Object[]{this.getContextName()}), (Throwable)e);
        }
        catch (Exception e) {
            JreCompat jreCompat = JreCompat.getInstance();
            if (jreCompat.isInstanceOfInaccessibleObjectException((Throwable)e)) {
                String currentModule = JreCompat.getInstance().getModuleName(this.getClass());
                log.warn((Object)sm.getString("webappClassLoader.addExportsRmi", new Object[]{currentModule}));
            }
            throw e;
        }
    }

    private void clearReferencesObjectStreamClassCaches() {
        if (JreCompat.isJre19Available()) {
            return;
        }
        try {
            Class<?> clazz = Class.forName("java.io.ObjectStreamClass$Caches");
            this.clearCache(clazz, "localDescs");
            this.clearCache(clazz, "reflectors");
        }
        catch (ClassCastException | ReflectiveOperationException | SecurityException e) {
            log.warn((Object)sm.getString("webappClassLoader.clearObjectStreamClassCachesFail", new Object[]{this.getContextName()}), (Throwable)e);
        }
        catch (Exception e) {
            JreCompat jreCompat = JreCompat.getInstance();
            if (jreCompat.isInstanceOfInaccessibleObjectException((Throwable)e)) {
                String currentModule = JreCompat.getInstance().getModuleName(this.getClass());
                log.warn((Object)sm.getString("webappClassLoader.addExportsJavaIo", new Object[]{currentModule}));
                return;
            }
            throw e;
        }
    }

    private void clearCache(Class<?> target, String mapName) throws ReflectiveOperationException, SecurityException, ClassCastException {
        Field f = target.getDeclaredField(mapName);
        f.setAccessible(true);
        Object map = f.get(null);
        if (map instanceof Map) {
            Iterator keys = ((Map)map).keySet().iterator();
            while (keys.hasNext()) {
                Object clazz;
                Object key = keys.next();
                if (!(key instanceof Reference) || !this.loadedByThisOrChild(clazz = ((Reference)key).get())) continue;
                keys.remove();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Class<?> findClassInternal(String name) {
        Class<?> clazz;
        this.checkStateForResourceLoading(name);
        if (name == null) {
            return null;
        }
        String path = this.binaryNameToPath(name, true);
        ResourceEntry entry = this.resourceEntries.get(path);
        WebResource resource = null;
        if (entry == null) {
            resource = this.resources.getClassLoaderResource(path);
            if (!resource.exists()) {
                return null;
            }
            entry = new ResourceEntry();
            entry.lastModified = resource.getLastModified();
            Map<String, ResourceEntry> map = this.resourceEntries;
            synchronized (map) {
                ResourceEntry entry2 = this.resourceEntries.get(path);
                if (entry2 == null) {
                    this.resourceEntries.put(path, entry);
                } else {
                    entry = entry2;
                }
            }
        }
        if ((clazz = entry.loadedClass) != null) {
            return clazz;
        }
        WebappClassLoaderBase webappClassLoaderBase = JreCompat.isGraalAvailable() ? this : this.getClassLoadingLock(name);
        synchronized (webappClassLoaderBase) {
            clazz = entry.loadedClass;
            if (clazz != null) {
                return clazz;
            }
            if (resource == null) {
                resource = this.resources.getClassLoaderResource(path);
            }
            if (!resource.exists()) {
                return null;
            }
            byte[] binaryContent = resource.getContent();
            if (binaryContent == null) {
                return null;
            }
            Manifest manifest = resource.getManifest();
            URL codeBase = resource.getCodeBase();
            Certificate[] certificates = resource.getCertificates();
            if (this.transformers.size() > 0) {
                String internalName = path.substring(1, path.length() - CLASS_FILE_SUFFIX.length());
                for (ClassFileTransformer transformer : this.transformers) {
                    try {
                        byte[] transformed = transformer.transform(this, internalName, null, null, binaryContent);
                        if (transformed == null) continue;
                        binaryContent = transformed;
                    }
                    catch (IllegalClassFormatException e) {
                        log.error((Object)sm.getString("webappClassLoader.transformError", new Object[]{name}), (Throwable)e);
                        return null;
                    }
                }
            }
            String packageName = null;
            int pos = name.lastIndexOf(46);
            if (pos != -1) {
                packageName = name.substring(0, pos);
            }
            Package pkg = null;
            if (packageName != null && (pkg = this.getPackage(packageName)) == null) {
                try {
                    if (manifest == null) {
                        this.definePackage(packageName, null, null, null, null, null, null, null);
                    } else {
                        this.definePackage(packageName, manifest, codeBase);
                    }
                }
                catch (IllegalArgumentException e) {
                    // empty catch block
                }
                pkg = this.getPackage(packageName);
            }
            if (this.securityManager != null && pkg != null) {
                boolean sealCheck = true;
                if (pkg.isSealed()) {
                    sealCheck = pkg.isSealed(codeBase);
                } else {
                    boolean bl = sealCheck = manifest == null || !this.isPackageSealed(packageName, manifest);
                }
                if (!sealCheck) {
                    throw new SecurityException("Sealing violation loading " + name + " : Package " + packageName + " is sealed.");
                }
            }
            try {
                clazz = this.defineClass(name, binaryContent, 0, binaryContent.length, new CodeSource(codeBase, certificates));
            }
            catch (UnsupportedClassVersionError ucve) {
                throw new UnsupportedClassVersionError(ucve.getLocalizedMessage() + " " + sm.getString("webappClassLoader.wrongVersion", new Object[]{name}));
            }
            entry.loadedClass = clazz;
        }
        return clazz;
    }

    private String binaryNameToPath(String binaryName, boolean withLeadingSlash) {
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }
        path.append(binaryName.replace('.', '/'));
        path.append(CLASS_FILE_SUFFIX);
        return path.toString();
    }

    private String nameToPath(String name) {
        if (name.startsWith("/")) {
            return name;
        }
        StringBuilder path = new StringBuilder(1 + name.length());
        path.append('/');
        path.append(name);
        return path.toString();
    }

    protected boolean isPackageSealed(String name, Manifest man) {
        String path = name.replace('.', '/') + '/';
        Attributes attr = man.getAttributes(path);
        String sealed = null;
        if (attr != null) {
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        if (sealed == null && (attr = man.getMainAttributes()) != null) {
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        return "true".equalsIgnoreCase(sealed);
    }

    protected Class<?> findLoadedClass0(String name) {
        String path = this.binaryNameToPath(name, true);
        ResourceEntry entry = this.resourceEntries.get(path);
        if (entry != null) {
            return entry.loadedClass;
        }
        return null;
    }

    protected void refreshPolicy() {
        try {
            Policy policy = Policy.getPolicy();
            policy.refresh();
        }
        catch (AccessControlException accessControlException) {
            // empty catch block
        }
    }

    protected boolean filter(String name, boolean isClassName) {
        if (name == null) {
            return false;
        }
        if (name.startsWith("javax")) {
            if (name.length() == 5) {
                return false;
            }
            char ch = name.charAt(5);
            if (isClassName && ch == '.') {
                if (name.startsWith("servlet.jsp.jstl.", 6)) {
                    return false;
                }
                if (name.startsWith("annotation.", 6) || name.startsWith("el.", 6) || name.startsWith("servlet.", 6) || name.startsWith("websocket.", 6) || name.startsWith("security.auth.message.", 6)) {
                    return true;
                }
            } else if (!isClassName && ch == '/') {
                if (name.startsWith("servlet/jsp/jstl/", 6)) {
                    return false;
                }
                if (name.startsWith("annotation/", 6) || name.startsWith("el/", 6) || name.startsWith("servlet/", 6) || name.startsWith("websocket/", 6) || name.startsWith("security/auth/message/", 6)) {
                    return true;
                }
            }
        } else if (name.startsWith("org")) {
            if (name.length() == 3) {
                return false;
            }
            char ch = name.charAt(3);
            if (isClassName && ch == '.') {
                if (name.startsWith("apache.", 4)) {
                    if (name.startsWith("tomcat.jdbc.", 11)) {
                        return false;
                    }
                    if (name.startsWith("el.", 11) || name.startsWith("catalina.", 11) || name.startsWith("jasper.", 11) || name.startsWith("juli.", 11) || name.startsWith("tomcat.", 11) || name.startsWith("naming.", 11) || name.startsWith("coyote.", 11)) {
                        return true;
                    }
                }
            } else if (!isClassName && ch == '/' && name.startsWith("apache/", 4)) {
                if (name.startsWith("tomcat/jdbc/", 11)) {
                    return false;
                }
                if (name.startsWith("el/", 11) || name.startsWith("catalina/", 11) || name.startsWith("jasper/", 11) || name.startsWith("juli/", 11) || name.startsWith("tomcat/", 11) || name.startsWith("naming/", 11) || name.startsWith("coyote/", 11)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
        this.hasExternalRepositories = true;
    }

    public String getWebappName() {
        return this.getContextName();
    }

    public String getHostName() {
        Container host;
        if (this.resources != null && (host = this.resources.getContext().getParent()) != null) {
            return host.getName();
        }
        return null;
    }

    public String getServiceName() {
        Container engine;
        Container host;
        if (this.resources != null && (host = this.resources.getContext().getParent()) != null && (engine = host.getParent()) != null) {
            return engine.getName();
        }
        return null;
    }

    public boolean hasLoggingConfig() {
        if (Globals.IS_SECURITY_ENABLED) {
            Boolean result = AccessController.doPrivileged(new PrivilegedHasLoggingConfig());
            return result;
        }
        return this.findResource("logging.properties") != null;
    }

    static {
        if (!JreCompat.isGraalAvailable()) {
            WebappClassLoaderBase.registerAsParallelCapable();
        }
        JVM_THREAD_GROUP_NAMES.add(JVM_THREAD_GROUP_SYSTEM);
        JVM_THREAD_GROUP_NAMES.add("RMI Runtime");
        sm = StringManager.getManager(WebappClassLoaderBase.class);
    }

    protected class PrivilegedFindClassByName
    implements PrivilegedAction<Class<?>> {
        private final String name;

        PrivilegedFindClassByName(String name) {
            this.name = name;
        }

        @Override
        public Class<?> run() {
            return WebappClassLoaderBase.this.findClassInternal(this.name);
        }
    }

    protected static final class PrivilegedGetClassLoader
    implements PrivilegedAction<ClassLoader> {
        private final Class<?> clazz;

        public PrivilegedGetClassLoader(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public ClassLoader run() {
            return this.clazz.getClassLoader();
        }
    }

    private static class CombinedEnumeration
    implements Enumeration<URL> {
        private final Enumeration<URL>[] sources;
        private int index = 0;

        CombinedEnumeration(Enumeration<URL> enum1, Enumeration<URL> enum2) {
            Enumeration[] sources = new Enumeration[]{enum1, enum2};
            this.sources = sources;
        }

        @Override
        public boolean hasMoreElements() {
            return this.inc();
        }

        @Override
        public URL nextElement() {
            if (this.inc()) {
                return this.sources[this.index].nextElement();
            }
            throw new NoSuchElementException();
        }

        private boolean inc() {
            while (this.index < this.sources.length) {
                if (this.sources[this.index].hasMoreElements()) {
                    return true;
                }
                ++this.index;
            }
            return false;
        }
    }

    protected final class PrivilegedJavaseGetResource
    implements PrivilegedAction<URL> {
        private final String name;

        public PrivilegedJavaseGetResource(String name) {
            this.name = name;
        }

        @Override
        public URL run() {
            return WebappClassLoaderBase.this.javaseClassLoader.getResource(this.name);
        }
    }

    private class PrivilegedHasLoggingConfig
    implements PrivilegedAction<Boolean> {
        private PrivilegedHasLoggingConfig() {
        }

        @Override
        public Boolean run() {
            return WebappClassLoaderBase.this.findResource("logging.properties") != null;
        }
    }
}

