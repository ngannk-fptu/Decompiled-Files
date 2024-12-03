/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io.impl;

import com.mchange.io.ReadOnlyMemoryFile;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LazyReadOnlyMemoryFileImpl
implements ReadOnlyMemoryFile {
    File file;
    byte[] bytes = null;
    long last_mod = -1L;
    int last_len = -1;

    public LazyReadOnlyMemoryFileImpl(File file) {
        this.file = file;
    }

    public LazyReadOnlyMemoryFileImpl(String string) {
        this(new File(string));
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public synchronized byte[] getBytes() throws IOException {
        this.update();
        return this.bytes;
    }

    void update() throws IOException {
        if (this.file.lastModified() > this.last_mod) {
            if (this.bytes != null) {
                this.last_len = this.bytes.length;
            }
            this.refreshBytes();
        }
    }

    void refreshBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = this.last_len > 0 ? new ByteArrayOutputStream(2 * this.last_len) : new ByteArrayOutputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(this.file));
        int n = ((InputStream)bufferedInputStream).read();
        while (n >= 0) {
            byteArrayOutputStream.write((byte)n);
            n = ((InputStream)bufferedInputStream).read();
        }
        this.bytes = byteArrayOutputStream.toByteArray();
        this.last_mod = this.file.lastModified();
    }
}

