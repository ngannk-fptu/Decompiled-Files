/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.sling.api.SlingException
 *  org.apache.sling.api.resource.Resource
 *  org.apache.sling.api.resource.ResourceMetadata
 *  org.apache.sling.api.resource.ResourceResolver
 *  org.apache.sling.commons.classloader.ClassLoaderWriter
 *  org.apache.sling.commons.compiler.JavaCompiler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.sling.scripting.jsp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.classloader.ClassLoaderWriter;
import org.apache.sling.commons.compiler.JavaCompiler;
import org.apache.sling.scripting.jsp.jasper.IOProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SlingIOProvider
implements IOProvider {
    private static final String WEB_INF_TAGS = "/WEB-INF/tags";
    private final Logger log = LoggerFactory.getLogger(SlingIOProvider.class);
    private final ThreadLocal<ResourceResolver> requestResourceResolver = new ThreadLocal();
    private final ClassLoaderWriter classLoaderWriter;
    private final JavaCompiler javaCompiler;

    SlingIOProvider(ClassLoaderWriter classLoaderWriter, JavaCompiler compiler) {
        this.classLoaderWriter = classLoaderWriter;
        this.javaCompiler = compiler;
    }

    ResourceResolver setRequestResourceResolver(ResourceResolver resolver) {
        ResourceResolver old = this.requestResourceResolver.get();
        this.requestResourceResolver.set(resolver);
        return old;
    }

    void resetRequestResourceResolver(ResourceResolver resolver) {
        this.requestResourceResolver.set(resolver);
    }

    @Override
    public InputStream getInputStream(String path) throws FileNotFoundException, IOException {
        if (path.startsWith(":")) {
            return this.classLoaderWriter.getInputStream(path.substring(1));
        }
        ResourceResolver resolver = this.requestResourceResolver.get();
        if (resolver != null) {
            try {
                InputStream stream;
                Resource resource = resolver.getResource(this.cleanPath(path, true));
                if (resource != null && (stream = (InputStream)resource.adaptTo(InputStream.class)) != null) {
                    return stream;
                }
            }
            catch (SlingException se) {
                throw (IOException)new IOException("Failed to get InputStream for " + path).initCause(se);
            }
        }
        throw new FileNotFoundException("Cannot find " + path);
    }

    @Override
    public long lastModified(String path) {
        if (path.startsWith(":")) {
            return this.classLoaderWriter.getLastModified(path.substring(1));
        }
        ResourceResolver resolver = this.requestResourceResolver.get();
        if (resolver != null) {
            try {
                Resource resource = resolver.getResource(this.cleanPath(path, true));
                if (resource != null) {
                    ResourceMetadata meta = resource.getResourceMetadata();
                    long modTime = meta.getModificationTime();
                    return modTime > 0L ? modTime : 0L;
                }
            }
            catch (SlingException se) {
                this.log.error("Cannot get last modification time for " + path, (Throwable)se);
            }
        }
        return -1L;
    }

    @Override
    public boolean delete(String path) {
        return this.classLoaderWriter.delete(path.substring(1));
    }

    @Override
    public OutputStream getOutputStream(String path) {
        return this.classLoaderWriter.getOutputStream(path.substring(1));
    }

    @Override
    public boolean rename(String oldFileName, String newFileName) {
        return this.classLoaderWriter.rename(oldFileName.substring(1), newFileName.substring(1));
    }

    @Override
    public boolean mkdirs(String path) {
        return true;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoaderWriter.getClassLoader();
    }

    URL getURL(String path) throws MalformedURLException {
        ResourceResolver resolver = this.requestResourceResolver.get();
        if (resolver != null) {
            try {
                Resource resource = resolver.getResource(this.cleanPath(path, true));
                return resource != null ? (URL)resource.adaptTo(URL.class) : null;
            }
            catch (SlingException se) {
                throw (MalformedURLException)new MalformedURLException("Cannot get URL for " + path).initCause(se);
            }
        }
        return null;
    }

    Set<String> getResourcePaths(String path) {
        HashSet<String> paths = new HashSet<String>();
        ResourceResolver resolver = this.requestResourceResolver.get();
        if (resolver != null) {
            try {
                String cleanedPath = this.cleanPath(path, false);
                boolean startsWithWebInfTags = cleanedPath.startsWith(WEB_INF_TAGS);
                Resource resource = resolver.getResource(startsWithWebInfTags ? cleanedPath.substring(WEB_INF_TAGS.length()) : cleanedPath);
                if (resource != null) {
                    Iterator entries = resolver.listChildren(resource);
                    while (entries.hasNext()) {
                        String entryPath = ((Resource)entries.next()).getPath();
                        if (startsWithWebInfTags) {
                            paths.add(WEB_INF_TAGS + entryPath);
                            continue;
                        }
                        paths.add(entryPath);
                    }
                }
            }
            catch (SlingException se) {
                this.log.warn("getResourcePaths: Cannot list children of " + path, (Throwable)se);
            }
        }
        return paths.isEmpty() ? null : paths;
    }

    private String cleanPath(String path, boolean removeWebInfTags) {
        path = path.replace('\\', '/');
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (removeWebInfTags && path.startsWith(WEB_INF_TAGS)) {
            path = path.substring(WEB_INF_TAGS.length());
        }
        return path;
    }

    @Override
    public JavaCompiler getJavaCompiler() {
        return this.javaCompiler;
    }

    @Override
    public ClassLoaderWriter getClassLoaderWriter() {
        return this.classLoaderWriter;
    }
}

