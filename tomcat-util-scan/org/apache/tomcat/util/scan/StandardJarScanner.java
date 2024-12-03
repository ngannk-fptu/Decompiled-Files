/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.JarScanFilter
 *  org.apache.tomcat.JarScanType
 *  org.apache.tomcat.JarScanner
 *  org.apache.tomcat.JarScannerCallback
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.scan;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.JarFactory;
import org.apache.tomcat.util.scan.StandardJarScanFilter;

public class StandardJarScanner
implements JarScanner {
    private final Log log = LogFactory.getLog(StandardJarScanner.class);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.tomcat.util.scan");
    private static final Set<ClassLoader> CLASSLOADER_HIERARCHY;
    private boolean scanClassPath = true;
    private boolean scanManifest = true;
    private boolean scanAllFiles = false;
    private boolean scanAllDirectories = true;
    private boolean scanBootstrapClassPath = false;
    private JarScanFilter jarScanFilter = new StandardJarScanFilter();

    public boolean isScanClassPath() {
        return this.scanClassPath;
    }

    public void setScanClassPath(boolean scanClassPath) {
        this.scanClassPath = scanClassPath;
    }

    public boolean isScanManifest() {
        return this.scanManifest;
    }

    public void setScanManifest(boolean scanManifest) {
        this.scanManifest = scanManifest;
    }

    public boolean isScanAllFiles() {
        return this.scanAllFiles;
    }

    public void setScanAllFiles(boolean scanAllFiles) {
        this.scanAllFiles = scanAllFiles;
    }

    public boolean isScanAllDirectories() {
        return this.scanAllDirectories;
    }

    public void setScanAllDirectories(boolean scanAllDirectories) {
        this.scanAllDirectories = scanAllDirectories;
    }

    public boolean isScanBootstrapClassPath() {
        return this.scanBootstrapClassPath;
    }

    public void setScanBootstrapClassPath(boolean scanBootstrapClassPath) {
        this.scanBootstrapClassPath = scanBootstrapClassPath;
    }

    public JarScanFilter getJarScanFilter() {
        return this.jarScanFilter;
    }

    public void setJarScanFilter(JarScanFilter jarScanFilter) {
        this.jarScanFilter = jarScanFilter;
    }

    public void scan(JarScanType scanType, ServletContext context, JarScannerCallback callback) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)sm.getString("jarScan.webinflibStart"));
        }
        if (this.jarScanFilter.isSkipAll()) {
            return;
        }
        HashSet<URL> processedURLs = new HashSet<URL>();
        Set dirList = context.getResourcePaths("/WEB-INF/lib/");
        if (dirList != null) {
            for (String path : dirList) {
                if (path.endsWith(".jar") && this.getJarScanFilter().check(scanType, path.substring(path.lastIndexOf(47) + 1))) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)sm.getString("jarScan.webinflibJarScan", new Object[]{path}));
                    }
                    URL url = null;
                    try {
                        url = context.getResource(path);
                        if (url != null) {
                            processedURLs.add(url);
                            this.process(scanType, callback, url, path, true, null);
                            continue;
                        }
                        this.log.warn((Object)sm.getString("jarScan.webinflibFail", new Object[]{path}));
                    }
                    catch (IOException e) {
                        this.log.warn((Object)sm.getString("jarScan.webinflibFail", new Object[]{url}), (Throwable)e);
                    }
                    continue;
                }
                if (!this.log.isTraceEnabled()) continue;
                this.log.trace((Object)sm.getString("jarScan.webinflibJarNoScan", new Object[]{path}));
            }
        }
        try {
            URL webInfURL = context.getResource("/WEB-INF/classes");
            if (webInfURL != null) {
                URL url;
                processedURLs.add(webInfURL);
                if (this.isScanAllDirectories() && (url = context.getResource("/WEB-INF/classes/META-INF")) != null) {
                    try {
                        callback.scanWebInfClasses();
                    }
                    catch (IOException e) {
                        this.log.warn((Object)sm.getString("jarScan.webinfclassesFail"), (Throwable)e);
                    }
                }
            }
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        if (this.isScanClassPath()) {
            this.doScanClassPath(scanType, context, callback, processedURLs);
        }
    }

    protected void doScanClassPath(JarScanType scanType, ServletContext context, JarScannerCallback callback, Set<URL> processedURLs) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)sm.getString("jarScan.classloaderStart"));
        }
        ClassLoader stopLoader = null;
        if (!this.isScanBootstrapClassPath()) {
            stopLoader = ClassLoader.getSystemClassLoader().getParent();
        }
        boolean isWebapp = true;
        ArrayDeque<URL> classPathUrlsToProcess = new ArrayDeque<URL>();
        for (ClassLoader classLoader = context.getClassLoader(); classLoader != null && classLoader != stopLoader; classLoader = classLoader.getParent()) {
            if (!(classLoader instanceof URLClassLoader)) continue;
            if (isWebapp) {
                isWebapp = StandardJarScanner.isWebappClassLoader(classLoader);
            }
            classPathUrlsToProcess.addAll(Arrays.asList(((URLClassLoader)classLoader).getURLs()));
            this.processURLs(scanType, callback, processedURLs, isWebapp, classPathUrlsToProcess);
        }
        if (JreCompat.isJre9Available()) {
            this.addClassPath(classPathUrlsToProcess);
            JreCompat.getInstance().addBootModulePath(classPathUrlsToProcess);
            this.processURLs(scanType, callback, processedURLs, false, classPathUrlsToProcess);
        }
    }

    protected void processURLs(JarScanType scanType, JarScannerCallback callback, Set<URL> processedURLs, boolean isWebapp, Deque<URL> classPathUrlsToProcess) {
        if (this.jarScanFilter.isSkipAll()) {
            return;
        }
        while (!classPathUrlsToProcess.isEmpty()) {
            URL url = classPathUrlsToProcess.pop();
            if (processedURLs.contains(url)) continue;
            ClassPathEntry cpe = new ClassPathEntry(url);
            if ((cpe.isJar() || scanType == JarScanType.PLUGGABILITY || this.isScanAllDirectories()) && this.getJarScanFilter().check(scanType, cpe.getName())) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("jarScan.classloaderJarScan", new Object[]{url}));
                }
                try {
                    processedURLs.add(url);
                    this.process(scanType, callback, url, null, isWebapp, classPathUrlsToProcess);
                }
                catch (IOException ioe) {
                    this.log.warn((Object)sm.getString("jarScan.classloaderFail", new Object[]{url}), (Throwable)ioe);
                }
                continue;
            }
            if (!this.log.isTraceEnabled()) continue;
            this.log.trace((Object)sm.getString("jarScan.classloaderJarNoScan", new Object[]{url}));
        }
    }

    protected void addClassPath(Deque<URL> classPathUrlsToProcess) {
        String[] classPathEntries;
        String classPath = System.getProperty("java.class.path");
        if (classPath == null || classPath.length() == 0) {
            return;
        }
        for (String classPathEntry : classPathEntries = classPath.split(File.pathSeparator)) {
            File f = new File(classPathEntry);
            try {
                classPathUrlsToProcess.add(f.toURI().toURL());
            }
            catch (MalformedURLException e) {
                this.log.warn((Object)sm.getString("jarScan.classPath.badEntry", new Object[]{classPathEntry}), (Throwable)e);
            }
        }
    }

    private static boolean isWebappClassLoader(ClassLoader classLoader) {
        return !CLASSLOADER_HIERARCHY.contains(classLoader);
    }

    protected void process(JarScanType scanType, JarScannerCallback callback, URL url, String webappPath, boolean isWebapp, Deque<URL> classPathUrlsToProcess) throws IOException {
        block24: {
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)sm.getString("jarScan.jarUrlStart", new Object[]{url}));
            }
            if ("jar".equals(url.getProtocol()) || url.getPath().endsWith(".jar")) {
                try (Jar jar = JarFactory.newInstance(url);){
                    if (this.isScanManifest()) {
                        this.processManifest(jar, isWebapp, classPathUrlsToProcess);
                    }
                    callback.scan(jar, webappPath, isWebapp);
                }
            }
            if ("file".equals(url.getProtocol())) {
                try {
                    File f = new File(url.toURI());
                    if (f.isFile() && this.isScanAllFiles()) {
                        URL jarURL = UriUtil.buildJarUrl((File)f);
                        try (Jar jar = JarFactory.newInstance(jarURL);){
                            if (this.isScanManifest()) {
                                this.processManifest(jar, isWebapp, classPathUrlsToProcess);
                            }
                            callback.scan(jar, webappPath, isWebapp);
                            break block24;
                        }
                    }
                    if (f.isDirectory()) {
                        if (scanType == JarScanType.PLUGGABILITY) {
                            callback.scan(f, webappPath, isWebapp);
                        } else {
                            File metainf = new File(f.getAbsoluteFile() + File.separator + "META-INF");
                            if (metainf.isDirectory()) {
                                callback.scan(f, webappPath, isWebapp);
                            }
                        }
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    throw new IOException(t);
                }
            }
        }
    }

    private void processManifest(Jar jar, boolean isWebapp, Deque<URL> classPathUrlsToProcess) throws IOException {
        if (isWebapp || classPathUrlsToProcess == null) {
            return;
        }
        Manifest manifest = jar.getManifest();
        if (manifest != null) {
            String[] classPathEntries;
            Attributes attributes = manifest.getMainAttributes();
            String classPathAttribute = attributes.getValue("Class-Path");
            if (classPathAttribute == null) {
                return;
            }
            for (String classPathEntry : classPathEntries = classPathAttribute.split(" ")) {
                URL classPathEntryURL;
                if ((classPathEntry = classPathEntry.trim()).length() == 0) continue;
                URL jarURL = jar.getJarFileURL();
                try {
                    URI jarURI = jarURL.toURI();
                    URI classPathEntryURI = jarURI.resolve(classPathEntry);
                    classPathEntryURL = classPathEntryURI.toURL();
                }
                catch (Exception e) {
                    if (!this.log.isDebugEnabled()) continue;
                    this.log.debug((Object)sm.getString("jarScan.invalidUri", new Object[]{jarURL}), (Throwable)e);
                    continue;
                }
                classPathUrlsToProcess.add(classPathEntryURL);
            }
        }
    }

    static {
        HashSet<ClassLoader> cls = new HashSet<ClassLoader>();
        for (ClassLoader cl = StandardJarScanner.class.getClassLoader(); cl != null; cl = cl.getParent()) {
            cls.add(cl);
        }
        CLASSLOADER_HIERARCHY = Collections.unmodifiableSet(cls);
    }

    private static class ClassPathEntry {
        private final boolean jar;
        private final String name;

        ClassPathEntry(URL url) {
            String path = url.getPath();
            int end = path.lastIndexOf(".jar");
            if (end != -1) {
                this.jar = true;
                int start = path.lastIndexOf(47, end);
                this.name = path.substring(start + 1, end + 4);
            } else {
                this.jar = false;
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                int start = path.lastIndexOf(47);
                this.name = path.substring(start + 1);
            }
        }

        public boolean isJar() {
            return this.jar;
        }

        public String getName() {
            return this.name;
        }
    }
}

