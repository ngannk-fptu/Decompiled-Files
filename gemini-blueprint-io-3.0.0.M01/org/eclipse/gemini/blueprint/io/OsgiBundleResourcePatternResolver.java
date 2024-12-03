/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.springframework.core.io.ContextResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.UrlResource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.PathMatcher
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.io;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.io.OsgiBundleResource;
import org.eclipse.gemini.blueprint.io.OsgiBundleResourceLoader;
import org.eclipse.gemini.blueprint.io.UrlContextResource;
import org.eclipse.gemini.blueprint.io.internal.OsgiHeaderUtils;
import org.eclipse.gemini.blueprint.io.internal.OsgiResourceUtils;
import org.eclipse.gemini.blueprint.io.internal.OsgiUtils;
import org.eclipse.gemini.blueprint.io.internal.resolver.DependencyResolver;
import org.eclipse.gemini.blueprint.io.internal.resolver.ImportedBundle;
import org.eclipse.gemini.blueprint.io.internal.resolver.PackageAdminResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

public class OsgiBundleResourcePatternResolver
extends PathMatchingResourcePatternResolver {
    private static final Log logger = LogFactory.getLog(OsgiBundleResourcePatternResolver.class);
    private final Bundle bundle;
    private final BundleContext bundleContext;
    private static final String FOLDER_SEPARATOR = "/";
    private static final String FOLDER_WILDCARD = "**";
    private static final String JAR_EXTENSION = ".jar";
    private static final String BUNDLE_DEFAULT_CP = ".";
    private static final char SLASH = '/';
    private static final char DOT = '.';
    private final DependencyResolver resolver;

    public OsgiBundleResourcePatternResolver(Bundle bundle) {
        this((ResourceLoader)new OsgiBundleResourceLoader(bundle));
    }

    public OsgiBundleResourcePatternResolver(ResourceLoader resourceLoader) {
        super(resourceLoader);
        this.bundle = resourceLoader instanceof OsgiBundleResourceLoader ? ((OsgiBundleResourceLoader)resourceLoader).getBundle() : null;
        this.bundleContext = this.bundle != null ? OsgiUtils.getBundleContext(this.bundle) : null;
        this.resolver = this.bundleContext != null ? new PackageAdminResolver(this.bundleContext) : null;
    }

    protected Resource[] findResources(String locationPattern) throws IOException {
        Assert.notNull((Object)locationPattern, (String)"Location pattern must not be null");
        int type = OsgiResourceUtils.getSearchType(locationPattern);
        if (this.getPathMatcher().isPattern(locationPattern)) {
            if (OsgiResourceUtils.isClassPathType(type)) {
                return this.findClassPathMatchingResources(locationPattern, type);
            }
            return this.findPathMatchingResources(locationPattern, type);
        }
        ContextResource[] result = null;
        OsgiBundleResource resource = new OsgiBundleResource(this.bundle, locationPattern);
        switch (type) {
            case 0: 
            case 16: {
                result = resource.getAllUrlsFromBundleSpace(locationPattern);
                break;
            }
            default: {
                if (resource.exists()) break;
                result = new Resource[]{resource};
            }
        }
        return result;
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        Object[] resources = this.findResources(locationPattern);
        if (ObjectUtils.isEmpty((Object[])resources) && !this.getPathMatcher().isPattern(locationPattern)) {
            return new Resource[]{this.getResourceLoader().getResource(locationPattern)};
        }
        return resources;
    }

    private Resource[] findClassPathMatchingResources(String locationPattern, int type) throws IOException {
        if (this.resolver == null) {
            throw new IllegalArgumentException("PackageAdmin service/a started bundle is required for classpath matching");
        }
        final ImportedBundle[] importedBundles = this.resolver.getImportedBundles(this.bundle);
        final String path = OsgiResourceUtils.stripPrefix(locationPattern);
        final LinkedHashSet<String> foundPaths = new LinkedHashSet<String>();
        final String rootDirPath = this.determineFolderPattern(path);
        if (System.getSecurityManager() != null) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                    @Override
                    public Object run() throws IOException {
                        for (int i = 0; i < importedBundles.length; ++i) {
                            ImportedBundle importedBundle = importedBundles[i];
                            if (OsgiBundleResourcePatternResolver.this.bundle.equals(importedBundle.getBundle())) continue;
                            OsgiBundleResourcePatternResolver.this.findImportedBundleMatchingResource(importedBundle, rootDirPath, path, foundPaths);
                        }
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException pe) {
                throw (IOException)pe.getException();
            }
        } else {
            for (int i = 0; i < importedBundles.length; ++i) {
                ImportedBundle importedBundle = importedBundles[i];
                if (this.bundle.equals(importedBundle.getBundle())) continue;
                this.findImportedBundleMatchingResource(importedBundle, rootDirPath, path, foundPaths);
            }
        }
        this.findSyntheticClassPathMatchingResource(this.bundle, path, foundPaths);
        ArrayList<UrlContextResource> resources = new ArrayList<UrlContextResource>(foundPaths.size());
        for (String resourcePath : foundPaths) {
            if (512 == type) {
                CollectionUtils.mergeArrayIntoCollection((Object)this.convertURLEnumerationToResourceArray(this.bundle.getResources(resourcePath), resourcePath), resources);
                continue;
            }
            URL url = this.bundle.getResource(resourcePath);
            if (url == null) continue;
            resources.add(new UrlContextResource(url, resourcePath));
        }
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Fitered " + foundPaths + " to " + resources));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    private String determineFolderPattern(String path) {
        int index = path.lastIndexOf(FOLDER_SEPARATOR);
        return index > 0 ? path.substring(0, index + 1) : "";
    }

    private ContextResource[] convertURLEnumerationToResourceArray(Enumeration<URL> enm, String path) {
        LinkedHashSet<UrlContextResource> resources = new LinkedHashSet<UrlContextResource>(4);
        while (enm != null && enm.hasMoreElements()) {
            resources.add(new UrlContextResource(enm.nextElement(), path));
        }
        return resources.toArray(new ContextResource[resources.size()]);
    }

    private void findImportedBundleMatchingResource(ImportedBundle importedBundle, String rootPath, String path, Collection<String> foundPaths) throws IOException {
        boolean trace = logger.isTraceEnabled();
        Object[] packages = importedBundle.getImportedPackages();
        if (trace) {
            logger.trace((Object)("Searching path [" + path + "] on imported pkgs " + ObjectUtils.nullSafeToString((Object[])packages) + "..."));
        }
        boolean startsWithSlash = rootPath.startsWith(FOLDER_SEPARATOR);
        for (int i = 0; i < packages.length; ++i) {
            PathMatcher matcher;
            String pkg = ((String)packages[i]).replace('.', '/') + '/';
            if (startsWithSlash) {
                pkg = FOLDER_SEPARATOR + pkg;
            }
            if (!(matcher = this.getPathMatcher()).matchStart(path, pkg)) continue;
            Bundle bundle = importedBundle.getBundle();
            Enumeration entries = bundle.getEntryPaths(pkg);
            while (entries != null && entries.hasMoreElements()) {
                String entry = (String)entries.nextElement();
                if (startsWithSlash) {
                    entry = FOLDER_SEPARATOR + entry;
                }
                if (!matcher.match(path, entry)) continue;
                if (trace) {
                    logger.trace((Object)("Found entry [" + entry + "]"));
                }
                foundPaths.add(entry);
            }
            Collection<String> cpMatchingPaths = this.findBundleClassPathMatchingPaths(bundle, path);
            foundPaths.addAll(cpMatchingPaths);
        }
    }

    private void findSyntheticClassPathMatchingResource(Bundle bundle, String path, Collection<String> foundPaths) throws IOException {
        OsgiBundleResourcePatternResolver localPatternResolver = new OsgiBundleResourcePatternResolver(bundle);
        Object[] foundResources = localPatternResolver.findResources(path);
        boolean trace = logger.isTraceEnabled();
        if (trace) {
            logger.trace((Object)("Found synthetic cp resources " + ObjectUtils.nullSafeToString((Object[])foundResources)));
        }
        for (int j = 0; j < foundResources.length; ++j) {
            foundPaths.add(foundResources[j].getURL().getPath());
        }
        Collection<String> cpMatchingPaths = this.findBundleClassPathMatchingPaths(bundle, path);
        if (trace) {
            logger.trace((Object)("Found Bundle-ClassPath matches " + cpMatchingPaths));
        }
        foundPaths.addAll(cpMatchingPaths);
    }

    private Collection<String> findBundleClassPathMatchingPaths(Bundle bundle, String pattern) throws IOException {
        ArrayList<String> list = new ArrayList<String>(4);
        boolean trace = logger.isTraceEnabled();
        if (trace) {
            logger.trace((Object)("Analyzing Bundle-ClassPath entries for bundle [" + bundle.getBundleId() + "|" + bundle.getSymbolicName() + "]"));
        }
        Object[] entries = OsgiHeaderUtils.getBundleClassPath(bundle);
        if (trace) {
            logger.trace((Object)("Found Bundle-ClassPath entries " + ObjectUtils.nullSafeToString((Object[])entries)));
        }
        for (int i = 0; i < entries.length; ++i) {
            Object entry = entries[i];
            if (((String)entry).equals(BUNDLE_DEFAULT_CP)) continue;
            OsgiBundleResource entryResource = new OsgiBundleResource(bundle, (String)entry);
            URL url = null;
            ContextResource res = entryResource.getResourceFromBundleSpace((String)entry);
            if (res != null) {
                url = res.getURL();
            }
            if (trace) {
                logger.trace((Object)("Classpath entry [" + (String)entry + "] resolves to [" + url + "]"));
            }
            if (url == null) continue;
            String cpEntryPath = url.getPath();
            if (((String)entry).endsWith(JAR_EXTENSION)) {
                this.findBundleClassPathMatchingJarEntries(list, url, pattern);
                continue;
            }
            this.findBundleClassPathMatchingFolders(list, bundle, cpEntryPath, pattern);
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void findBundleClassPathMatchingJarEntries(List<String> list, URL url, String pattern) throws IOException {
        JarInputStream jis = new JarInputStream(url.openStream());
        LinkedHashSet<String> result = new LinkedHashSet<String>(8);
        boolean patternWithFolderSlash = pattern.startsWith(FOLDER_SEPARATOR);
        try {
            while (jis.available() > 0) {
                JarEntry jarEntry = jis.getNextJarEntry();
                if (jarEntry == null) continue;
                String entryPath = jarEntry.getName();
                if (entryPath.startsWith(FOLDER_SEPARATOR)) {
                    if (!patternWithFolderSlash) {
                        entryPath = entryPath.substring(FOLDER_SEPARATOR.length());
                    }
                } else if (patternWithFolderSlash) {
                    entryPath = FOLDER_SEPARATOR.concat(entryPath);
                }
                if (!this.getPathMatcher().match(pattern, entryPath)) continue;
                result.add(entryPath);
            }
        }
        finally {
            try {
                jis.close();
            }
            catch (IOException iOException) {}
        }
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Found in nested jar [" + url + "] matching entries " + result));
        }
        list.addAll(result);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void findBundleClassPathMatchingFolders(List<String> list, Bundle bundle, String cpEntryPath, String pattern) throws IOException {
        boolean entryWithFolderSlash = cpEntryPath.endsWith(FOLDER_SEPARATOR);
        boolean patternWithFolderSlash = pattern.startsWith(FOLDER_SEPARATOR);
        String bundlePathPattern = entryWithFolderSlash ? (patternWithFolderSlash ? cpEntryPath + pattern.substring(1, pattern.length()) : cpEntryPath + pattern) : (patternWithFolderSlash ? cpEntryPath + pattern : cpEntryPath + FOLDER_SEPARATOR + pattern);
        OsgiBundleResourcePatternResolver localResolver = new OsgiBundleResourcePatternResolver(bundle);
        Resource[] resources = localResolver.getResources(bundlePathPattern);
        boolean trace = logger.isTraceEnabled();
        ArrayList<String> foundResources = trace ? new ArrayList<String>(resources.length) : null;
        try {
            if (resources.length == 1 && !resources[0].exists()) {
                return;
            }
            int cutStartingIndex = cpEntryPath.length();
            for (int i = 0; i < resources.length; ++i) {
                String path = resources[i].getURL().getPath().substring(cutStartingIndex);
                list.add(path);
                if (!trace) continue;
                foundResources.add(path);
            }
        }
        finally {
            if (trace) {
                logger.trace((Object)("Searching for [" + bundlePathPattern + "] revealed resources (relative to the cp entry [" + cpEntryPath + "]): " + foundResources));
            }
        }
    }

    private Resource[] findPathMatchingResources(String locationPattern, int searchType) throws IOException {
        String rootDirPath = this.determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Object[] rootDirResources = this.getResources(rootDirPath);
        boolean trace = logger.isTraceEnabled();
        if (trace) {
            logger.trace((Object)("Found root resources for [" + rootDirPath + "] :" + ObjectUtils.nullSafeToString((Object[])rootDirResources)));
        }
        LinkedHashSet<Resource> result = new LinkedHashSet<Resource>();
        for (Object rootDirResource : rootDirResources) {
            if (this.isJarResource((Resource)rootDirResource)) {
                result.addAll(this.doFindPathMatchingJarResources((Resource)rootDirResource, new URL(""), subPattern));
                continue;
            }
            result.addAll(this.doFindPathMatchingFileResources((Resource)rootDirResource, subPattern, searchType));
        }
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Resolved location pattern [" + locationPattern + "] to resources " + result));
        }
        return result.toArray(new Resource[result.size()]);
    }

    protected boolean isJarResource(Resource resource) throws IOException {
        OsgiBundleResource bundleResource;
        if (resource instanceof OsgiBundleResource && (bundleResource = (OsgiBundleResource)resource).getSearchType() != -1) {
            return false;
        }
        return super.isJarResource(resource);
    }

    private Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern, int searchType) throws IOException {
        String rootPath = null;
        if (rootDirResource instanceof OsgiBundleResource) {
            OsgiBundleResource bundleResource = (OsgiBundleResource)rootDirResource;
            rootPath = bundleResource.getPath();
            searchType = bundleResource.getSearchType();
        } else if (rootDirResource instanceof UrlResource) {
            rootPath = rootDirResource.getURL().getPath();
        }
        if (rootPath != null) {
            String cleanPath = OsgiResourceUtils.stripPrefix(rootPath);
            if (!cleanPath.endsWith(FOLDER_SEPARATOR)) {
                cleanPath = cleanPath + FOLDER_SEPARATOR;
            }
            String fullPattern = cleanPath + subPattern;
            LinkedHashSet<Resource> result = new LinkedHashSet<Resource>();
            this.doRetrieveMatchingBundleEntries(this.bundle, fullPattern, cleanPath, result, searchType);
            return result;
        }
        return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
    }

    private void doRetrieveMatchingBundleEntries(Bundle bundle, String fullPattern, String dir, Set<Resource> result, int searchType) throws IOException {
        Enumeration candidates;
        switch (searchType) {
            case 0: 
            case 16: {
                candidates = bundle.findEntries(dir, null, false);
                break;
            }
            case 1: {
                candidates = bundle.getEntryPaths(dir);
                break;
            }
            case 256: {
                throw new IllegalArgumentException("class space does not support pattern matching");
            }
            default: {
                throw new IllegalArgumentException("unknown searchType " + searchType);
            }
        }
        if (candidates != null) {
            boolean dirDepthNotFixed;
            boolean bl = dirDepthNotFixed = fullPattern.indexOf(FOLDER_WILDCARD) != -1;
            while (candidates.hasMoreElements()) {
                int dirIndex;
                Object path = candidates.nextElement();
                String currPath = path instanceof String ? this.handleString((String)path) : this.handleURL((URL)path);
                if (!currPath.startsWith(dir) && (dirIndex = currPath.indexOf(dir)) != -1) {
                    currPath = currPath.substring(dirIndex);
                }
                if (currPath.endsWith(FOLDER_SEPARATOR) && (dirDepthNotFixed || StringUtils.countOccurrencesOf((String)currPath, (String)FOLDER_SEPARATOR) < StringUtils.countOccurrencesOf((String)fullPattern, (String)FOLDER_SEPARATOR))) {
                    this.doRetrieveMatchingBundleEntries(bundle, fullPattern, currPath, result, searchType);
                }
                if (!this.getPathMatcher().match(fullPattern, currPath)) continue;
                if (path instanceof URL) {
                    result.add((Resource)new UrlContextResource((URL)path, currPath));
                    continue;
                }
                result.add((Resource)new OsgiBundleResource(bundle, currPath));
            }
        }
    }

    private String handleURL(URL path) {
        return path.getPath();
    }

    private String handleString(String path) {
        return FOLDER_SEPARATOR.concat(path);
    }
}

