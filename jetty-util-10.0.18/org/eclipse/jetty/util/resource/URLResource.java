/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.ReadableByteChannel;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLResource
extends Resource {
    private static final Logger LOG = LoggerFactory.getLogger(URLResource.class);
    protected final AutoLock _lock = new AutoLock();
    protected final URL _url;
    protected final String _urlString;
    protected URLConnection _connection;
    protected InputStream _in = null;
    transient boolean _useCaches = Resource.__defaultUseCaches;

    protected URLResource(URL url, URLConnection connection) {
        this._url = url;
        this._urlString = this._url.toExternalForm();
        this._connection = connection;
    }

    protected URLResource(URL url, URLConnection connection, boolean useCaches) {
        this(url, connection);
        this._useCaches = useCaches;
    }

    protected boolean checkConnection() {
        try (AutoLock l = this._lock.lock();){
            if (this._connection == null) {
                try {
                    this._connection = this._url.openConnection();
                    this._connection.setUseCaches(this._useCaches);
                }
                catch (IOException e) {
                    LOG.trace("IGNORED", (Throwable)e);
                }
            }
            boolean bl = this._connection != null;
            return bl;
        }
    }

    @Override
    public void close() {
        try (AutoLock l = this._lock.lock();){
            if (this._in != null) {
                try {
                    this._in.close();
                }
                catch (IOException e) {
                    LOG.trace("IGNORED", (Throwable)e);
                }
                this._in = null;
            }
            if (this._connection != null) {
                this._connection = null;
            }
        }
    }

    @Override
    public boolean exists() {
        try (AutoLock l = this._lock.lock();){
            if (this.checkConnection() && this._in == null) {
                this._in = this._connection.getInputStream();
            }
        }
        catch (IOException e) {
            LOG.trace("IGNORED", (Throwable)e);
        }
        return this._in != null;
    }

    @Override
    public boolean isDirectory() {
        return this.exists() && this._urlString.endsWith("/");
    }

    @Override
    public long lastModified() {
        if (this.checkConnection()) {
            return this._connection.getLastModified();
        }
        return -1L;
    }

    @Override
    public long length() {
        if (this.checkConnection()) {
            return this._connection.getContentLength();
        }
        return -1L;
    }

    @Override
    public URI getURI() {
        try {
            return this._url.toURI();
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return this._url.toExternalForm();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.getInputStream(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected InputStream getInputStream(boolean resetConnection) throws IOException {
        try (AutoLock l = this._lock.lock();){
            InputStream inputStream;
            block18: {
                block16: {
                    InputStream inputStream2;
                    block17: {
                        if (!this.checkConnection()) {
                            throw new IOException("Invalid resource");
                        }
                        try {
                            if (this._in == null) break block16;
                            InputStream in = this._in;
                            this._in = null;
                            inputStream2 = in;
                            if (!resetConnection) break block17;
                            this._connection = null;
                        }
                        catch (Throwable throwable) {
                            if (resetConnection) {
                                this._connection = null;
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Connection nulled");
                                }
                            }
                            throw throwable;
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Connection nulled");
                        }
                    }
                    return inputStream2;
                }
                inputStream = this._connection.getInputStream();
                if (!resetConnection) break block18;
                this._connection = null;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Connection nulled");
                }
            }
            return inputStream;
        }
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return null;
    }

    @Override
    public boolean delete() throws SecurityException {
        throw new SecurityException("Delete not supported");
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        throw new SecurityException("RenameTo not supported");
    }

    @Override
    public String[] list() {
        return null;
    }

    @Override
    public Resource addPath(String path) throws IOException {
        if (URIUtil.canonicalPath(path) == null) {
            throw new MalformedURLException(path);
        }
        return URLResource.newResource(URIUtil.addEncodedPaths(this._url.toExternalForm(), URIUtil.encodePath(path)), this._useCaches);
    }

    public String toString() {
        return this._urlString;
    }

    public int hashCode() {
        return this._urlString.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof URLResource && this._urlString.equals(((URLResource)o)._urlString);
    }

    public boolean getUseCaches() {
        return this._useCaches;
    }

    @Override
    public boolean isContainedIn(Resource containingResource) throws MalformedURLException {
        return false;
    }
}

