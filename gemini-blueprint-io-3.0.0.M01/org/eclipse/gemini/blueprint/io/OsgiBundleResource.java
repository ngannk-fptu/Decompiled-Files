/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.core.io.AbstractResource
 *  org.springframework.core.io.ContextResource
 *  org.springframework.core.io.Resource
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ResourceUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import org.eclipse.gemini.blueprint.io.UrlContextResource;
import org.eclipse.gemini.blueprint.io.internal.OsgiResourceUtils;
import org.osgi.framework.Bundle;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

public class OsgiBundleResource
extends AbstractResource
implements ContextResource {
    public static final String BUNDLE_URL_PREFIX = "osgibundle:";
    public static final String BUNDLE_JAR_URL_PREFIX = "osgibundlejar:";
    private static final char PREFIX_SEPARATOR = ':';
    private static final String ABSOLUTE_PATH_PREFIX = "/";
    private final Bundle bundle;
    private final String path;
    private final String pathWithoutPrefix;
    private int searchType = 0;

    public OsgiBundleResource(Bundle bundle, String path) {
        Assert.notNull((Object)bundle, (String)"Bundle must not be null");
        this.bundle = bundle;
        Assert.notNull((Object)path, (String)"Path must not be null");
        this.path = StringUtils.cleanPath((String)path);
        this.searchType = OsgiResourceUtils.getSearchType(this.path);
        switch (this.searchType) {
            case 0: {
                this.pathWithoutPrefix = path;
                break;
            }
            case 16: {
                this.pathWithoutPrefix = path.substring(BUNDLE_URL_PREFIX.length());
                break;
            }
            case 1: {
                this.pathWithoutPrefix = path.substring(BUNDLE_JAR_URL_PREFIX.length());
                break;
            }
            case 256: {
                this.pathWithoutPrefix = path.substring("classpath:".length());
                break;
            }
            default: {
                this.pathWithoutPrefix = null;
            }
        }
    }

    final String getPath() {
        return this.path;
    }

    final Bundle getBundle() {
        return this.bundle;
    }

    public InputStream getInputStream() throws IOException {
        URLConnection con = this.getURL().openConnection();
        con.setUseCaches(false);
        return con.getInputStream();
    }

    public URL getURL() throws IOException {
        ContextResource res = null;
        URL url = null;
        switch (this.searchType) {
            case 0: {
                res = this.getResourceFromBundleSpace(this.pathWithoutPrefix);
                break;
            }
            case 16: {
                res = this.getResourceFromBundleSpace(this.pathWithoutPrefix);
                break;
            }
            case 1: {
                url = this.getResourceFromBundleJar(this.pathWithoutPrefix);
                break;
            }
            case 256: {
                url = this.getResourceFromBundleClasspath(this.pathWithoutPrefix);
                break;
            }
            default: {
                url = new URL(this.path);
            }
        }
        if (res != null) {
            url = res.getURL();
        }
        if (url == null) {
            throw new FileNotFoundException(this.getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    ContextResource getResourceFromBundleSpace(String bundlePath) throws IOException {
        Object[] res = this.getAllUrlsFromBundleSpace(bundlePath);
        return ObjectUtils.isEmpty((Object[])res) ? null : res[0];
    }

    URL getResourceFromBundleJar(String bundlePath) throws IOException {
        return this.bundle.getEntry(bundlePath);
    }

    URL getResourceFromBundleClasspath(String bundlePath) {
        return this.bundle.getResource(bundlePath);
    }

    boolean isRelativePath(String locationPath) {
        return locationPath.indexOf(58) == -1 && !locationPath.startsWith(ABSOLUTE_PATH_PREFIX);
    }

    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath((String)this.path, (String)relativePath);
        return new OsgiBundleResource(this.bundle, pathToUse);
    }

    public String getFilename() {
        return StringUtils.getFilename((String)this.path);
    }

    public File getFile() throws IOException {
        if (this.searchType != -1) {
            File file;
            String bundleLocation = this.bundle.getLocation();
            int prefixIndex = bundleLocation.indexOf("file:");
            if (prefixIndex > -1) {
                bundleLocation = bundleLocation.substring(prefixIndex + "file:".length());
            }
            if ((file = new File(bundleLocation, this.path)).exists()) {
                return file;
            }
        }
        try {
            return ResourceUtils.getFile((URI)this.getURI(), (String)this.getDescription());
        }
        catch (IOException ioe) {
            throw (IOException)new FileNotFoundException(this.getDescription() + " cannot be resolved to absolute file path").initCause(ioe);
        }
    }

    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        buf.append("OSGi resource[");
        buf.append(this.path);
        buf.append("|bnd.id=");
        buf.append(this.bundle.getBundleId());
        buf.append("|bnd.sym=");
        buf.append(this.bundle.getSymbolicName());
        buf.append("]");
        return buf.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof OsgiBundleResource) {
            OsgiBundleResource otherRes = (OsgiBundleResource)((Object)obj);
            return this.path.equals(otherRes.path) && ObjectUtils.nullSafeEquals((Object)this.bundle, (Object)otherRes.bundle);
        }
        return false;
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public long lastModified() throws IOException {
        URLConnection con = this.getURL().openConnection();
        con.setUseCaches(false);
        long time = con.getLastModified();
        if (time == 0L && 1 == this.searchType) {
            return this.bundle.getLastModified();
        }
        return time;
    }

    int getSearchType() {
        return this.searchType;
    }

    ContextResource[] getAllUrlsFromBundleSpace(String location) throws IOException {
        if (this.bundle == null) {
            throw new IllegalArgumentException("cannot locate items in bundle-space w/o a bundle; specify one when creating this resolver");
        }
        Assert.notNull((Object)location);
        LinkedHashSet<UrlContextResource> resources = new LinkedHashSet<UrlContextResource>(5);
        location = StringUtils.cleanPath((String)location);
        location = OsgiResourceUtils.stripPrefix(location);
        if (!StringUtils.hasText((String)location)) {
            location = ABSOLUTE_PATH_PREFIX;
        }
        if (ABSOLUTE_PATH_PREFIX.equals(location)) {
            Enumeration candidates = this.bundle.findEntries(ABSOLUTE_PATH_PREFIX, null, false);
            while (candidates != null && candidates.hasMoreElements()) {
                URL url = (URL)candidates.nextElement();
                String rootPath = OsgiResourceUtils.findUpperFolder(url.toExternalForm());
                resources.add(new UrlContextResource(rootPath));
            }
        } else {
            if (location.startsWith(ABSOLUTE_PATH_PREFIX)) {
                location = location.substring(1);
            }
            if (location.endsWith(ABSOLUTE_PATH_PREFIX)) {
                location = location.substring(0, location.length() - 1);
            }
            boolean hasFolder = location.indexOf(ABSOLUTE_PATH_PREFIX) != -1;
            String path = hasFolder ? location : ABSOLUTE_PATH_PREFIX;
            String file = hasFolder ? null : location;
            int separatorIndex = location.lastIndexOf(ABSOLUTE_PATH_PREFIX);
            if (separatorIndex > -1 && separatorIndex + 1 < location.length()) {
                path = location.substring(0, separatorIndex);
                if (separatorIndex + 1 < location.length()) {
                    file = location.substring(separatorIndex + 1);
                }
            }
            Enumeration candidates = this.bundle.findEntries(path, file, false);
            String contextPath = ABSOLUTE_PATH_PREFIX + location;
            while (candidates != null && candidates.hasMoreElements()) {
                resources.add(new UrlContextResource((URL)candidates.nextElement(), contextPath));
            }
        }
        return resources.toArray(new ContextResource[resources.size()]);
    }

    public String getPathWithinContext() {
        return this.pathWithoutPrefix;
    }

    public boolean exists() {
        try {
            InputStream is = this.getInputStream();
            is.close();
            return true;
        }
        catch (Throwable isEx) {
            return false;
        }
    }
}

