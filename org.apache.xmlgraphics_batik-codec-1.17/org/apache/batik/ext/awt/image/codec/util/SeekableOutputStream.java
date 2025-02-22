/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class SeekableOutputStream
extends OutputStream {
    private RandomAccessFile file;

    public SeekableOutputStream(RandomAccessFile file) {
        if (file == null) {
            throw new IllegalArgumentException("SeekableOutputStream0");
        }
        this.file = file;
    }

    @Override
    public void write(int b) throws IOException {
        this.file.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.file.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.file.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.file.getFD().sync();
    }

    @Override
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

