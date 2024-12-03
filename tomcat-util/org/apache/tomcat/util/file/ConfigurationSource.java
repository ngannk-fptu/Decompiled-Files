/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import org.apache.tomcat.util.buf.UriUtil;

public interface ConfigurationSource {
    public static final ConfigurationSource DEFAULT = new ConfigurationSource(){
        protected final File userDir = new File(System.getProperty("user.dir"));
        protected final URI userDirUri = this.userDir.toURI();

        @Override
        public Resource getResource(String name) throws IOException {
            if (!UriUtil.isAbsoluteURI(name)) {
                File f = new File(name);
                if (!f.isAbsolute()) {
                    f = new File(this.userDir, name);
                }
                if (f.isFile()) {
                    FileInputStream fis = new FileInputStream(f);
                    return new Resource(fis, f.toURI());
                }
            }
            URI uri = null;
            try {
                uri = this.userDirUri.resolve(name);
            }
            catch (IllegalArgumentException e) {
                throw new FileNotFoundException(name);
            }
            try {
                URL url = uri.toURL();
                return new Resource(url.openConnection().getInputStream(), uri);
            }
            catch (MalformedURLException e) {
                throw new FileNotFoundException(name);
            }
        }

        @Override
        public URI getURI(String name) {
            if (!UriUtil.isAbsoluteURI(name)) {
                File f = new File(name);
                if (!f.isAbsolute()) {
                    f = new File(this.userDir, name);
                }
                if (f.isFile()) {
                    return f.toURI();
                }
            }
            return this.userDirUri.resolve(name);
        }
    };

    default public Resource getServerXml() throws IOException {
        return this.getConfResource("server.xml");
    }

    default public Resource getSharedWebXml() throws IOException {
        return this.getConfResource("web.xml");
    }

    default public Resource getConfResource(String name) throws IOException {
        String fullName = "conf/" + name;
        return this.getResource(fullName);
    }

    public Resource getResource(String var1) throws IOException;

    public URI getURI(String var1);

    public static class Resource
    implements AutoCloseable {
        private final InputStream inputStream;
        private final URI uri;

        public Resource(InputStream inputStream, URI uri) {
            this.inputStream = inputStream;
            this.uri = uri;
        }

        public InputStream getInputStream() {
            return this.inputStream;
        }

        public URI getURI() {
            return this.uri;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public long getLastModified() throws MalformedURLException, IOException {
            URLConnection connection = null;
            try {
                connection = this.uri.toURL().openConnection();
                long l = connection.getLastModified();
                return l;
            }
            finally {
                if (connection != null) {
                    connection.getInputStream().close();
                }
            }
        }

        @Override
        public void close() throws IOException {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        }
    }
}

