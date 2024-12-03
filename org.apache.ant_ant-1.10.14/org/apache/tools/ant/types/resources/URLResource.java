/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.util.FileUtils;

public class URLResource
extends Resource
implements URLProvider {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final int NULL_URL = Resource.getMagicNumber("null URL".getBytes());
    private URL url;
    private URLConnection conn;
    private URL baseURL;
    private String relPath;

    public URLResource() {
    }

    public URLResource(URL u) {
        this.setURL(u);
    }

    public URLResource(URLProvider u) {
        this.setURL(u.getURL());
    }

    public URLResource(File f) {
        this.setFile(f);
    }

    public URLResource(String u) {
        this(URLResource.newURL(u));
    }

    public synchronized void setURL(URL u) {
        this.checkAttributesAllowed();
        this.url = u;
    }

    public synchronized void setFile(File f) {
        try {
            this.setURL(FILE_UTILS.getFileURL(f));
        }
        catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }

    public synchronized void setBaseURL(URL base) {
        this.checkAttributesAllowed();
        if (this.url != null) {
            throw new BuildException("can't define URL and baseURL attribute");
        }
        this.baseURL = base;
    }

    public synchronized void setRelativePath(String r) {
        this.checkAttributesAllowed();
        if (this.url != null) {
            throw new BuildException("can't define URL and relativePath attribute");
        }
        this.relPath = r;
    }

    @Override
    public synchronized URL getURL() {
        if (this.isReference()) {
            return this.getRef().getURL();
        }
        if (this.url == null && this.baseURL != null) {
            if (this.relPath == null) {
                throw new BuildException("must provide relativePath attribute when using baseURL.");
            }
            try {
                this.url = new URL(this.baseURL, this.relPath);
            }
            catch (MalformedURLException e) {
                throw new BuildException(e);
            }
        }
        return this.url;
    }

    @Override
    public synchronized void setRefid(Reference r) {
        if (this.url != null || this.baseURL != null || this.relPath != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public synchronized String getName() {
        if (this.isReference()) {
            return this.getRef().getName();
        }
        String name = this.getURL().getFile();
        return name.isEmpty() ? name : name.substring(1);
    }

    @Override
    public synchronized String toString() {
        return this.isReference() ? this.getRef().toString() : String.valueOf(this.getURL());
    }

    @Override
    public synchronized boolean isExists() {
        if (this.isReference()) {
            return this.getRef().isExists();
        }
        return this.isExists(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized boolean isExists(boolean closeConnection) {
        if (this.getURL() == null) {
            return false;
        }
        try {
            this.connect(3);
            if (this.conn instanceof HttpURLConnection) {
                int sc = ((HttpURLConnection)this.conn).getResponseCode();
                boolean bl = sc < 400;
                return bl;
            }
            if (this.url.getProtocol().startsWith("ftp")) {
                closeConnection = true;
                InputStream in = null;
                try {
                    in = this.conn.getInputStream();
                }
                finally {
                    FileUtils.close(in);
                }
            }
            boolean in = true;
            return in;
        }
        catch (IOException e) {
            boolean bl = false;
            return bl;
        }
        finally {
            if (closeConnection) {
                this.close();
            }
        }
    }

    @Override
    public synchronized long getLastModified() {
        if (this.isReference()) {
            return this.getRef().getLastModified();
        }
        if (!this.isExists(false)) {
            return 0L;
        }
        return this.withConnection(c -> this.conn.getLastModified(), 0L);
    }

    @Override
    public synchronized boolean isDirectory() {
        return this.isReference() ? this.getRef().isDirectory() : this.getName().endsWith("/");
    }

    @Override
    public synchronized long getSize() {
        if (this.isReference()) {
            return this.getRef().getSize();
        }
        if (!this.isExists(false)) {
            return 0L;
        }
        return this.withConnection(c -> this.conn.getContentLength(), -1L);
    }

    @Override
    public synchronized boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (this.isReference()) {
            return this.getRef().equals(another);
        }
        if (another == null || another.getClass() != this.getClass()) {
            return false;
        }
        URLResource other = (URLResource)another;
        return this.getURL() == null ? other.getURL() == null : this.getURL().equals(other.getURL());
    }

    @Override
    public synchronized int hashCode() {
        if (this.isReference()) {
            return this.getRef().hashCode();
        }
        return MAGIC * (this.getURL() == null ? NULL_URL : this.getURL().hashCode());
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getInputStream();
        }
        this.connect();
        try {
            InputStream inputStream = this.conn.getInputStream();
            return inputStream;
        }
        finally {
            this.conn = null;
        }
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getOutputStream();
        }
        this.connect();
        try {
            OutputStream outputStream = this.conn.getOutputStream();
            return outputStream;
        }
        finally {
            this.conn = null;
        }
    }

    protected void connect() throws IOException {
        this.connect(0);
    }

    protected synchronized void connect(int logLevel) throws IOException {
        URL u = this.getURL();
        if (u == null) {
            throw new BuildException("URL not set");
        }
        if (this.conn == null) {
            try {
                this.conn = u.openConnection();
                this.conn.connect();
            }
            catch (IOException e) {
                this.log(e.toString(), logLevel);
                this.conn = null;
                throw e;
            }
        }
    }

    @Override
    protected URLResource getRef() {
        return this.getCheckedRef(URLResource.class);
    }

    private synchronized void close() {
        try {
            FileUtils.close(this.conn);
        }
        finally {
            this.conn = null;
        }
    }

    private static URL newURL(String u) {
        try {
            return new URL(u);
        }
        catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long withConnection(ConnectionUser u, long defaultValue) {
        if (this.conn != null) {
            return u.useConnection(this.conn);
        }
        try {
            this.connect();
            long l = u.useConnection(this.conn);
            this.close();
            return l;
        }
        catch (Throwable throwable) {
            try {
                this.close();
                throw throwable;
            }
            catch (IOException ex) {
                return defaultValue;
            }
        }
    }

    private static interface ConnectionUser {
        public long useConnection(URLConnection var1);
    }
}

