/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class StrutsJarURLConnection
extends URLConnection
implements AutoCloseable {
    private static final String FILE_URL_PREFIX = "file:";
    private JarURLConnection jarURLConnection;
    private JarFile jarFile;
    private String entryName;
    private URL jarFileURL;

    private StrutsJarURLConnection(URL url) throws IOException {
        super(url);
        URLConnection conn = this.url.openConnection();
        if (conn instanceof JarURLConnection) {
            this.jarURLConnection = (JarURLConnection)conn;
        } else {
            try {
                conn.getInputStream().close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.parseSpecs(url);
        }
    }

    private void parseSpecs(URL url) throws MalformedURLException, UnsupportedEncodingException {
        String spec = url.getFile();
        int separator = spec.indexOf("!/");
        if (separator == -1) {
            throw new MalformedURLException("no !/ found in url spec:" + spec);
        }
        String jarFileSpec = spec.substring(0, separator++);
        try {
            this.jarFileURL = new URL(jarFileSpec);
        }
        catch (MalformedURLException e) {
            if (!jarFileSpec.startsWith("/")) {
                jarFileSpec = "/" + jarFileSpec;
            }
            this.jarFileURL = new URL(FILE_URL_PREFIX + jarFileSpec);
        }
        this.entryName = null;
        if (++separator != spec.length()) {
            this.entryName = spec.substring(separator, spec.length());
            this.entryName = URLDecoder.decode(this.entryName, "UTF-8");
        }
    }

    @Override
    public void connect() throws IOException {
        if (this.connected) {
            return;
        }
        if (this.jarURLConnection != null) {
            this.connected = true;
            return;
        }
        try (final InputStream in = this.jarFileURL.openConnection().getInputStream();){
            this.jarFile = AccessController.doPrivileged(new PrivilegedExceptionAction<JarFile>(){

                @Override
                public JarFile run() throws IOException {
                    Path tmpFile = Files.createTempFile("jar_cache", null, new FileAttribute[0]);
                    try {
                        JarFile jarFile;
                        Files.copy(in, tmpFile, StandardCopyOption.REPLACE_EXISTING);
                        JarFile jarFile2 = jarFile = new JarFile(tmpFile.toFile(), true, 5);
                        return jarFile2;
                    }
                    catch (Throwable thr) {
                        try {
                            Files.delete(tmpFile);
                        }
                        catch (IOException ioe) {
                            thr.addSuppressed(ioe);
                        }
                        throw thr;
                    }
                    finally {
                        in.close();
                    }
                }
            });
            this.connected = true;
        }
        catch (PrivilegedActionException pae) {
            throw (IOException)pae.getException();
        }
    }

    JarEntry getJarEntry() throws IOException {
        if (this.jarURLConnection != null) {
            return this.jarURLConnection.getJarEntry();
        }
        this.connect();
        return this.jarFile.getJarEntry(this.entryName);
    }

    @Override
    public void setUseCaches(boolean usecaches) {
        super.setUseCaches(usecaches);
        if (this.jarURLConnection != null) {
            this.jarURLConnection.setUseCaches(usecaches);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.jarURLConnection != null) {
            return this.jarURLConnection.getInputStream();
        }
        return this.jarFile.getInputStream(this.jarFile.getJarEntry(this.entryName));
    }

    @Override
    public void close() throws Exception {
        try {
            this.getInputStream().close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (this.jarURLConnection == null) {
            try {
                this.jarFile.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    static StrutsJarURLConnection openConnection(URL url) throws IOException {
        return new StrutsJarURLConnection(url);
    }
}

