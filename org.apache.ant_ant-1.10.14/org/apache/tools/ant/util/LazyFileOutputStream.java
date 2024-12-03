/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.util.FileUtils;

public class LazyFileOutputStream
extends OutputStream {
    private OutputStream fos;
    private File file;
    private boolean append;
    private boolean alwaysCreate;
    private boolean opened = false;
    private boolean closed = false;

    public LazyFileOutputStream(String name) {
        this(name, false);
    }

    public LazyFileOutputStream(String name, boolean append) {
        this(new File(name), append);
    }

    public LazyFileOutputStream(File f) {
        this(f, false);
    }

    public LazyFileOutputStream(File file, boolean append) {
        this(file, append, false);
    }

    public LazyFileOutputStream(File file, boolean append, boolean alwaysCreate) {
        this.file = file;
        this.append = append;
        this.alwaysCreate = alwaysCreate;
    }

    public void open() throws IOException {
        this.ensureOpened();
    }

    @Override
    public synchronized void close() throws IOException {
        if (this.alwaysCreate && !this.closed) {
            this.ensureOpened();
        }
        if (this.opened) {
            this.fos.close();
        }
        this.closed = true;
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public synchronized void write(byte[] b, int offset, int len) throws IOException {
        this.ensureOpened();
        this.fos.write(b, offset, len);
    }

    @Override
    public synchronized void write(int b) throws IOException {
        this.ensureOpened();
        this.fos.write(b);
    }

    private synchronized void ensureOpened() throws IOException {
        if (this.closed) {
            throw new IOException(this.file + " has already been closed.");
        }
        if (!this.opened) {
            this.fos = FileUtils.newOutputStream(this.file.toPath(), this.append);
            this.opened = true;
        }
    }
}

