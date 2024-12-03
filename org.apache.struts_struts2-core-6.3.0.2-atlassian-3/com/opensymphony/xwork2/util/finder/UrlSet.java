/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.finder;

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UrlSet {
    private static final Logger LOG = LogManager.getLogger(UrlSet.class);
    private final Map<String, URL> urls;
    private Set<String> protocols;

    private UrlSet() {
        this.urls = new HashMap<String, URL>();
    }

    public UrlSet(ClassLoaderInterface classLoader) throws IOException {
        this();
        this.load(this.getUrls(classLoader));
    }

    public UrlSet(ClassLoaderInterface classLoader, Set<String> protocols) throws IOException {
        this();
        this.protocols = protocols;
        this.load(this.getUrls(classLoader, protocols));
    }

    public UrlSet(URL ... urls) {
        this(Arrays.asList(urls));
    }

    public UrlSet(Collection<URL> urls) {
        this();
        this.load(urls);
    }

    private UrlSet(Map<String, URL> urls) {
        this.urls = urls;
    }

    private void load(Collection<URL> urls) {
        for (URL location : urls) {
            try {
                this.urls.put(location.toExternalForm(), location);
            }
            catch (Exception e) {
                LOG.warn("Cannot translate url to external form!", (Throwable)e);
            }
        }
    }

    public UrlSet include(UrlSet urlSet) {
        HashMap<String, URL> urls = new HashMap<String, URL>(this.urls);
        urls.putAll(urlSet.urls);
        return new UrlSet(urls);
    }

    public UrlSet exclude(UrlSet urlSet) {
        HashMap<String, URL> urls = new HashMap<String, URL>(this.urls);
        Map<String, URL> parentUrls = urlSet.urls;
        for (String url : parentUrls.keySet()) {
            urls.remove(url);
        }
        return new UrlSet(urls);
    }

    public UrlSet exclude(ClassLoaderInterface parent) throws IOException {
        return this.exclude(new UrlSet(parent, this.protocols));
    }

    public UrlSet exclude(File file) throws MalformedURLException {
        return this.exclude(this.relative(file));
    }

    public UrlSet exclude(String pattern) throws MalformedURLException {
        return this.exclude(this.matching(pattern));
    }

    public UrlSet excludeJavaExtDirs() throws MalformedURLException {
        return this.excludePaths(System.getProperty("java.ext.dirs", ""));
    }

    public UrlSet excludeJavaEndorsedDirs() throws MalformedURLException {
        return this.excludePaths(System.getProperty("java.endorsed.dirs", ""));
    }

    public UrlSet excludeUserExtensionsDir() throws MalformedURLException {
        return this.excludePaths(System.getProperty("java.ext.dirs", ""));
    }

    public UrlSet excludeJavaHome() throws MalformedURLException {
        String path = System.getProperty("java.home");
        if (path != null) {
            File java = new File(path);
            if (path.matches("/System/Library/Frameworks/JavaVM.framework/Versions/[^/]+/Home")) {
                java = java.getParentFile();
            }
            return this.exclude(java);
        }
        return this;
    }

    public UrlSet excludePaths(String pathString) throws MalformedURLException {
        String[] paths = pathString.split(File.pathSeparator);
        UrlSet urlSet = this;
        for (String path : paths) {
            if (!StringUtils.isNotEmpty((CharSequence)path)) continue;
            File file = new File(path);
            urlSet = urlSet.exclude(file);
        }
        return urlSet;
    }

    public UrlSet matching(String pattern) {
        HashMap<String, URL> urls = new HashMap<String, URL>();
        for (Map.Entry<String, URL> entry : this.urls.entrySet()) {
            String url = entry.getKey();
            if (!url.matches(pattern)) continue;
            urls.put(url, entry.getValue());
        }
        return new UrlSet(urls);
    }

    public UrlSet includeClassesUrl(ClassLoaderInterface classLoaderInterface, FileProtocolNormalizer normalizer) throws IOException {
        Enumeration<URL> rootUrlEnumeration = classLoaderInterface.getResources("");
        while (rootUrlEnumeration.hasMoreElements()) {
            URL url = rootUrlEnumeration.nextElement();
            String externalForm = StringUtils.removeEnd((String)url.toExternalForm(), (String)"/");
            if (!externalForm.endsWith(".war/WEB-INF/classes")) continue;
            externalForm = StringUtils.substringBefore((String)externalForm, (String)"/WEB-INF/classes");
            URL warUrl = new URL(externalForm);
            URL normalizedUrl = normalizer.normalizeToFileProtocol(warUrl);
            URL finalUrl = (URL)ObjectUtils.defaultIfNull((Object)normalizedUrl, (Object)warUrl);
            HashMap<String, URL> newUrls = new HashMap<String, URL>(this.urls);
            if ("jar".equals(finalUrl.getProtocol()) || "file".equals(finalUrl.getProtocol())) {
                newUrls.put(finalUrl.toExternalForm(), finalUrl);
            }
            return new UrlSet(newUrls);
        }
        return this;
    }

    public UrlSet relative(File file) throws MalformedURLException {
        String urlPath = file.toURI().toURL().toExternalForm();
        HashMap<String, URL> urls = new HashMap<String, URL>();
        for (Map.Entry<String, URL> entry : this.urls.entrySet()) {
            String url = entry.getKey();
            if (!url.startsWith(urlPath) && !url.startsWith("jar:" + urlPath)) continue;
            urls.put(url, entry.getValue());
        }
        return new UrlSet(urls);
    }

    public List<URL> getUrls() {
        return new ArrayList<URL>(this.urls.values());
    }

    private List<URL> getUrls(ClassLoaderInterface classLoader) throws IOException {
        ArrayList<URL> list = new ArrayList<URL>();
        ArrayList<URL> urls = Collections.list(classLoader.getResources("META-INF"));
        for (URL url : urls) {
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                String externalForm = url.toExternalForm();
                url = new URL(StringUtils.substringBefore((String)externalForm, (String)"META-INF"));
                list.add(url);
                continue;
            }
            LOG.debug("Ignoring URL [{}] because it is not a jar", (Object)url.toExternalForm());
        }
        list.addAll(Collections.list(classLoader.getResources("")));
        return list;
    }

    private List<URL> getUrls(ClassLoaderInterface classLoader, Set<String> protocols) throws IOException {
        if (protocols == null) {
            return this.getUrls(classLoader);
        }
        ArrayList<URL> list = new ArrayList<URL>();
        ArrayList<URL> urls = Collections.list(classLoader.getResources("META-INF"));
        for (URL url : urls) {
            if (protocols.contains(url.getProtocol())) {
                String externalForm = url.toExternalForm();
                url = new URL(StringUtils.substringBefore((String)externalForm, (String)"META-INF"));
                list.add(url);
                continue;
            }
            LOG.debug("Ignoring URL [{}] because it is not a valid protocol", (Object)url.toExternalForm());
        }
        return list;
    }

    public static interface FileProtocolNormalizer {
        public URL normalizeToFileProtocol(URL var1);
    }
}

