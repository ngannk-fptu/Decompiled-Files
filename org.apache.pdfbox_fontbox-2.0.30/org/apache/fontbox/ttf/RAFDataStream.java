/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.fontbox.ttf.BufferedRandomAccessFile;
import org.apache.fontbox.ttf.TTFDataStream;

class RAFDataStream
extends TTFDataStream {
    private RandomAccessFile raf = null;
    private File ttfFile = null;
    private static final int BUFFERSIZE = 16384;

    RAFDataStream(String name, String mode) throws IOException {
        this(new File(name), mode);
    }

    RAFDataStream(File file, String mode) throws IOException {
        this.raf = new BufferedRandomAccessFile(file, mode, 16384);
        this.ttfFile = file;
    }

    @Override
    public short readSignedShort() throws IOException {
        return this.raf.readShort();
    }

    @Override
    public long getCurrentPosition() throws IOException {
        return this.raf.getFilePointer();
    }

    @Override
    public void close() throws IOException {
        if (this.raf != null) {
            this.raf.close();
            this.raf = null;
        }
    }

    @Override
    public int read() throws IOException {
        return this.raf.read();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.raf.readUnsignedShort();
    }

    @Override
    public long readLong() throws IOException {
        return this.raf.readLong();
    }

    @Override
    public void seek(long pos) throws IOException {
        this.raf.seek(pos);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.raf.read(b, off, len);
    }

    @Override
    public InputStream getOriginalData() throws IOException {
        return new FileInputStream(this.ttfFile);
    }

    @Override
    public long getOriginalDataSize() {
        return this.ttfFile.length();
    }
}

