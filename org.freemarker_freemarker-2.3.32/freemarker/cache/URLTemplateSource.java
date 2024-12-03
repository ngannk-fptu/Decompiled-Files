/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

class URLTemplateSource {
    private final URL url;
    private URLConnection conn;
    private InputStream inputStream;
    private Boolean useCaches;

    URLTemplateSource(URL url, Boolean useCaches) throws IOException {
        this.url = url;
        this.conn = url.openConnection();
        this.useCaches = useCaches;
        if (useCaches != null) {
            this.conn.setUseCaches(useCaches);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof URLTemplateSource) {
            return this.url.equals(((URLTemplateSource)o).url);
        }
        return false;
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public String toString() {
        return this.url.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    long lastModified() {
        if (this.conn instanceof JarURLConnection) {
            URL jarURL = ((JarURLConnection)this.conn).getJarFileURL();
            if (jarURL.getProtocol().equals("file")) {
                return new File(jarURL.getFile()).lastModified();
            }
            URLConnection jarConn = null;
            try {
                jarConn = jarURL.openConnection();
                long l = jarConn.getLastModified();
                return l;
            }
            catch (IOException e) {
                long l = -1L;
                return l;
            }
            finally {
                try {
                    if (jarConn != null) {
                        jarConn.getInputStream().close();
                    }
                }
                catch (IOException iOException) {}
            }
        }
        long lastModified = this.conn.getLastModified();
        if (lastModified == -1L && this.url.getProtocol().equals("file")) {
            return new File(this.url.getFile()).lastModified();
        }
        return lastModified;
    }

    InputStream getInputStream() throws IOException {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.conn = this.url.openConnection();
        }
        this.inputStream = this.conn.getInputStream();
        return this.inputStream;
    }

    void close() throws IOException {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            } else {
                this.conn.getInputStream().close();
            }
        }
        finally {
            this.inputStream = null;
            this.conn = null;
        }
    }

    Boolean getUseCaches() {
        return this.useCaches;
    }

    void setUseCaches(boolean useCaches) {
        if (this.conn != null) {
            this.conn.setUseCaches(useCaches);
            this.useCaches = useCaches;
        }
    }
}

