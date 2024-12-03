/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.pdfbox.io.RandomAccess;

public class RandomAccessFile
implements RandomAccess {
    private final java.io.RandomAccessFile ras;
    private boolean isClosed;

    public RandomAccessFile(File file, String mode) throws FileNotFoundException {
        this.ras = new java.io.RandomAccessFile(file, mode);
    }

    @Override
    public void close() throws IOException {
        this.ras.close();
        this.isClosed = true;
    }

    @Override
    public void clear() throws IOException {
        this.checkClosed();
        this.ras.seek(0L);
        this.ras.setLength(0L);
    }

    @Override
    public void seek(long position) throws IOException {
        this.checkClosed();
        this.ras.seek(position);
    }

    @Override
    public long getPosition() throws IOException {
        this.checkClosed();
        return this.ras.getFilePointer();
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        return this.ras.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        this.checkClosed();
        return this.ras.read(b);
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        this.checkClosed();
        return this.ras.read(b, offset, length);
    }

    @Override
    public long length() throws IOException {
        this.checkClosed();
        return this.ras.length();
    }

    private void checkClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("RandomAccessFile already closed");
        }
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        this.checkClosed();
        this.ras.write(b, offset, length);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(int b) throws IOException {
        this.checkClosed();
        this.ras.write(b);
    }

    @Override
    public int peek() throws IOException {
        int result = this.read();
        if (result != -1) {
            this.rewind(1);
        }
        return result;
    }

    @Override
    public void rewind(int bytes) throws IOException {
        this.checkClosed();
        this.ras.seek(this.ras.getFilePointer() - (long)bytes);
    }

    @Override
    public byte[] readFully(int length) throws IOException {
        this.checkClosed();
        byte[] b = new byte[length];
        this.ras.readFully(b);
        return b;
    }

    @Override
    public boolean isEOF() throws IOException {
        return this.peek() == -1;
    }

    @Override
    public int available() throws IOException {
        this.checkClosed();
        return (int)Math.min(this.ras.length() - this.getPosition(), Integer.MAX_VALUE);
    }
}

