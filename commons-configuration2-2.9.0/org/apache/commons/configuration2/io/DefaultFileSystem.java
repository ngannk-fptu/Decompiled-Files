/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.io.URLConnectionOptions;
import org.apache.commons.configuration2.io.VerifiableOutputStream;

public class DefaultFileSystem
extends FileSystem {
    @Override
    public InputStream getInputStream(URL url) throws ConfigurationException {
        return this.getInputStream(url, null);
    }

    @Override
    public InputStream getInputStream(URL url, URLConnectionOptions urlConnectionOptions) throws ConfigurationException {
        File file = FileLocatorUtils.fileFromURL(url);
        if (file != null && file.isDirectory()) {
            throw new ConfigurationException("Cannot load a configuration from a directory");
        }
        try {
            return urlConnectionOptions == null ? url.openStream() : urlConnectionOptions.openConnection(url).getInputStream();
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to load the configuration from the URL " + url, e);
        }
    }

    @Override
    public OutputStream getOutputStream(URL url) throws ConfigurationException {
        File file = FileLocatorUtils.fileFromURL(url);
        if (file != null) {
            return this.getOutputStream(file);
        }
        try {
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection conn = (HttpURLConnection)connection;
                conn.setRequestMethod("PUT");
            }
            OutputStream out = connection.getOutputStream();
            if (connection instanceof HttpURLConnection) {
                out = new HttpOutputStream(out, (HttpURLConnection)connection);
            }
            return out;
        }
        catch (IOException e) {
            throw new ConfigurationException("Could not save to URL " + url, e);
        }
    }

    @Override
    public OutputStream getOutputStream(File file) throws ConfigurationException {
        try {
            this.createPath(file);
            return new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            throw new ConfigurationException("Unable to save to file " + file, e);
        }
    }

    @Override
    public String getPath(File file, URL url, String basePath, String fileName) {
        String path;
        block6: {
            path = null;
            if (file != null) {
                path = file.getAbsolutePath();
            }
            if (path == null) {
                if (url != null) {
                    path = url.getPath();
                } else {
                    try {
                        path = this.getURL(basePath, fileName).getPath();
                    }
                    catch (Exception e) {
                        if (!this.getLogger().isDebugEnabled()) break block6;
                        this.getLogger().debug(String.format("Could not determine URL for basePath = %s, fileName = %s: %s", basePath, fileName, e));
                    }
                }
            }
        }
        return path;
    }

    @Override
    public String getBasePath(String path) {
        try {
            URL url = this.getURL(null, path);
            return FileLocatorUtils.getBasePath(url);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getFileName(String path) {
        try {
            URL url = this.getURL(null, path);
            return FileLocatorUtils.getFileName(url);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public URL getURL(String basePath, String file) throws MalformedURLException {
        File f = new File(file);
        if (f.isAbsolute()) {
            return FileLocatorUtils.toURL(f);
        }
        try {
            if (basePath == null) {
                return new URL(file);
            }
            URL base = new URL(basePath);
            return new URL(base, file);
        }
        catch (MalformedURLException uex) {
            return FileLocatorUtils.toURL(FileLocatorUtils.constructFile(basePath, file));
        }
    }

    @Override
    public URL locateFromURL(String basePath, String fileName) {
        try {
            if (basePath == null) {
                return new URL(fileName);
            }
            URL baseURL = new URL(basePath);
            URL url = new URL(baseURL, fileName);
            try (InputStream in = url.openStream();){
                in.available();
            }
            return url;
        }
        catch (IOException e) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Could not locate file " + fileName + " at " + basePath + ": " + e.getMessage());
            }
            return null;
        }
    }

    private void createPath(File file) throws ConfigurationException {
        File parent;
        if (!(file == null || file.exists() || (parent = file.getParentFile()) == null || parent.exists() || parent.mkdirs())) {
            throw new ConfigurationException("Cannot create path: " + parent);
        }
    }

    private static class HttpOutputStream
    extends VerifiableOutputStream {
        private final OutputStream stream;
        private final HttpURLConnection connection;

        public HttpOutputStream(OutputStream stream, HttpURLConnection connection) {
            this.stream = stream;
            this.connection = connection;
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this.stream.write(bytes);
        }

        @Override
        public void write(byte[] bytes, int i, int i1) throws IOException {
            this.stream.write(bytes, i, i1);
        }

        @Override
        public void flush() throws IOException {
            this.stream.flush();
        }

        @Override
        public void close() throws IOException {
            this.stream.close();
        }

        @Override
        public void write(int i) throws IOException {
            this.stream.write(i);
        }

        public String toString() {
            return this.stream.toString();
        }

        @Override
        public void verify() throws IOException {
            if (this.connection.getResponseCode() >= 400) {
                throw new IOException("HTTP Error " + this.connection.getResponseCode() + " " + this.connection.getResponseMessage());
            }
        }
    }
}

