/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.URLHandlers;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWiring;

class URLHandlersBundleURLConnection
extends URLConnection {
    private Felix m_framework;
    private BundleRevision m_targetRevision;
    private int m_classPathIdx = -1;
    private long m_contentLength;
    private long m_contentTime;
    private String m_contentType;
    private InputStream m_is;
    private final String m_path;

    public URLHandlersBundleURLConnection(URL url, Felix framework) throws IOException {
        super(url);
        Object tmp;
        String urlString = url.toExternalForm();
        String path = urlString.substring(urlString.indexOf(url.getPath()));
        if (path == null || path.length() == 0 || path.equals("/")) {
            throw new IOException("Resource does not exist: " + url);
        }
        this.m_framework = framework;
        if (this.m_framework == null && (tmp = URLHandlers.getFrameworkFromContext(Util.getFrameworkUUIDFromURL(url.getHost()))) instanceof Felix) {
            this.m_framework = (Felix)tmp;
        }
        if (this.m_framework == null) {
            throw new IOException("Unable to find framework for URL: " + url);
        }
        long bundleId = Util.getBundleIdFromRevisionId(Util.getRevisionIdFromURL(url.getHost()));
        Bundle bundle = this.m_framework.getBundle(bundleId);
        if (bundle == null) {
            throw new IOException("No bundle associated with resource: " + url);
        }
        BundleRevisions revisions = bundle.adapt(BundleRevisions.class);
        if (revisions == null || revisions.getRevisions().isEmpty()) {
            throw new IOException("Resource does not exist: " + url);
        }
        for (BundleRevision br : revisions.getRevisions()) {
            if (!((BundleRevisionImpl)br).getId().equals(url.getHost())) continue;
            this.m_targetRevision = br;
            break;
        }
        if (this.m_targetRevision == null) {
            this.m_targetRevision = revisions.getRevisions().get(0);
        }
        this.m_classPathIdx = url.getPort();
        if (this.m_classPathIdx < 0) {
            this.m_classPathIdx = 0;
        }
        if (!((BundleRevisionImpl)this.m_targetRevision).hasInputStream(this.m_classPathIdx, path)) {
            URL newurl;
            BundleWiring wiring = this.m_targetRevision.getWiring();
            ClassLoader cl = wiring != null ? wiring.getClassLoader() : null;
            URL uRL = newurl = cl != null ? cl.getResource(path) : null;
            if (newurl == null) {
                if (!"runtime".equals(url.getRef())) {
                    throw new IOException("Resource does not exist: " + url);
                }
                path = url.getPath();
                if (path == null || path.length() == 0 || path.equals("/")) {
                    throw new IOException("Resource does not exist: " + url);
                }
                if (!((BundleRevisionImpl)this.m_targetRevision).hasInputStream(this.m_classPathIdx, path)) {
                    URL uRL2 = newurl = cl != null ? cl.getResource(path) : null;
                    if (newurl == null) {
                        throw new IOException("Resource does not exist: " + url);
                    }
                    this.m_classPathIdx = newurl.getPort();
                }
            } else {
                this.m_classPathIdx = newurl.getPort();
            }
        }
        this.m_path = path;
    }

    @Override
    public synchronized void connect() throws IOException {
        if (!this.connected) {
            if (this.m_targetRevision == null || this.m_classPathIdx < 0) {
                throw new IOException("Resource does not exist: " + this.url);
            }
            this.m_is = ((BundleRevisionImpl)this.m_targetRevision).getInputStream(this.m_classPathIdx, this.m_path);
            this.m_contentLength = this.m_is == null ? 0L : (long)this.m_is.available();
            this.m_contentTime = ((BundleRevisionImpl)this.m_targetRevision).getContentTime(this.m_classPathIdx, this.m_path);
            this.m_contentType = URLConnection.guessContentTypeFromName(this.m_path);
            this.connected = true;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        this.connect();
        return this.m_is;
    }

    @Override
    public int getContentLength() {
        return (int)this.getContentLengthLong();
    }

    @Override
    public long getContentLengthLong() {
        try {
            this.connect();
        }
        catch (IOException ex) {
            return -1L;
        }
        return this.m_contentLength;
    }

    @Override
    public long getLastModified() {
        try {
            this.connect();
        }
        catch (IOException ex) {
            return 0L;
        }
        if (this.m_contentTime != -1L) {
            return this.m_contentTime;
        }
        return 0L;
    }

    @Override
    public String getContentType() {
        try {
            this.connect();
        }
        catch (IOException ex) {
            return null;
        }
        return this.m_contentType;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    URL getLocalURL() {
        if (this.m_targetRevision == null || this.m_classPathIdx < 0) {
            return this.url;
        }
        return ((BundleRevisionImpl)this.m_targetRevision).getLocalURL(this.m_classPathIdx, this.m_path);
    }
}

