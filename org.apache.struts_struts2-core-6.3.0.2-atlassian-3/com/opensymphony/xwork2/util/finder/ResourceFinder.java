/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.finder;

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceFinder {
    private static final Logger LOG = LogManager.getLogger(ResourceFinder.class);
    private final URL[] urls;
    private final String path;
    private final ClassLoaderInterface classLoaderInterface;
    private final List<String> resourcesNotLoaded = new ArrayList<String>();

    public ResourceFinder(URL ... urls) {
        this(null, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), urls);
    }

    public ResourceFinder(String path) {
        this(path, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), (URL[])null);
    }

    public ResourceFinder(String path, URL ... urls) {
        this(path, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), urls);
    }

    public ResourceFinder(String path, ClassLoaderInterface classLoaderInterface) {
        this(path, classLoaderInterface, (URL[])null);
    }

    public ResourceFinder(String path, ClassLoaderInterface classLoaderInterface, URL ... urls) {
        path = StringUtils.trimToEmpty((String)path);
        if (!path.isEmpty() && !StringUtils.endsWith((CharSequence)path, (CharSequence)"/")) {
            path = path + "/";
        }
        this.path = path;
        this.classLoaderInterface = classLoaderInterface == null ? new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()) : classLoaderInterface;
        for (int i = 0; urls != null && i < urls.length; ++i) {
            URL url = urls[i];
            if (url == null || ResourceFinder.isDirectory(url) || "jar".equals(url.getProtocol())) continue;
            try {
                urls[i] = new URL("jar", "", -1, url.toString() + "!/");
                continue;
            }
            catch (MalformedURLException malformedURLException) {
                // empty catch block
            }
        }
        this.urls = urls == null || urls.length == 0 ? null : urls;
    }

    private static boolean isDirectory(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("Cannot test if a null URL is a directory");
        }
        String file = url.getFile();
        return file.length() > 0 && file.charAt(file.length() - 1) == '/';
    }

    public List<String> getResourcesNotLoaded() {
        return Collections.unmodifiableList(this.resourcesNotLoaded);
    }

    public URL find(String uri) throws IOException {
        String fullUri = this.path + uri;
        return this.getResource(fullUri);
    }

    public List<URL> findAll(String uri) throws IOException {
        String fullUri = this.path + uri;
        Enumeration<URL> resources = this.getResources(fullUri);
        ArrayList<URL> list = new ArrayList<URL>();
        if (resources == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            list.add(url);
        }
        return list;
    }

    public String findString(String uri) throws IOException {
        String fullUri = this.path + uri;
        URL resource = this.getResource(fullUri);
        if (resource == null) {
            throw new IOException("Could not find a resource in: " + fullUri);
        }
        return this.readContents(resource);
    }

    public List<String> findAllStrings(String uri) throws IOException {
        String fulluri = this.path + uri;
        ArrayList<String> strings = new ArrayList<String>();
        Enumeration<URL> resources = this.getResources(fulluri);
        if (resources == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String string = this.readContents(url);
            strings.add(string);
        }
        return strings;
    }

    public List<String> findAvailableStrings(String uri) throws IOException {
        this.resourcesNotLoaded.clear();
        String fulluri = this.path + uri;
        ArrayList<String> strings = new ArrayList<String>();
        Enumeration<URL> resources = this.getResources(fulluri);
        if (resources == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                String string = this.readContents(url);
                strings.add(string);
            }
            catch (IOException notAvailable) {
                this.resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return strings;
    }

    public Map<String, String> mapAllStrings(String uri) throws IOException {
        HashMap<String, String> strings = new HashMap<String, String>();
        Map<String, URL> resourcesMap = this.getResourcesMap(uri);
        if (resourcesMap == null) {
            LOG.trace("Null resources URL map for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL map produced for uri: " + uri);
        }
        for (Map.Entry<String, URL> entry : resourcesMap.entrySet()) {
            String name = entry.getKey();
            URL url = entry.getValue();
            String value = this.readContents(url);
            strings.put(name, value);
        }
        return strings;
    }

    public Map<String, String> mapAvailableStrings(String uri) throws IOException {
        this.resourcesNotLoaded.clear();
        Map<String, URL> resourcesMap = this.getResourcesMap(uri);
        HashMap<String, String> strings = new HashMap<String, String>(resourcesMap != null ? resourcesMap.size() : 0);
        if (resourcesMap == null) {
            LOG.trace("Null resources URL map for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL map produced for uri: " + uri);
        }
        resourcesMap.entrySet().forEach(entry -> {
            String name = (String)entry.getKey();
            URL url = (URL)entry.getValue();
            try {
                String value = this.readContents(url);
                strings.put(name, value);
            }
            catch (IOException notAvailable) {
                this.resourcesNotLoaded.add(url.toExternalForm());
            }
        });
        return strings;
    }

    public Class findClass(String uri) throws IOException, ClassNotFoundException {
        String className = this.findString(uri);
        return this.classLoaderInterface.loadClass(className);
    }

    public List<Class> findAllClasses(String uri) throws IOException, ClassNotFoundException {
        List<String> strings = this.findAllStrings(uri);
        ArrayList<Class> classes = new ArrayList<Class>(strings != null ? strings.size() : 0);
        if (strings == null) {
            LOG.trace("Null strings list for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null strings list produced for uri: " + uri);
        }
        for (String className : strings) {
            Class<?> clazz = this.classLoaderInterface.loadClass(className);
            classes.add(clazz);
        }
        return classes;
    }

    public List<Class> findAvailableClasses(String uri) throws IOException {
        this.resourcesNotLoaded.clear();
        List<String> strings = this.findAvailableStrings(uri);
        ArrayList<Class> classes = new ArrayList<Class>(strings != null ? strings.size() : 0);
        if (strings == null) {
            LOG.trace("Null strings list for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null strings list produced for uri: " + uri);
        }
        strings.forEach(className -> {
            try {
                Class<?> clazz = this.classLoaderInterface.loadClass((String)className);
                classes.add(clazz);
            }
            catch (Exception notAvailable) {
                this.resourcesNotLoaded.add((String)className);
            }
        });
        return classes;
    }

    public Map<String, Class> mapAllClasses(String uri) throws IOException, ClassNotFoundException {
        Map<String, String> map = this.mapAllStrings(uri);
        HashMap<String, Class> classes = new HashMap<String, Class>(map != null ? map.size() : 0);
        if (map == null) {
            LOG.trace("Null strings map for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null strings map produced for uri: " + uri);
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            Class<?> clazz = this.classLoaderInterface.loadClass(className);
            classes.put(string, clazz);
        }
        return classes;
    }

    public Map<String, Class> mapAvailableClasses(String uri) throws IOException {
        this.resourcesNotLoaded.clear();
        Map<String, String> map = this.mapAvailableStrings(uri);
        HashMap<String, Class> classes = new HashMap<String, Class>(map != null ? map.size() : 0);
        if (map == null) {
            LOG.trace("Null strings map for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null strings map produced for uri: " + uri);
        }
        map.entrySet().forEach(entry -> {
            String string = (String)entry.getKey();
            String className = (String)entry.getValue();
            try {
                Class<?> clazz = this.classLoaderInterface.loadClass(className);
                classes.put(string, clazz);
            }
            catch (Exception notAvailable) {
                this.resourcesNotLoaded.add(className);
            }
        });
        return classes;
    }

    public Class findImplementation(Class interfase) throws IOException, ClassNotFoundException {
        String className = this.findString(interfase.getName());
        Class<?> impl = this.classLoaderInterface.loadClass(className);
        if (!interfase.isAssignableFrom(impl)) {
            throw new ClassCastException("Class not of type: " + interfase.getName());
        }
        return impl;
    }

    public List<Class> findAllImplementations(Class interfase) throws IOException, ClassNotFoundException {
        List<String> strings = this.findAllStrings(interfase.getName());
        ArrayList<Class> implementations = new ArrayList<Class>(strings != null ? strings.size() : 0);
        if (strings == null) {
            LOG.trace("Null strings list for [{}], should not be possible!", (Object)interfase.getName());
            throw new IllegalStateException("Null strings list produced for interface: " + interfase.getName());
        }
        for (String className : strings) {
            Class<?> impl = this.classLoaderInterface.loadClass(className);
            if (!interfase.isAssignableFrom(impl)) {
                throw new ClassCastException("Class not of type: " + interfase.getName());
            }
            implementations.add(impl);
        }
        return implementations;
    }

    public List<Class> findAvailableImplementations(Class interfase) throws IOException {
        this.resourcesNotLoaded.clear();
        List<String> strings = this.findAvailableStrings(interfase.getName());
        ArrayList<Class> implementations = new ArrayList<Class>(strings != null ? strings.size() : 0);
        if (strings == null) {
            LOG.trace("Null strings list for [{}], should not be possible!", (Object)interfase.getName());
            throw new IllegalStateException("Null strings list produced for interface: " + interfase.getName());
        }
        strings.forEach(className -> {
            try {
                Class<?> impl = this.classLoaderInterface.loadClass((String)className);
                if (interfase.isAssignableFrom(impl)) {
                    implementations.add(impl);
                } else {
                    this.resourcesNotLoaded.add((String)className);
                }
            }
            catch (Exception notAvailable) {
                this.resourcesNotLoaded.add((String)className);
            }
        });
        return implementations;
    }

    public Map<String, Class> mapAllImplementations(Class interfase) throws IOException, ClassNotFoundException {
        Map<String, String> map = this.mapAllStrings(interfase.getName());
        HashMap<String, Class> implementations = new HashMap<String, Class>(map != null ? map.size() : 0);
        if (map == null) {
            LOG.trace("Null strings map for [{}], should not be possible!", (Object)interfase.getName());
            throw new IllegalStateException("Null strings map produced for interface: " + interfase.getName());
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            Class<?> impl = this.classLoaderInterface.loadClass(className);
            if (!interfase.isAssignableFrom(impl)) {
                throw new ClassCastException("Class not of type: " + interfase.getName());
            }
            implementations.put(string, impl);
        }
        return implementations;
    }

    public Map<String, Class> mapAvailableImplementations(Class interfase) throws IOException {
        this.resourcesNotLoaded.clear();
        Map<String, String> map = this.mapAvailableStrings(interfase.getName());
        HashMap<String, Class> implementations = new HashMap<String, Class>(map != null ? map.size() : 0);
        if (map == null) {
            LOG.trace("Null strings map for [{}], should not be possible!", (Object)interfase.getName());
            throw new IllegalStateException("Null strings map produced for interface: " + interfase.getName());
        }
        map.entrySet().forEach(entry -> {
            String string = (String)entry.getKey();
            String className = (String)entry.getValue();
            try {
                Class<?> impl = this.classLoaderInterface.loadClass(className);
                if (interfase.isAssignableFrom(impl)) {
                    implementations.put(string, impl);
                } else {
                    this.resourcesNotLoaded.add(className);
                }
            }
            catch (Exception notAvailable) {
                this.resourcesNotLoaded.add(className);
            }
        });
        return implementations;
    }

    public Properties findProperties(String uri) throws IOException {
        String fulluri = this.path + uri;
        URL resource = this.getResource(fulluri);
        if (resource == null) {
            throw new IOException("Could not find a resource in: " + fulluri);
        }
        return this.loadProperties(resource);
    }

    public List<Properties> findAllProperties(String uri) throws IOException {
        String fulluri = this.path + uri;
        ArrayList<Properties> properties = new ArrayList<Properties>();
        Enumeration<URL> resources = this.getResources(fulluri);
        if (resources == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Properties props = this.loadProperties(url);
            properties.add(props);
        }
        return properties;
    }

    public List<Properties> findAvailableProperties(String uri) throws IOException {
        this.resourcesNotLoaded.clear();
        String fulluri = this.path + uri;
        ArrayList<Properties> properties = new ArrayList<Properties>();
        Enumeration<URL> resources = this.getResources(fulluri);
        if (resources == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                Properties props = this.loadProperties(url);
                properties.add(props);
            }
            catch (Exception notAvailable) {
                this.resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return properties;
    }

    public Map<String, Properties> mapAllProperties(String uri) throws IOException {
        Map<String, URL> map = this.getResourcesMap(uri);
        HashMap<String, Properties> propertiesMap = new HashMap<String, Properties>(map != null ? map.size() : 0);
        if (map == null) {
            LOG.trace("Null resources URL map for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL map produced for uri: " + uri);
        }
        for (Map.Entry<String, URL> entry : map.entrySet()) {
            String string = entry.getKey();
            URL url = entry.getValue();
            Properties properties = this.loadProperties(url);
            propertiesMap.put(string, properties);
        }
        return propertiesMap;
    }

    public Map<String, Properties> mapAvailableProperties(String uri) throws IOException {
        this.resourcesNotLoaded.clear();
        Map<String, URL> map = this.getResourcesMap(uri);
        HashMap<String, Properties> propertiesMap = new HashMap<String, Properties>(map != null ? map.size() : 0);
        if (map == null) {
            LOG.trace("Null resources URL map for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL map produced for uri: " + uri);
        }
        map.entrySet().forEach(entry -> {
            String string = (String)entry.getKey();
            URL url = (URL)entry.getValue();
            try {
                Properties properties = this.loadProperties(url);
                propertiesMap.put(string, properties);
            }
            catch (Exception notAvailable) {
                this.resourcesNotLoaded.add(url.toExternalForm());
            }
        });
        return propertiesMap;
    }

    public Map<String, URL> getResourcesMap(String uri) throws IOException {
        Enumeration<URL> urlsForURI;
        String basePath = this.path + uri;
        HashMap<String, URL> resources = new HashMap<String, URL>();
        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }
        if ((urlsForURI = this.getResources(basePath)) == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        while (urlsForURI.hasMoreElements()) {
            URL location = urlsForURI.nextElement();
            try {
                if ("jar".equals(location.getProtocol())) {
                    ResourceFinder.readJarEntries(location, basePath, resources);
                    continue;
                }
                if (!"file".equals(location.getProtocol())) continue;
                ResourceFinder.readDirectoryEntries(location, resources);
            }
            catch (Exception e) {
                LOG.debug("Got exception loading resources for {}", (Object)uri, (Object)e);
            }
        }
        return resources;
    }

    public Set<String> findPackages(String uri) throws IOException {
        Enumeration<URL> urlsForURI;
        String basePath = this.path + uri;
        HashSet<String> resources = new HashSet<String>();
        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }
        if ((urlsForURI = this.getResources(basePath)) == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        while (urlsForURI.hasMoreElements()) {
            URL location = urlsForURI.nextElement();
            try {
                if ("jar".equals(location.getProtocol())) {
                    ResourceFinder.readJarDirectoryEntries(location, basePath, resources);
                    continue;
                }
                if (!"file".equals(location.getProtocol())) continue;
                ResourceFinder.readSubDirectories(new File(location.toURI()), uri, resources);
            }
            catch (Exception e) {
                LOG.debug("Got exception search for subpackages for {}", (Object)uri, (Object)e);
            }
        }
        return this.convertPathsToPackages(resources);
    }

    public Map<URL, Set<String>> findPackagesMap(String uri) throws IOException {
        String basePath = this.path + uri;
        LOG.trace("    basePath(initial): " + basePath);
        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }
        LOG.trace("    basePath(final): " + basePath);
        Enumeration<URL> urlsForURI = this.getResources(basePath);
        HashMap<URL, Set<String>> result = new HashMap<URL, Set<String>>();
        if (urlsForURI == null) {
            LOG.trace("Null resources URL enumeration for [{}], should not be possible!", (Object)uri);
            throw new IllegalStateException("Null resources URL enumeration produced for uri: " + uri);
        }
        if (!urlsForURI.hasMoreElements()) {
            LOG.debug("    urls enumeration for basePath is empty ?");
        }
        while (urlsForURI.hasMoreElements()) {
            URL location = urlsForURI.nextElement();
            LOG.debug("       url (location): " + location);
            try {
                HashSet<String> resources;
                if ("jar".equals(location.getProtocol())) {
                    resources = new HashSet<String>();
                    ResourceFinder.readJarDirectoryEntries(location, basePath, resources);
                    result.put(location, this.convertPathsToPackages(resources));
                    continue;
                }
                if (!"file".equals(location.getProtocol())) continue;
                resources = new HashSet();
                ResourceFinder.readSubDirectories(new File(location.toURI()), uri, resources);
                result.put(location, this.convertPathsToPackages(resources));
            }
            catch (Exception e) {
                LOG.debug("Got exception finding subpackages for {}", (Object)uri, (Object)e);
            }
        }
        return result;
    }

    private Set<String> convertPathsToPackages(Set<String> resources) {
        HashSet<String> packageNames = new HashSet<String>(resources.size());
        resources.forEach(resource -> packageNames.add(StringUtils.removeEnd((String)StringUtils.replace((String)resource, (String)"/", (String)"."), (String)".")));
        return packageNames;
    }

    private static void readDirectoryEntries(URL location, Map<String, URL> resources) throws MalformedURLException {
        File dir = new File(URLDecoder.decode(location.getPath()));
        if (dir.isDirectory()) {
            File[] files;
            for (File file : files = dir.listFiles()) {
                if (file.isDirectory()) continue;
                String name = file.getName();
                URL url = file.toURL();
                resources.put(name, url);
            }
        }
    }

    private static void readSubDirectories(File dir, String basePath, Set<String> resources) throws MalformedURLException {
        if (dir.isDirectory()) {
            File[] files;
            for (File file : files = dir.listFiles()) {
                if (!file.isDirectory()) continue;
                String name = file.getName();
                String subName = StringUtils.removeEnd((String)basePath, (String)"/") + "/" + name;
                resources.add(subName);
                ResourceFinder.readSubDirectories(file, subName, resources);
            }
        }
    }

    private static void readJarEntries(URL location, String basePath, Map<String, URL> resources) throws IOException {
        JarURLConnection conn = (JarURLConnection)location.openConnection();
        JarFile jarfile = conn.getJarFile();
        Enumeration<JarEntry> entries = jarfile.entries();
        while (entries != null && entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (entry.isDirectory() || !name.startsWith(basePath) || name.length() == basePath.length() || (name = name.substring(basePath.length())).contains("/")) continue;
            URL resource = new URL(location, name);
            resources.put(name, resource);
        }
    }

    private static void readJarDirectoryEntries(URL location, String basePath, Set<String> resources) throws IOException {
        JarURLConnection conn = (JarURLConnection)location.openConnection();
        JarFile jarfile = conn.getJarFile();
        Enumeration<JarEntry> entries = jarfile.entries();
        if (entries == null || !entries.hasMoreElements()) {
            LOG.debug("           JAR entries null or empty");
        }
        LOG.debug("           Looking for entries matching basePath: " + basePath);
        while (entries != null && entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (entry.isDirectory() && StringUtils.startsWith((CharSequence)name, (CharSequence)basePath)) {
                resources.add(name);
                continue;
            }
            if (!entry.isDirectory()) continue;
            LOG.trace("           entry: " + name + " , isDirectory: " + entry.isDirectory() + " but does not start with basepath");
        }
    }

    private Properties loadProperties(URL resource) throws IOException {
        try (BufferedInputStream reader = new BufferedInputStream(resource.openStream());){
            Properties properties = new Properties();
            properties.load(reader);
            Properties properties2 = properties;
            return properties2;
        }
    }

    private String readContents(URL resource) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedInputStream reader = new BufferedInputStream(resource.openStream());){
            int b = ((InputStream)reader).read();
            while (b != -1) {
                sb.append((char)b);
                b = ((InputStream)reader).read();
            }
            String string = sb.toString().trim();
            return string;
        }
    }

    private URL getResource(String fullUri) {
        if (this.urls == null) {
            return this.classLoaderInterface.getResource(fullUri);
        }
        return this.findResource(fullUri, this.urls);
    }

    private Enumeration<URL> getResources(String fulluri) throws IOException {
        if (this.urls == null) {
            LOG.debug("    urls (member) null, using classLoaderInterface to get resources");
            return this.classLoaderInterface.getResources(fulluri);
        }
        LOG.debug("    urls (member) non-null, using findResource to get resources");
        ArrayList<URL> resources = new ArrayList<URL>(this.urls != null ? this.urls.length : 0);
        for (URL url : this.urls) {
            URL resource = this.findResource(fulluri, url);
            if (resource != null) {
                LOG.trace("    resource lookup non-null");
                resources.add(resource);
                continue;
            }
            LOG.trace("    resource lookup is null");
        }
        return Collections.enumeration(resources);
    }

    private URL findResource(String resourceName, URL ... search) {
        for (int i = 0; i < search.length; ++i) {
            URL currentUrl = search[i];
            if (currentUrl == null) continue;
            try {
                String protocol = currentUrl.getProtocol();
                if ("jar".equals(protocol)) {
                    String entryName;
                    JarFile jarFile;
                    URL jarURL = ((JarURLConnection)currentUrl.openConnection()).getJarFileURL();
                    try {
                        JarURLConnection juc = (JarURLConnection)new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                        jarFile = juc.getJarFile();
                    }
                    catch (IOException e) {
                        search[i] = null;
                        throw e;
                    }
                    if (currentUrl.getFile().endsWith("!/")) {
                        entryName = resourceName;
                    } else {
                        String file = currentUrl.getFile();
                        int sepIdx = file.lastIndexOf("!/");
                        if (sepIdx == -1) {
                            search[i] = null;
                            continue;
                        }
                        StringBuilder sb = new StringBuilder(file.length() - (sepIdx += 2) + resourceName.length());
                        sb.append(file.substring(sepIdx));
                        sb.append(resourceName);
                        entryName = sb.toString();
                    }
                    if ("META-INF/".equals(entryName) && jarFile.getEntry("META-INF/MANIFEST.MF") != null) {
                        return this.targetURL(currentUrl, "META-INF/MANIFEST.MF");
                    }
                    if (jarFile.getEntry(entryName) == null) continue;
                    return this.targetURL(currentUrl, resourceName);
                }
                if ("file".equals(protocol)) {
                    String baseFile = currentUrl.getFile();
                    String host = currentUrl.getHost();
                    int hostLength = 0;
                    if (host != null) {
                        hostLength = host.length();
                    }
                    StringBuilder buf = new StringBuilder(2 + hostLength + baseFile.length() + resourceName.length());
                    if (hostLength > 0) {
                        buf.append("//").append(host);
                    }
                    buf.append(baseFile);
                    String fixedResName = resourceName;
                    while (fixedResName.startsWith("/") || fixedResName.startsWith("\\")) {
                        fixedResName = fixedResName.substring(1);
                    }
                    buf.append(fixedResName);
                    String filename = buf.toString();
                    File file = new File(filename);
                    File file2 = new File(URLDecoder.decode(filename));
                    if (!file.exists() && !file2.exists()) continue;
                    return this.targetURL(currentUrl, fixedResName);
                }
                URL resourceURL = this.targetURL(currentUrl, resourceName);
                URLConnection urlConnection = resourceURL.openConnection();
                try {
                    urlConnection.getInputStream().close();
                }
                catch (SecurityException e) {
                    return null;
                }
                if (!"http".equals(resourceURL.getProtocol())) {
                    return resourceURL;
                }
                int code = ((HttpURLConnection)urlConnection).getResponseCode();
                if (code < 200 || code >= 300) continue;
                return resourceURL;
            }
            catch (IOException | SecurityException exception) {
                // empty catch block
            }
        }
        return null;
    }

    private URL targetURL(URL base, String name) throws MalformedURLException {
        StringBuilder sb = new StringBuilder(base.getFile().length() + name.length());
        sb.append(base.getFile());
        sb.append(name);
        String file = sb.toString();
        return new URL(base.getProtocol(), base.getHost(), base.getPort(), file, null);
    }
}

