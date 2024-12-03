/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LazyFileInputStream
extends InputStream {
    private FileInputStream in;
    private FileDescriptor fd;
    private File file;

    public LazyFileInputStream(File file) throws FileNotFoundException {
        if (!file.canRead()) {
            throw new FileNotFoundException(file.getPath());
        }
        this.file = file;
    }

    public LazyFileInputStream(FileDescriptor fdObj) {
        this.fd = fdObj;
    }

    public LazyFileInputStream(String name) throws FileNotFoundException {
        this(new File(name));
    }

    public void open() throws IOException {
        if (this.in == null) {
            if (this.file != null) {
                this.in = new FileInputStream(this.file);
            } else if (this.fd != null) {
                this.in = new FileInputStream(this.fd);
            } else {
                throw new IOException("Stream already closed.");
            }
        }
    }

    @Override
    public int read() throws IOException {
        this.open();
        return this.in.read();
    }

    @Override
    public int available() throws IOException {
        this.open();
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
        }
        this.in = null;
        this.file = null;
        this.fd = null;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.open();
        this.in.reset();
    }

    @Override
    public boolean markSupported() {
        try {
            this.open();
            return this.in.markSupported();
        }
        catch (IOException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            this.open();
            this.in.mark(readlimit);
        }
        catch (IOException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    @Override
    public long skip(long n) throws IOException {
        this.open();
        return this.in.skip(n);
    }

    @Override
    public int read(byte[] b) throws IOException {
        this.open();
        return this.in.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.open();
        return this.in.read(b, off, len);
    }
}

