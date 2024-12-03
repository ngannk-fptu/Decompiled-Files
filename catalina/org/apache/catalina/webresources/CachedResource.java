/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.webresources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.security.Permission;
import java.security.cert.Certificate;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.Cache;
import org.apache.catalina.webresources.EmptyResource;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class CachedResource
implements WebResource {
    private static final Log log = LogFactory.getLog(CachedResource.class);
    private static final StringManager sm = StringManager.getManager(CachedResource.class);
    private static final long CACHE_ENTRY_SIZE = 500L;
    private final Cache cache;
    private final StandardRoot root;
    private final String webAppPath;
    private final long ttl;
    private final int objectMaxSizeBytes;
    private final boolean usesClassLoaderResources;
    private volatile WebResource webResource;
    private volatile WebResource[] webResources;
    private volatile long nextCheck;
    private volatile Long cachedLastModified = null;
    private volatile String cachedLastModifiedHttp = null;
    private volatile byte[] cachedContent = null;
    private volatile Boolean cachedIsFile = null;
    private volatile Boolean cachedIsDirectory = null;
    private volatile Boolean cachedExists = null;
    private volatile Boolean cachedIsVirtual = null;
    private volatile Long cachedContentLength = null;

    public CachedResource(Cache cache, StandardRoot root, String path, long ttl, int objectMaxSizeBytes, boolean usesClassLoaderResources) {
        this.cache = cache;
        this.root = root;
        this.webAppPath = path;
        this.ttl = ttl;
        this.objectMaxSizeBytes = objectMaxSizeBytes;
        this.usesClassLoaderResources = usesClassLoaderResources;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean validateResource(boolean useClassLoaderResources) {
        if (this.usesClassLoaderResources != useClassLoaderResources) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (this.webResource == null) {
            CachedResource cachedResource = this;
            synchronized (cachedResource) {
                if (this.webResource == null) {
                    this.webResource = this.root.getResourceInternal(this.webAppPath, useClassLoaderResources);
                    this.getLastModified();
                    this.getContentLength();
                    this.nextCheck = this.ttl + now;
                    this.cachedExists = this.webResource instanceof EmptyResource ? Boolean.FALSE : Boolean.TRUE;
                    return true;
                }
            }
        }
        if (now < this.nextCheck) {
            return true;
        }
        if (!this.root.isPackedWarFile()) {
            WebResource webResourceInternal = this.root.getResourceInternal(this.webAppPath, useClassLoaderResources);
            if (!this.webResource.exists() && webResourceInternal.exists()) {
                return false;
            }
            if (this.webResource.getLastModified() != this.getLastModified() || this.webResource.getContentLength() != this.getContentLength()) {
                return false;
            }
            if (this.webResource.getLastModified() != webResourceInternal.getLastModified() || this.webResource.getContentLength() != webResourceInternal.getContentLength()) {
                return false;
            }
        }
        this.nextCheck = this.ttl + now;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean validateResources(boolean useClassLoaderResources) {
        long now = System.currentTimeMillis();
        if (this.webResources == null) {
            CachedResource cachedResource = this;
            synchronized (cachedResource) {
                if (this.webResources == null) {
                    this.webResources = this.root.getResourcesInternal(this.webAppPath, useClassLoaderResources);
                    this.nextCheck = this.ttl + now;
                    return true;
                }
            }
        }
        if (now < this.nextCheck) {
            return true;
        }
        if (this.root.isPackedWarFile()) {
            this.nextCheck = this.ttl + now;
            return true;
        }
        return false;
    }

    protected long getNextCheck() {
        return this.nextCheck;
    }

    @Override
    public long getLastModified() {
        if (this.cachedLastModified == null) {
            this.cachedLastModified = this.webResource.getLastModified();
        }
        return this.cachedLastModified;
    }

    @Override
    public String getLastModifiedHttp() {
        if (this.cachedLastModifiedHttp == null) {
            this.cachedLastModifiedHttp = this.webResource.getLastModifiedHttp();
        }
        return this.cachedLastModifiedHttp;
    }

    @Override
    public boolean exists() {
        if (this.cachedExists == null) {
            this.cachedExists = this.webResource.exists();
        }
        return this.cachedExists;
    }

    @Override
    public boolean isVirtual() {
        if (this.cachedIsVirtual == null) {
            this.cachedIsVirtual = this.webResource.isVirtual();
        }
        return this.cachedIsVirtual;
    }

    @Override
    public boolean isDirectory() {
        if (this.cachedIsDirectory == null) {
            this.cachedIsDirectory = this.webResource.isDirectory();
        }
        return this.cachedIsDirectory;
    }

    @Override
    public boolean isFile() {
        if (this.cachedIsFile == null) {
            this.cachedIsFile = this.webResource.isFile();
        }
        return this.cachedIsFile;
    }

    @Override
    public boolean delete() {
        boolean deleteResult = this.webResource.delete();
        if (deleteResult) {
            this.cache.removeCacheEntry(this.webAppPath);
        }
        return deleteResult;
    }

    @Override
    public String getName() {
        return this.webResource.getName();
    }

    @Override
    public long getContentLength() {
        if (this.cachedContentLength == null) {
            long result = 0L;
            if (this.webResource != null) {
                result = this.webResource.getContentLength();
                this.cachedContentLength = result;
            }
            return result;
        }
        return this.cachedContentLength;
    }

    @Override
    public String getCanonicalPath() {
        return this.webResource.getCanonicalPath();
    }

    @Override
    public boolean canRead() {
        return this.webResource.canRead();
    }

    @Override
    public String getWebappPath() {
        return this.webAppPath;
    }

    @Override
    public String getETag() {
        return this.webResource.getETag();
    }

    @Override
    public void setMimeType(String mimeType) {
        this.webResource.setMimeType(mimeType);
    }

    @Override
    public String getMimeType() {
        return this.webResource.getMimeType();
    }

    @Override
    public InputStream getInputStream() {
        byte[] content = this.getContent();
        if (content == null) {
            return this.webResource.getInputStream();
        }
        return new ByteArrayInputStream(content);
    }

    @Override
    public byte[] getContent() {
        if (this.cachedContent == null) {
            if (this.getContentLength() > (long)this.objectMaxSizeBytes) {
                return null;
            }
            this.cachedContent = this.webResource.getContent();
        }
        return this.cachedContent;
    }

    @Override
    public long getCreation() {
        return this.webResource.getCreation();
    }

    @Override
    public URL getURL() {
        URL resourceURL = this.webResource.getURL();
        if (resourceURL == null) {
            return null;
        }
        try {
            CachedResourceURLStreamHandler handler = new CachedResourceURLStreamHandler(resourceURL, this.root, this.webAppPath, this.usesClassLoaderResources);
            URL result = new URL(null, resourceURL.toExternalForm(), handler);
            handler.setCacheURL(result);
            return result;
        }
        catch (MalformedURLException e) {
            log.error((Object)sm.getString("cachedResource.invalidURL", new Object[]{resourceURL.toExternalForm()}), (Throwable)e);
            return null;
        }
    }

    @Override
    public URL getCodeBase() {
        return this.webResource.getCodeBase();
    }

    @Override
    public Certificate[] getCertificates() {
        return this.webResource.getCertificates();
    }

    @Override
    public Manifest getManifest() {
        return this.webResource.getManifest();
    }

    @Override
    public WebResourceRoot getWebResourceRoot() {
        return this.webResource.getWebResourceRoot();
    }

    WebResource getWebResource() {
        return this.webResource;
    }

    WebResource[] getWebResources() {
        return this.webResources;
    }

    boolean usesClassLoaderResources() {
        return this.usesClassLoaderResources;
    }

    long getSize() {
        long result = 500L;
        result += (long)(this.getWebappPath().length() * 2);
        if (this.getContentLength() <= (long)this.objectMaxSizeBytes) {
            result += this.getContentLength();
        }
        return result;
    }

    private static InputStream buildInputStream(String[] files) {
        Arrays.sort(files, Collator.getInstance(Locale.getDefault()));
        StringBuilder result = new StringBuilder();
        for (String file : files) {
            result.append(file);
            result.append('\n');
        }
        return new ByteArrayInputStream(result.toString().getBytes(Charset.defaultCharset()));
    }

    private static class CachedResourceURLStreamHandler
    extends URLStreamHandler {
        private final URL resourceURL;
        private final StandardRoot root;
        private final String webAppPath;
        private final boolean usesClassLoaderResources;
        private URL cacheURL = null;

        CachedResourceURLStreamHandler(URL resourceURL, StandardRoot root, String webAppPath, boolean usesClassLoaderResources) {
            this.resourceURL = resourceURL;
            this.root = root;
            this.webAppPath = webAppPath;
            this.usesClassLoaderResources = usesClassLoaderResources;
        }

        protected void setCacheURL(URL cacheURL) {
            this.cacheURL = cacheURL;
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            URI constructedURI;
            if (this.cacheURL != null && u == this.cacheURL) {
                if ("jar".equals(this.cacheURL.getProtocol())) {
                    return new CachedResourceJarURLConnection(this.resourceURL, this.root, this.webAppPath, this.usesClassLoaderResources);
                }
                return new CachedResourceURLConnection(this.resourceURL, this.root, this.webAppPath, this.usesClassLoaderResources);
            }
            try {
                constructedURI = new URI(u.toExternalForm());
            }
            catch (URISyntaxException e) {
                throw new IOException(e);
            }
            URL constructedURL = constructedURI.toURL();
            return constructedURL.openConnection();
        }

        @Override
        protected boolean equals(URL u1, URL u2) {
            if (this.cacheURL == u1) {
                return this.resourceURL.equals(u2);
            }
            return super.equals(u1, u2);
        }

        @Override
        protected int hashCode(URL u) {
            if (this.cacheURL == u) {
                return this.resourceURL.hashCode();
            }
            return super.hashCode(u);
        }
    }

    private static class CachedResourceJarURLConnection
    extends JarURLConnection {
        private final StandardRoot root;
        private final String webAppPath;
        private final boolean usesClassLoaderResources;
        private final URL resourceURL;

        protected CachedResourceJarURLConnection(URL resourceURL, StandardRoot root, String webAppPath, boolean usesClassLoaderResources) throws IOException {
            super(resourceURL);
            this.root = root;
            this.webAppPath = webAppPath;
            this.usesClassLoaderResources = usesClassLoaderResources;
            this.resourceURL = resourceURL;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {
            WebResource resource = this.getResource();
            if (resource.isDirectory()) {
                return CachedResource.buildInputStream(resource.getWebResourceRoot().list(this.webAppPath));
            }
            return this.getResource().getInputStream();
        }

        @Override
        public Permission getPermission() throws IOException {
            return this.resourceURL.openConnection().getPermission();
        }

        @Override
        public long getLastModified() {
            return this.getResource().getLastModified();
        }

        @Override
        public long getContentLengthLong() {
            return this.getResource().getContentLength();
        }

        private WebResource getResource() {
            return this.root.getResource(this.webAppPath, false, this.usesClassLoaderResources);
        }

        @Override
        public JarFile getJarFile() throws IOException {
            return ((JarURLConnection)this.resourceURL.openConnection()).getJarFile();
        }
    }

    private static class CachedResourceURLConnection
    extends URLConnection {
        private final StandardRoot root;
        private final String webAppPath;
        private final boolean usesClassLoaderResources;
        private final URL resourceURL;

        protected CachedResourceURLConnection(URL resourceURL, StandardRoot root, String webAppPath, boolean usesClassLoaderResources) {
            super(resourceURL);
            this.root = root;
            this.webAppPath = webAppPath;
            this.usesClassLoaderResources = usesClassLoaderResources;
            this.resourceURL = resourceURL;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {
            WebResource resource = this.getResource();
            if (resource.isDirectory()) {
                return CachedResource.buildInputStream(resource.getWebResourceRoot().list(this.webAppPath));
            }
            return this.getResource().getInputStream();
        }

        @Override
        public Permission getPermission() throws IOException {
            return this.resourceURL.openConnection().getPermission();
        }

        @Override
        public long getLastModified() {
            return this.getResource().getLastModified();
        }

        @Override
        public long getContentLengthLong() {
            return this.getResource().getContentLength();
        }

        private WebResource getResource() {
            return this.root.getResource(this.webAppPath, false, this.usesClassLoaderResources);
        }
    }
}

