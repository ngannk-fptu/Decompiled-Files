/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.webresources.AbstractArchiveResourceSet;
import org.apache.catalina.webresources.AbstractResource;

public abstract class AbstractArchiveResource
extends AbstractResource {
    private final AbstractArchiveResourceSet archiveResourceSet;
    private final String baseUrl;
    private final JarEntry resource;
    private final String codeBaseUrl;
    private final String name;
    private boolean readCerts = false;
    private Certificate[] certificates;

    protected AbstractArchiveResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry, String codeBaseUrl) {
        super(archiveResourceSet.getRoot(), webAppPath);
        int index;
        String internalPath;
        this.archiveResourceSet = archiveResourceSet;
        this.baseUrl = baseUrl;
        this.resource = jarEntry;
        this.codeBaseUrl = codeBaseUrl;
        String resourceName = this.resource.getName();
        if (resourceName.charAt(resourceName.length() - 1) == '/') {
            resourceName = resourceName.substring(0, resourceName.length() - 1);
        }
        this.name = (internalPath = archiveResourceSet.getInternalPath()).length() > 0 && resourceName.equals(internalPath.subSequence(1, internalPath.length())) ? "" : ((index = resourceName.lastIndexOf(47)) == -1 ? resourceName : resourceName.substring(index + 1));
    }

    protected AbstractArchiveResourceSet getArchiveResourceSet() {
        return this.archiveResourceSet;
    }

    protected final String getBase() {
        return this.archiveResourceSet.getBase();
    }

    protected final String getBaseUrl() {
        return this.baseUrl;
    }

    protected final JarEntry getResource() {
        return this.resource;
    }

    @Override
    public long getLastModified() {
        return this.resource.getTime();
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return this.resource.isDirectory();
    }

    @Override
    public boolean isFile() {
        return !this.resource.isDirectory();
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getContentLength() {
        if (this.isDirectory()) {
            return -1L;
        }
        return this.resource.getSize();
    }

    @Override
    public String getCanonicalPath() {
        return null;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public long getCreation() {
        return this.resource.getTime();
    }

    @Override
    public URL getURL() {
        String url = this.baseUrl + URLEncoder.DEFAULT.encode(this.resource.getName(), StandardCharsets.UTF_8);
        try {
            return new URI(url).toURL();
        }
        catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)sm.getString("fileResource.getUrlFail", new Object[]{url}), (Throwable)e);
            }
            return null;
        }
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URI(this.codeBaseUrl).toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)sm.getString("fileResource.getUrlFail", new Object[]{this.codeBaseUrl}), (Throwable)e);
            }
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public final byte[] getContent() {
        long len = this.getContentLength();
        if (len > Integer.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("abstractResource.getContentTooLarge", new Object[]{this.getWebappPath(), len}));
        }
        if (len < 0L) {
            return null;
        }
        int size = (int)len;
        byte[] result = new byte[size];
        try (JarInputStreamWrapper jisw = this.getJarInputStreamWrapper();){
            int n;
            if (jisw == null) {
                byte[] byArray = null;
                return byArray;
            }
            for (int pos = 0; pos < size && (n = jisw.read(result, pos, size - pos)) >= 0; pos += n) {
            }
            this.certificates = jisw.getCertificates();
            this.readCerts = true;
            return result;
        }
        catch (IOException ioe) {
            if (!this.getLog().isDebugEnabled()) return null;
            this.getLog().debug((Object)sm.getString("abstractResource.getContentFail", new Object[]{this.getWebappPath()}), (Throwable)ioe);
            return null;
        }
    }

    @Override
    public Certificate[] getCertificates() {
        if (!this.readCerts) {
            throw new IllegalStateException();
        }
        return this.certificates;
    }

    @Override
    public Manifest getManifest() {
        return this.archiveResourceSet.getManifest();
    }

    @Override
    protected final InputStream doGetInputStream() {
        if (this.isDirectory()) {
            return null;
        }
        return this.getJarInputStreamWrapper();
    }

    protected abstract JarInputStreamWrapper getJarInputStreamWrapper();

    protected class JarInputStreamWrapper
    extends InputStream {
        private final JarEntry jarEntry;
        private final InputStream is;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        public JarInputStreamWrapper(JarEntry jarEntry, InputStream is) {
            this.jarEntry = jarEntry;
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            return this.is.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.is.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.is.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.is.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.is.available();
        }

        @Override
        public void close() throws IOException {
            if (this.closed.compareAndSet(false, true)) {
                AbstractArchiveResource.this.archiveResourceSet.closeJarFile();
            }
            this.is.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.is.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.is.reset();
        }

        @Override
        public boolean markSupported() {
            return this.is.markSupported();
        }

        public Certificate[] getCertificates() {
            return this.jarEntry.getCertificates();
        }
    }
}

