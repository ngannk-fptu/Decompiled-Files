/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.classworlds.uberjar.protocol.jar;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class NonLockingJarUrlConnection
extends JarURLConnection {
    private URL baseResource;
    private String[] segments;
    private InputStream in;

    NonLockingJarUrlConnection(URL url) throws IOException {
        url = NonLockingJarUrlConnection.normaliseURL(url);
        super(url);
        String baseText = url.getPath();
        int bangLoc = baseText.indexOf(33);
        String baseResourceText = baseText.substring(0, bangLoc);
        String extraText = "";
        if (bangLoc <= baseText.length() - 2 && baseText.charAt(bangLoc + 1) == '/') {
            extraText = bangLoc + 2 == baseText.length() ? "" : baseText.substring(bangLoc + 1);
        } else {
            throw new MalformedURLException("No !/ in url: " + url.toExternalForm());
        }
        ArrayList<String> segments = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(extraText, "!");
        while (tokens.hasMoreTokens()) {
            segments.add(tokens.nextToken());
        }
        this.segments = segments.toArray(new String[segments.size()]);
        this.baseResource = new URL(baseResourceText);
    }

    protected static URL normaliseURL(URL url) throws MalformedURLException {
        String text = NonLockingJarUrlConnection.normalizeUrlPath(url.toString());
        if (!text.startsWith("jar:")) {
            text = "jar:" + text;
        }
        if (text.indexOf(33) < 0) {
            text = text + "!/";
        }
        return new URL(text);
    }

    protected String[] getSegments() {
        return this.segments;
    }

    protected URL getBaseResource() {
        return this.baseResource;
    }

    @Override
    public void connect() throws IOException {
        if (this.segments.length == 0) {
            this.setupBaseResourceInputStream();
        } else {
            this.setupPathedInputStream();
        }
    }

    protected void setupBaseResourceInputStream() throws IOException {
        this.in = this.getBaseResource().openStream();
    }

    protected void setupPathedInputStream() throws IOException {
        String entryName;
        final JarFile jar = this.getJarFile();
        ZipEntry zipEntry = jar.getEntry(entryName = this.segments[0].substring(1));
        if (zipEntry == null) {
            throw new IOException("Unable to locate entry: " + entryName + ", in JAR file: " + jar.getName());
        }
        final InputStream delegate = jar.getInputStream(zipEntry);
        this.in = new InputStream(){

            @Override
            public int read() throws IOException {
                return delegate.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return delegate.read(b);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return delegate.read(b, off, len);
            }

            @Override
            public long skip(long n) throws IOException {
                return delegate.skip(n);
            }

            @Override
            public int available() throws IOException {
                return delegate.available();
            }

            @Override
            public void close() throws IOException {
                delegate.close();
                jar.close();
            }

            @Override
            public synchronized void mark(int readlimit) {
                delegate.mark(readlimit);
            }

            @Override
            public synchronized void reset() throws IOException {
                delegate.reset();
            }

            @Override
            public boolean markSupported() {
                return delegate.markSupported();
            }
        };
    }

    protected InputStream getSegmentInputStream(InputStream baseIn, String segment) throws IOException {
        JarInputStream jarIn = new JarInputStream(baseIn);
        JarEntry entry = null;
        while (jarIn.available() != 0 && (entry = jarIn.getNextJarEntry()) != null) {
            if (!("/" + entry.getName()).equals(segment)) continue;
            return jarIn;
        }
        throw new IOException("unable to locate segment: " + segment);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.in == null) {
            this.connect();
        }
        return this.in;
    }

    @Override
    public JarFile getJarFile() throws IOException {
        String url = this.baseResource.toExternalForm();
        if (url.startsWith("file:")) {
            url = url.substring(5);
        }
        return new JarFile(URLDecoder.decode(url, "UTF-8"));
    }

    private static String normalizeUrlPath(String name) {
        int i;
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if ((i = name.indexOf("/..")) > 0) {
            int j = name.lastIndexOf(47, i - 1);
            name = name.substring(0, j) + name.substring(i + 3);
        }
        return name;
    }
}

