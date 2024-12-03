/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.JaiI18N;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class SeekableOutputStream
extends OutputStream {
    private RandomAccessFile file;

    public SeekableOutputStream(RandomAccessFile file) {
        if (file == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SeekableOutputStream0"));
        }
        this.file = file;
    }

    public void write(int b) throws IOException {
        this.file.write(b);
    }

    public void write(byte[] b) throws IOException {
        this.file.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.file.write(b, off, len);
    }

    public void flush() throws IOException {
        FileDescriptor fd = this.file.getFD();
        if (fd.valid()) {
            fd.sync();
        }
    }

    public void close() throws IOException {
        this.file.close();
    }

    public long getFilePointer() throws IOException {
        return this.file.getFilePointer();
    }

    public void seek(long pos) throws IOException {
        this.file.seek(pos);
    }
}

