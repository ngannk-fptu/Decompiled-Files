/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.catalina.TrackedWebResource;
import org.apache.catalina.WebResourceRoot;

class TrackedInputStream
extends InputStream
implements TrackedWebResource {
    private final WebResourceRoot root;
    private final String name;
    private final InputStream is;
    private final Exception creation;

    TrackedInputStream(WebResourceRoot root, String name, InputStream is) {
        this.root = root;
        this.name = name;
        this.is = is;
        this.creation = new Exception();
        root.registerTrackedResource(this);
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
        this.root.deregisterTrackedResource(this);
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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Exception getCreatedBy() {
        return this.creation;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        sw.append('[');
        sw.append(this.name);
        sw.append(']');
        sw.append(System.lineSeparator());
        this.creation.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}

